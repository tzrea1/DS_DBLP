import java.io.*;
import java.net.Socket;
public class AccessServer {
    private final static String[] ipList = new String[]
            {"1.15.143.17", "212.129.245.31", "101.35.155.147"};
    private final static int[] portList = new int[]
            {8820, 8821, 8822};
    public static int sendQuery(String name, String beginYear, String endYear,int ipSelected,int portSelected){
        int num;
        try {
            //创建Socket链接
            Socket socket = new Socket(ipList[ipSelected], portList[portSelected]);
            DataInputStream is = new DataInputStream(socket.getInputStream());
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            //向Server传递name信息
            os.writeUTF(name);
            os.flush();
            //向Server传递beginYear信息
            os.writeUTF(beginYear);
            os.flush();
            //向Server传递endYear信息
            os.writeUTF(endYear);
            os.flush();

            //接收服务端的查询信息
            String queryResult = is.readUTF();
            num = Integer.parseInt(queryResult);//查询到的次数

            //关闭Socket链接
            is.close();
            os.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return num;
    }
}
