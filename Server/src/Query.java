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
    private static String DBLP_Path = "/root/Desktop/DS_Code/DS_DBLP/Server/dblp.xml"; //dblp.xml路径
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
        //根据姓名进行查询
        String command = "grep -wo \"" + name + "\" " + DBLP_Path + " |wc -l"; //按作者名查询，非模糊搜索
        String result = exeCmd(command);//命令执行结果
        return result;
    }
    /**
     * 判断输入的年份是否在指定的范围内，并返回一个布尔值表示是否在指定范围内.
     * @param year 要判断的年份
     * @param beginYear 起始年份
     * @param endYear 结束年份
     * @return 是否在指定范围内
     */
    public static boolean checkYearInRange(String year, String beginYear, String endYear) {
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
     * @Description TODO: 按照[作者名]和[年份限制]进行查询
     * @return 查询结果：次数
     * @param name
     * @param beginYear
     * @param endYear
     * @Author root
     * @Date 2022/12/09 17:30
     * @Version 1.0
     **/
    public static String queryByNameAndYear(String name,String beginYear,String endYear) throws FileNotFoundException {
        try {
            // 创建一个 XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // 创建一个 XMLStreamReader
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new FileReader(DBLP_Path));
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
            return result;
        }
        catch (FileNotFoundException | XMLStreamException ex)
        {
            return null;
        }
    }

}
