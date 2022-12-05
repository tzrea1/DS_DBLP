import java.io.BufferedReader;
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
                sb.append(line);
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
            //输入姓名，测试可用：Ion Stoica
            String input = sc.nextLine();
            //退出标识
            if(input.equals("exit"))
                return;
            //输入年份区间
            System.out.println("限定年份区间，请依次输入起始年份和截至年份");
            System.out.println("若不存在起始/截至年份，则输入*");
            System.out.println("请输入起始年份:");
            String beginYear=sc.nextLine();//起始年份
            System.out.println("请输入截至年份:");
            String endYear=sc.nextLine();//截至年份
            System.out.println("限定的年份区间为:"+beginYear+" - "+endYear);

            String command="grep -wo \"" +input+ "\" " +DBLP_Path+ " |wc -l"; //按作者名查询，非模糊搜索
            String result = exeCmd(command);//命令执行结果
            int num = Integer.parseInt(result);//查询到的次数
            System.out.println("没有年份限制时，成功查询次数为："+num);
            //System.out.println("有年份限制时，成功查询次数为："+num);
        }
    }
}