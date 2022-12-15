import java.io.*;
import java.net.Socket;
/**
 * @Description TODO: Client访问Server时使用此类
 * @Author root
 * @Date 2022/12/09 15:52
 * @Version 1.0
 **/
public class AccessServer {
    /**
    * ip列表，记录可以访问的Server的ip地址
    */
    private final static String[] ipList = new String[]
            { "212.129.245.31", "1.15.143.17","101.35.155.147"};
    /**
    * port列表，记录可以访问的Server的端口
    */
    private final static int[] portList = new int[]
            {8920, 8921, 8922};
    /**
     * @Description TODO: 向指定的Server发送查询信息（作者、年份），并获得查询结果
     * @return
     * @param name
     * @param beginYear
     * @param endYear
     * @param ipSelected
     * @param portSelected
     * @Author root
     * @Date 2022/12/09 15:55
     * @Version 1.0
     **/
    public static int sendQuery(String name, String beginYear, String endYear,int ipSelected,int portSelected,boolean isBackup){
        int num;
        try {
            //创建Socket链接
            Socket socket = new Socket(ipList[ipSelected], portList[portSelected]);
            DataInputStream is = new DataInputStream(socket.getInputStream());
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            //向Server传递isBackup是否要查询备份文件块的信息
            if(isBackup==true){
                os.writeUTF("true");
            }
            else{
                os.writeUTF("false");
            }
            os.flush();
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
