import localindex.IndexQuery;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class VirtualServer {
    private int port;
    // 每台服务器应该存放的xml文件数量
    private final int xmlProperNum=4;
    public VirtualServer(int portID){
        this.port=portID;
        //在这里顺便初始化组服务
    }
    public void receiveQuery() {
        try {
            ServerSocket server = new ServerSocket(port);
            while (true) {
                Socket socket = server.accept();
                DataInputStream is = new DataInputStream(socket.getInputStream());
                DataOutputStream os = new DataOutputStream(socket.getOutputStream());

                //接收来自客户端的是否查询备份的信息
                String isBackup = "";
                isBackup = is.readUTF();
                //System.out.println("Recieved " + isBackup);
                //接收来自客户端的name信息
                String name = "";
                name = is.readUTF();
                //System.out.println("Recieved " + name);
                //接收来自客户端的beginYear信息
                String beginYear = "";
                beginYear = is.readUTF();
                //System.out.println("Recieved " + beginYear);
                //接收来自客户端的endYear信息
                String endYear = "";
                endYear = is.readUTF();
                //System.out.println("Recieved " + endYear);
                //接收来自客户端的useIndex信息
                String useIndex = "";
                useIndex = is.readUTF();
                //System.out.println("Recieved " + useIndex);

                // 创建Query实例
                Query query=new Query(port);
                //确定接收到了来自客户端的信息
                if (name.length()>0) {
                    if(useIndex.equals("true")){
                        boolean isCopy=true;
                        if(isBackup.equals("false")){
                            isCopy=false;
                        }
                        String queryResult = IndexQuery.queryByIndex(isCopy,port-100,name,beginYear,endYear);
                        System.out.println("queryByIndex final result: " + queryResult);
                        os.writeUTF(queryResult);
                        os.flush();
                    }
                    else {
//                        if (beginYear.equals("*") && endYear.equals("*")) {
//                            //向客户端发送查询结果信息（无年份限制）
//                            String queryResult = query.queryByName(name, isBackup);
//                            System.out.println("Result: " + queryResult);
//                            os.writeUTF(queryResult);
//                            os.flush();
//                        }
                        //向客户端发送查询结果信息
                        String queryResult = query.queryByNameAndYear(name, beginYear, endYear, isBackup);
                        System.out.println("queryByNameAndYea final result: " + queryResult);
                        os.writeUTF(queryResult);
                        os.flush();
                    }
                }

//                // 返回本地存储信息
//                InetAddress inetAddress = InetAddress.getLocalHost();
//                String ipAddress = inetAddress.getHostAddress();
//                // 发送虚拟机ip地址
//                os.writeUTF(ipAddress);
//                // 发送虚拟机端口
//                os.writeUTF(Integer.toString(port));
//                // 发送当前虚拟机存储的块数量
//                int xmlNum=query.getXmlNum();
//                os.writeUTF(Integer.toString(xmlNum));

                //关闭Socket链接
                is.close();
                os.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
