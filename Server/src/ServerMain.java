public class ServerMain {
    private final static int[] portList = new int[]
            {8820, 8821, 8822};
    public static void main(String[] args) {
        //创建虚拟机0
        VirtualServer s0=new VirtualServer(portList[0]);
        //创建虚拟机1
        VirtualServer s1=new VirtualServer(portList[1]);
        //创建虚拟机2
        VirtualServer s2=new VirtualServer(portList[2]);
        //创建虚拟机0线程
        Thread queryService0 = new Thread(new Runnable() {
            public void run() {
                s0.receiveQuery();
            }
        });
        //创建虚拟机1线程
        Thread queryService1 = new Thread(new Runnable() {
            public void run() {
                s1.receiveQuery();
            }
        });
        //创建虚拟机2线程
        Thread queryService2 = new Thread(new Runnable() {
            public void run() {
                s2.receiveQuery();
            }
        });
        //启动虚拟机0线程
        queryService0.start();
        //启动虚拟机1线程
        queryService1.start();
        //启动虚拟机2线程
        queryService2.start();
    }
}