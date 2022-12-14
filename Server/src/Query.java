import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.*;
import java.util.*;
import javax.xml.stream.*;
import java.io.File;
/**
 * @Description TODO: 查询功能类，包含：1.按姓名查询 2.按姓名和年份查询 两种查询功能。
 * @Author root
 * @Date 2022/12/09 16:00
 * @Version 1.0
 **/
public class Query {
    // 当前虚拟机端口
    private static int port;
    // dblp.xml正式块路径
    private static String DBLP_Path;
    // dblp.xml备份块路径
    private static String DBLP_Backup_Path;
    // 当前虚拟机下存储的文件块
    private static ArrayList<String> dblpNames;
    private static ArrayList<String> dblpBackupNames;
    /**
     * @Description TODO: Query的构造函数
     * @return 
     * @param portSelected 
     * @Author root
     * @Date 2022/12/11 17:35 
     * @Version 1.0
     **/
    Query(int portSelected){
        port=portSelected-100;
        DBLP_Path = "/mnt/dblpXmls/"+port;
        DBLP_Backup_Path = "/mnt/dblpBackupXmls/"+port;
        // 获取正式dblp文件块的名称
        File dir = new File(DBLP_Path);

        File[] xmlFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });

        // 获取xml文件的名称
        for (File xmlFile : xmlFiles) {
            dblpNames.add(xmlFile.getName());
        }

        // 获取备份dblp文件块的名称
        dir = new File(DBLP_Backup_Path);

        File[] xmlFilesBackup = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });

        // 获取xml文件的名称
        for (File xmlFile : xmlFilesBackup) {
            dblpBackupNames.add(xmlFile.getName());
        }
    }
    /**
     * @Description TODO: 开启终端，执行传入的命令行，获得执行结果
     * @return
     * @param commandStr
     * @Author root
     * @Date 2022/12/09 16:00
     * @Version 1.0
     **/
    public static String exeCmd(String commandStr) {
        //执行Linux的Cmd命令
        String result = null;
        try {
            String[] cmd = new String[]{"/bin/sh", "-c", commandStr};
            Process ps = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                //执行结果加上回车
                sb.append(line);
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * @Description TODO: 仅按照作者名进行查询
     * @return
     * @param name
     * @Author root
     * @Date 2022/12/09 17:28
     * @Version 1.0
     **/
    public static String queryByName(String name) {
        // 记录频次
        int num=0;
        for(int i=0;i<dblpNames.size();i++) {
            //根据姓名进行查询
            String command = "grep -wo \"" + name + "\" " + DBLP_Path +"/"+ dblpNames.get(i) +" |wc -l"; //按作者名查询，非模糊搜索
            String result = exeCmd(command);//命令执行结果
            num+=Integer.parseInt(result);
        }
        return String.valueOf(num);
    }
    /**
     * 判断输入的年份是否在指定的范围内，并返回一个布尔值表示是否在指定范围内.
     * @param year 要判断的年份
     * @param beginYear 起始年份
     * @param endYear 结束年份
     * @return 是否在指定范围内
     */
    public static boolean checkYearInRange(String year, String beginYear, String endYear) {
        if(year==null){
            return false;
        }
        if(beginYear.equals("*") && endYear.equals("*")) {
            return true;
        } else if(beginYear.equals("*")) {
            if(Integer.parseInt(year) <= Integer.parseInt(endYear)) {
                return true;
            } else {
                return false;
            }
        } else if(endYear.equals("*")) {
            if(Integer.parseInt(year) >= Integer.parseInt(beginYear)) {
                return true;
            } else {
                return false;
            }
        } else {
            if(Integer.parseInt(year) >= Integer.parseInt(beginYear)
                    && Integer.parseInt(year) <= Integer.parseInt(endYear)) {
                return true;
            } else {
                return false;
            }
        }
    }
    /**
     * @Description TODO: 按照[作者名]和[年份限制],对指定的dblp块进行依次查询
     * @return 查询结果：次数
     * @param name
     * @param beginYear
     * @param endYear
     * @Author root
     * @Date 2022/12/09 17:30
     * @Version 1.0
     **/
    public static String queryBlockByNameAndYear(String name,String beginYear,String endYear,String dblpBlockPath) throws FileNotFoundException {
        try {
            // 创建一个 XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // 创建一个 XMLStreamReader
            System.out.println("Reading file"+dblpBlockPath);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new FileReader(dblpBlockPath));
            //创建一个字符串集合，包含DBLP数据库中所有可能的文章类型
            Set<String> typeSet = new HashSet<>(Arrays.asList(
                    "article",
                    "inproceedings",
                    "proceedings",
                    "book",
                    "incollection",
                    "phdthesis",
                    "mastersthesis",
                    "www",
                    "person",
                    "data"));
            // 用于记录匹配的块的计数器
            int matchedCounter = 0;
            // 用于记录当前读取的块的信息
            String currentAuthor = null;
            String currentYear = null;
            boolean hasAuthor = false;
            // 创建一个栈，用于记录当前读取到的所有元素的名称
            Stack<String> elementStack = new Stack<String>();
            // 开始读取 XML 文档
            while (reader.hasNext()) {
                int event = reader.next();
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        // 如果是某个块的开头，则重置块信息
                        if (typeSet.contains(reader.getLocalName())) {
                            currentAuthor = null;
                            currentYear = null;
                            hasAuthor = false;
                        }
                        // 将元素名称压入栈
                        elementStack.push(reader.getLocalName());
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        // 如果是某个块的结尾，则检查块信息
                        if (typeSet.contains(reader.getLocalName())) {
                            if (hasAuthor && checkYearInRange(currentYear, beginYear, endYear)) {
                                // 如果块信息满足条件，则更新匹配计数器
                                matchedCounter++;
                            }
                            // 重置块信息
                            currentAuthor = null;
                            currentYear = null;
                            hasAuthor = false;
                        }
                        // 将元素名称弹出栈
                        elementStack.pop();
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        // 如果是某个块的文本内容，则更新块
                        if ("author".equals(elementStack.peek())) {
                            currentAuthor = reader.getText();
                            if (name.equals(currentAuthor)) {
                                hasAuthor = true;
                            }
                        } else if ("year".equals(elementStack.peek())) {
                            currentYear = reader.getText();
                        }
                        break;
                }
            }
            // 关闭 XMLStreamReader
            reader.close();
            // 输出匹配的块的数量
            System.out.println(matchedCounter);
            //次数转为字符串
            String result = String.valueOf(matchedCounter);
            System.out.println("Finished file"+dblpBlockPath);
            return result;
        }
        catch (FileNotFoundException | XMLStreamException ex)
        {
            return null;
        }
    }
    /**
     * @Description TODO: 按照姓名、年份限制对本虚拟机下存储的所有dblp块进行查询
     * @return
     * @param name
     * @param beginYear
     * @param endYear
     * @Author root
     * @Date 2022/12/11 18:03
     * @Version 1.0
     **/
    public static String queryByNameAndYear(String name,String beginYear,String endYear){
        // 记录频次
        int num=0;
        for(int i=0;i<dblpNames.size();i++) {
            String result = null;
            try {
                // 得到某一块的查询结果
                result = queryBlockByNameAndYear(name,beginYear,endYear,DBLP_Path +"/"+dblpNames.get(i));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            // 累加频次
            num+=Integer.parseInt(result);
        }
        return String.valueOf(num);
    }
}
