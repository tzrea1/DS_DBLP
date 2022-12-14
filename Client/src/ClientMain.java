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
        while(true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("是否要初始化DBLP分布式存储: 输入yes/no");
            String str=sc.nextLine();
            if(str.equals("yes")){
                try {
                    // 执行初始化流程
                    Initialize.initDBLP();
                    System.out.println("DBLP分布式存储初始化完成");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            else if(str.equals(("no"))){
                break;
            }
            else{
                System.out.println("输入不合法，请重新输入");
            }
        }
        while(true) {
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
            // 调用Query.queryByName进行查询
//            int numWithoutYear=-1;
//            // 访问每一台虚拟机进行查询
//            for(int i=0;i<3;i++){
//                for(int j=0;j<2;j++) {
//                    numWithoutYear += AccessServer.sendQuery(name, "*", "*", i, j);
//                }
//            }
            // 调用Query.queryByNameAndYear进行查询
            final int[] numWithYear = {-2, -2, -2, -2, -2, -2};
            int numAll = 0;
//            // 访问每一台虚拟机进行查询
//            for (int i = 0; i < 3; i++) {
//                for (int j = 0; j < 2; j++) {
//                    numAll += AccessServer.sendQuery(name, beginYear, endYear, i, j);
//                }
//            }
            Thread thread1 = new Thread(new Runnable() {
                public void run() {
                    numWithYear[0] = AccessServer.sendQuery(name, beginYear, endYear,0,0);
                }
            });
            Thread thread2 = new Thread(new Runnable() {
                public void run() {
                    numWithYear[1] = AccessServer.sendQuery(name, beginYear, endYear,0,1);
                }
            });
            Thread thread3 = new Thread(new Runnable() {
                public void run() {
                    numWithYear[2] = AccessServer.sendQuery(name, beginYear, endYear,1,0);
                }
            });
            Thread thread4 = new Thread(new Runnable() {
                public void run() {
                    numWithYear[3] = AccessServer.sendQuery(name, beginYear, endYear,1,1);
                }
            });
            Thread thread5 = new Thread(new Runnable() {
                public void run() {
                    numWithYear[4] = AccessServer.sendQuery(name, beginYear, endYear,2,0);
                }
            });
            Thread thread6 = new Thread(new Runnable() {
                public void run() {
                    numWithYear[5] = AccessServer.sendQuery(name, beginYear, endYear,2,1);
                }
            });
            // 启动Send线程
            thread1.start();
            thread2.start();
            thread3.start();
            thread4.start();
            thread5.start();
            thread6.start();

            // 等待线程进行
            try {
                thread1.join();
                thread2.join();
                thread3.join();
                thread4.join();
                thread5.join();
                thread6.join();
            } catch (Exception e) {
                System.out.println("thread error");
            }

            // 总的论文频次数
            numAll=numWithYear[0]+numWithYear[1]+numWithYear[2]+numWithYear[3]+numWithYear[4]+numWithYear[5];
            //输出用时
            long endTime = System.currentTimeMillis();
            System.out.println("查询成功! 用时：" + (double) (endTime - startTime) / 1000 + "s");
            //输出查询结果
            //System.out.println("没有年份限制时，"+name+"的DBLP发表论文总数为：" + numWithoutYear);
            System.out.println("有年份限制时，" + name + "的DBLP发表论文总数为：" + numAll);
        }
    }
}