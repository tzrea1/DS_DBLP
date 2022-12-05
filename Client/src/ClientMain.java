import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        while(true){
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入作者姓名:");
            //输入姓名，测试可用：Ion Stoica
            String name = sc.nextLine();
            //退出标识:exit
            if (name.equals("exit"))
                return;
            //输入年份区间
            System.out.println("限定年份区间，请依次输入起始年份和截至年份");
            System.out.println("若不存在起始/截至年份，则输入*");
            System.out.println("请输入起始年份:");
            String beginYear = sc.nextLine();//起始年份
            System.out.println("请输入截至年份:");
            String endYear = sc.nextLine();//截至年份
            System.out.println("限定的年份区间为:" + beginYear + " - " + endYear);
            //调用Query.queryByName进行查询
            int num=0;//查询到的次数
            //后两位选择ip，端口
            num = AccessServer.sendQuery(name, beginYear, endYear,0,0);
            if(num==-1)
                System.out.println("连接出错！");
            else
                System.out.println("没有年份限制时，成功查询次数为：" + num);
            //System.out.println("有年份限制时，成功查询次数为："+num);
        }
    }
}