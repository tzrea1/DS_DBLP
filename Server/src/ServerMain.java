import java.util.Scanner;
public class ServerMain {
    private final static String[] ipList = new String[]
            {"1.15.143.17", "212.129.245.31", "101.35.155.147"};
    private final static int[] portList = new int[]
            {8820, 8821, 8822};
    /**
     * @Description TODO: 执行初始化流程，包括切分xml和将xml发送给各个虚拟机
     * @return
     * @Author root
     * @Date 2022/12/11 18:14
     * @Version 1.0
     **/
    public static void initDBLP(){
        try {
            // 首先切分xml文件
            Initialize.SplitXml();
            // 将切分好的xml文件发送给各个虚拟机
            for(int i=0;i<24;i++) {
                String fileName="dblp"+i+".xml";
                System.out.println("发送"+fileName+"文件");
                // 虚拟机分配顺序：服务器0-端口0，服务器0-端口1，服务器1-端口0....
                // ip序号
                int ipSelect=i/2;
                // 备份文件的ip序号
                int ipBackupSelect=ipSelect%2;
                // port序号
                int portSelect=i%2;
                // 发送xml文件（正式版本）
                Initialize.sendXml(fileName, ipList[ipSelect], portList[portSelect], false);
                // 发送xml文件（备份版本）：规则为ip加1
                Initialize.sendXml(fileName, ipList[ipBackupSelect], portList[portSelect], true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int portSelected=-1;
        int port=portList[portSelected];
        while (portSelected==-1){
            System.out.println("请输入0/1/2选择虚拟机端口：0--8820, 1--8821, 2--8822");
            //接收输入的端口号
            String portStr = sc.nextLine();
            portSelected=Integer.parseInt(portStr);
            if((portSelected!=0)&&(portSelected!=1)&&(portSelected!=2)){
                portSelected=-1;
                System.out.println("请重新输入");
            }
        }
        // 创建查询虚拟机线程: portSelected
        VirtualServer vs=new VirtualServer(port);
        // 查询虚拟机线程
        Thread queryThread = new Thread(new Runnable() {
            public void run() {
                vs.receiveQuery();
            }
        });
        // 启动查询虚拟机线程
        queryThread.start();
        System.out.println("虚拟机已启动");

        // 创建文件接收线程
        Initialize in=new Initialize();
        // 文件接收线程
        Thread receiveThread = new Thread(new Runnable() {
            public void run() {
                try {
                    in.receiveXml(port);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        while(true) {
            System.out.println("是否要初始化DBLP分布式存储: 输入yes/no");
            String str=sc.nextLine();
            if(str.equals("yes")){
                try {
                    // 执行初始化流程
                    initDBLP();
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
    }
}