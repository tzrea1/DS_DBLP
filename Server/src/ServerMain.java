import java.util.Scanner;
public class ServerMain {
    private final static int[] portList = new int[]
            {8820, 8821, 8822};
    public static void main(String[] args) {
        int portSelected=-1;
        while (portSelected==-1){
            System.out.println("请输入0/1/2选择端口：0--8820, 1--8821, 2--8822");
            //接收输入的端口号
            Scanner sc = new Scanner(System.in);
            String portStr = sc.nextLine();
            portSelected=Integer.parseInt(portStr);
            if((portSelected!=0)&&(portSelected!=1)&&(portSelected!=2)){
                portSelected=-1;
                System.out.println("请重新输入");
            }
        }
        //创建虚拟机portSelected
        VirtualServer s0=new VirtualServer(portList[portSelected]);
//        //创建虚拟机1
//        VirtualServer s1=new VirtualServer(portList[1]);
//        //创建虚拟机2
//        VirtualServer s2=new VirtualServer(portList[2]);
        //创建虚拟机0线程
        Thread queryService0 = new Thread(new Runnable() {
            public void run() {
                s0.receiveQuery();
            }
        });
//        //创建虚拟机1线程
//        Thread queryService1 = new Thread(new Runnable() {
//            public void run() {
//                s1.receiveQuery();
//            }
//        });
//        //创建虚拟机2线程
//        Thread queryService2 = new Thread(new Runnable() {
//            public void run() {
//                s2.receiveQuery();
//            }
//        });
        //启动虚拟机0线程
        queryService0.start();
        System.out.println("虚拟机已启动");
//        //启动虚拟机1线程
//        queryService1.start();
//        //启动虚拟机2线程
//        queryService2.start();
    }
}