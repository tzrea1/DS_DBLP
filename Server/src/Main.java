import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
    public static String exeCmd(String commandStr) {
        //执行Linux的Cmd命令
        String result = null;
        try {
            String[] cmd = new String[]{"/bin/sh", "-c",commandStr};
            Process ps = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                //执行结果加上回车
                sb.append(line).append("\n");
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        String DBLP_Path="/root/Desktop/DS_Code/DS_DBLP/Server/dblp.xml"; //dblp.xml路径
        while(true){
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入作者姓名:");
            String input = sc.nextLine();  //读取字符串型输入
            if(input.equals("exit"))
                return;
            String command="grep -wo \"" +input+ "\" " +DBLP_Path+ " |wc -l"; //按作者名查询，非模糊搜索
            //System.out.println(command);
            String result = exeCmd(command);
            System.out.println("获取的结果是："+result);
        }
    }
}