import java.util.Scanner;
/**
 * @Description TODO: Client的主类
 * @Author root
 * @Date 2022/12/09 15:56
 * @Version 1.0
 **/
public class ClientMain {
    /**
     * @Description TODO: Client的主函数，调用其他类，运行Client的逻辑
     * @return
     * @param args
     * @Author root
     * @Date 2022/12/09 15:56
     * @Version 1.0
     **/
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
            System.out.println("正在查询.....");
            //创建计时
            long startTime = System.currentTimeMillis();
            //调用Query.queryByName进行查询
            //后两位选择ip，端口
            int numWithoutYear = AccessServer.sendQuery(name, "*", "*",0,0);
            int numWithYear = AccessServer.sendQuery(name, beginYear, endYear,0,0);
            if(numWithoutYear==-1)
                System.out.println("连接出错！");
            else {
                //输出用时
                long endTime = System.currentTimeMillis();
                System.out.println("查询成功! 用时："+ (double) (endTime - startTime) / 1000 + "s");
                //输出查询结果
                System.out.println("没有年份限制时，"+name+"的DBLP发表论文总数为：" + numWithoutYear);
                System.out.println("有年份限制时，"+name+"的DBLP发表论文总数为：" + numWithYear);
                //System.out.println("有年份限制时，成功查询次数为："+num);
            }
        }
    }
}