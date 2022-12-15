import java.util.Scanner;
/**
 * @Description TODO: Client的主类
 * @Author root
 * @Date 2022/12/09 15:56
 * @Version 1.0
 **/
public class ClientMain {
    /**
     * @Description TODO: 向服务器发送query请求（非备份）
     * @return
     * @param numWithYear
     * @param name
     * @param beginYear
     * @param endYear
     * @Author root
     * @Date 2022/12/15 17:19
     * @Version 1.0
     **/
    public static void sendQuerys(int[] numWithYear,String name,String beginYear,String endYear,boolean useIndex){
        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                numWithYear[0] = AccessServer.sendQuery(name, beginYear, endYear,0,0,false,useIndex);
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            public void run() {
                numWithYear[1] = AccessServer.sendQuery(name, beginYear, endYear,0,1,false,useIndex);
            }
        });
        Thread thread3 = new Thread(new Runnable() {
            public void run() {
                numWithYear[2] = AccessServer.sendQuery(name, beginYear, endYear,1,0,false,useIndex);
            }
        });
        Thread thread4 = new Thread(new Runnable() {
            public void run() {
                numWithYear[3] = AccessServer.sendQuery(name, beginYear, endYear,1,1,false,useIndex);
            }
        });
        Thread thread5 = new Thread(new Runnable() {
            public void run() {
                numWithYear[4] = AccessServer.sendQuery(name, beginYear, endYear,2,0,false,useIndex);
            }
        });
        Thread thread6 = new Thread(new Runnable() {
            public void run() {
                numWithYear[5] = AccessServer.sendQuery(name, beginYear, endYear,2,1,false,useIndex);
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
    }
    /**
     * @Description TODO: 向服务器发送query请求（备份）
     * @return
     * @param numWithYear
     * @param name
     * @param beginYear
     * @param endYear
     * @Author root
     * @Date 2022/12/15 17:19
     * @Version 1.0
     **/
    public static void sendBackupQuerys(int[] numWithYear,String name,String beginYear,String endYear,boolean useIndex){

        // 创建查询备份的线程
        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                numWithYear[0] = AccessServer.sendQuery(name, beginYear, endYear,1,0,true,useIndex);
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            public void run() {
                numWithYear[1] = AccessServer.sendQuery(name, beginYear, endYear,1,1,true,useIndex);
            }
        });
        Thread thread3 = new Thread(new Runnable() {
            public void run() {
                numWithYear[2] = AccessServer.sendQuery(name, beginYear, endYear,2,0,true,useIndex);
            }
        });
        Thread thread4 = new Thread(new Runnable() {
            public void run() {
                numWithYear[3] = AccessServer.sendQuery(name, beginYear, endYear,2,1,true,useIndex);
            }
        });
        Thread thread5 = new Thread(new Runnable() {
            public void run() {
                numWithYear[4] = AccessServer.sendQuery(name, beginYear, endYear,0,0,true,useIndex);
            }
        });
        Thread thread6 = new Thread(new Runnable() {
            public void run() {
                numWithYear[5] = AccessServer.sendQuery(name, beginYear, endYear,0,1,true,useIndex);
            }
        });

        // 启动Send线程
        if(numWithYear[0]==-1) {
            System.out.println("虚拟机1故障，查询备份");
            thread1.start();
        }
        if(numWithYear[1]==-1){
            System.out.println("虚拟机2故障，查询备份");
            thread2.start();
        }
        if(numWithYear[2]==-1) {
            System.out.println("虚拟机3故障，查询备份");
            thread3.start();
        }
        if(numWithYear[3]==-1){
            System.out.println("虚拟机4故障，查询备份");
            thread4.start();
        }
        if(numWithYear[4]==-1) {
            System.out.println("虚拟机5故障，查询备份");
            thread5.start();
        }
        if(numWithYear[5]==-1) {
            System.out.println("虚拟机6故障，查询备份");
            thread6.start();
        }

        // 等待线程进行
        try {
            if(numWithYear[0]==-1)
                thread1.join();
            if(numWithYear[1]==-1)
                thread2.join();
            if(numWithYear[2]==-1)
                thread3.join();
            if(numWithYear[3]==-1)
                thread4.join();
            if(numWithYear[4]==-1)
                thread5.join();
            if(numWithYear[5]==-1)
                thread6.join();
        } catch (Exception e) {
            System.out.println("backup thread error");
        }
    }
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

            System.out.println("正在查询（未使用本地索引）.....");
            //创建计时
            long startTime = System.currentTimeMillis();

            // 调用Query.queryByNameAndYear进行查询
            // 记录每台虚拟机的查询结果：-2为初始值，-1为连接失败
            int[] numWithYear = {-2, -2, -2, -2, -2, -2};
            int numAll = 0;

            // 向各个服务器发送查询请求
            sendQuerys(numWithYear,name,beginYear,endYear,false);

            // 存在numWithYear==-1，则对应的服务器发生宕机
            if(numWithYear[0]==-1||numWithYear[1]==-1||numWithYear[2]==-1||numWithYear[3]==-1||numWithYear[4]==-1||numWithYear[5]==-1){
                sendBackupQuerys(numWithYear,name,beginYear,endYear,false);
            }

            // 总的论文频次数
            for(int i=0;i<6;i++){
                if(numWithYear[i]>0){
                    numAll+=numWithYear[i];
                }
            }
            
            //输出用时
            long endTime = System.currentTimeMillis();
            System.out.println("查询成功（未使用本地索引）! 用时：" + (double) (endTime - startTime) / 1000 + "s");
            //输出查询结果
            //System.out.println("没有年份限制时，"+name+"的DBLP发表论文总数为：" + numWithoutYear);
            System.out.println("有年份限制时，" + name + "的DBLP发表论文总数为：" + numAll);

            System.out.println("正在查询（使用本地索引）.....");
            //创建计时
            startTime = System.currentTimeMillis();

            // 调用Query.queryByNameAndYear进行查询
            // 记录每台虚拟机的查询结果：-2为初始值，-1为连接失败
            for (int num:numWithYear) {
                num=-2;
            }
            numAll = 0;

            // 向各个服务器发送查询请求
            sendQuerys(numWithYear,name,beginYear,endYear,true);

            // 存在numWithYear==-1，则对应的服务器发生宕机
            if(numWithYear[0]==-1||numWithYear[1]==-1||numWithYear[2]==-1||numWithYear[3]==-1||numWithYear[4]==-1||numWithYear[5]==-1){
                sendBackupQuerys(numWithYear,name,beginYear,endYear,true);
            }

            // 总的论文频次数
            for(int i=0;i<6;i++){
                if(numWithYear[i]>0){
                    numAll+=numWithYear[i];
                }
            }

            //输出用时
            endTime = System.currentTimeMillis();
            System.out.println("查询成功（使用本地索引）! 用时：" + (double) (endTime - startTime) / 1000 + "s");
            //输出查询结果
            //System.out.println("没有年份限制时，"+name+"的DBLP发表论文总数为：" + numWithoutYear);
            System.out.println("有年份限制时，" + name + "的DBLP发表论文总数为：" + numAll);
        }
    }
}