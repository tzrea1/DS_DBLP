import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Daemon {
    /**
     * ip列表，记录可以访问的Server的ip地址
     */
    private final static String[] ipList = new String[]
            {"1.15.143.17", "212.129.245.31", "101.35.155.147"};
    /**
     * port列表，记录server的3个后台端口
     */
    private final static int[] portList = new int[]
            {9020, 9021, 9022};
    int portId;

    public Daemon(int portId) {
        this.portId = portId;
    }

    //add and test by mxy
    // 定义心跳消息
    public static String HEARTBEAT_MESSAGE = "I'm still alive";
    // 定义心跳频率（每隔10秒发送一次心跳）
    public int HEARTBEAT_INTERVAL = 10;
    // 定义组成员列表
    public List<String> memberList = new ArrayList<>();
    // 定义离线检查频率（每隔5秒检查一次）
    static final int OFFLINE_CHECK_INTERVAL = 5;
    // 定义离线超时时间（如果某个节点超过30秒没有发送心跳消息，则认为该节点已经离线）
    static final int OFFLINE_TIMEOUT = 30;
    // 定义节点的最后心跳时间映射
    static final Map<String, Long> lastHeartbeatMap = new HashMap<>();

    //开始后台进程
    public void startDaemon() {
        try {
            // 创建ServerSocket实例
            ServerSocket serverSocket = new ServerSocket(portId);
            // 启动离线检查线程
            new OfflineCheckThread(this).start();

            // 循环接收连接请求
            while (true) {
                // 接收连接请求
                Socket socket = serverSocket.accept();

                // 启动消息处理线程
                new MessageHandlerThread(socket,this).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class MessageHandlerThread extends Thread {
    private Socket socket;
    private Daemon daemon;

    public MessageHandlerThread(Socket socket,Daemon daemon) {
        this.socket = socket;
        this.daemon=daemon;
    }

    @Override
    public void run() {
        try {
            // 获取输入流
            DataInputStream is = new DataInputStream(socket.getInputStream());

            // 循环读取消息
            while (true) {
                // 读取消息
                String message = is.readUTF();

                // 如果收到心跳消息
                if (Daemon.HEARTBEAT_MESSAGE.equals(message)) {
                    // 更新组成员列表
                    updateMemberListandTime(socket,daemon);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateMemberListandTime(Socket socket,Daemon daemon) {
        // 获取连接的主机地址
        String host = socket.getInetAddress().getHostAddress();

        // 如果该主机不在组成员列表中，则将其添加到组成员列表中
        if (!daemon.memberList.contains(host)) {
            daemon.memberList.add(host);
        }
        // 更新客户端的最后心跳时间
        daemon.lastHeartbeatMap.put(host, System.currentTimeMillis());
    }
}

//离线检查类
class OfflineCheckThread extends Thread {
    private Daemon daemon;

    public OfflineCheckThread(Daemon daemon) {
        this.daemon=daemon;
    }
    public void run() {
        while (true) {
            try {
                  // 获取当前时间
                 long currentTime = System.currentTimeMillis();
                // 遍历组成员列表
                for (String host : daemon.memberList) {
                    // 获取节点的最后心跳时间
                    long lastHeartbeatTime = daemon.lastHeartbeatMap.get(host);

                    // 如果节点已经超过离线超时时间没有发送心跳消息，则认为该节点已经离线
                    if (currentTime - lastHeartbeatTime > daemon.OFFLINE_TIMEOUT) {
                        // 从组成员列表中删除该节点
                        daemon.memberList.remove(host);
                    }
                }

                // 等待一段时间
                Thread.sleep(daemon.OFFLINE_CHECK_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}