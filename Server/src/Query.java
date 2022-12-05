import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Query {
    static String DBLP_Path = "/root/Desktop/DS_Code/DS_DBLP/Server/dblp.xml"; //dblp.xml路径

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

    public static int queryByName(String name, String beginYear, String endYear) {
        //根据姓名进行查询
        String command = "grep -wo \"" + name + "\" " + DBLP_Path + " |wc -l"; //按作者名查询，非模糊搜索
        String result = exeCmd(command);//命令执行结果
        int num = Integer.parseInt(result);//查询到的次数
        return num;
    }
}
