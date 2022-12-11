import java.io.*;
import javax.xml.stream.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

public class Initialize {
    private static String DBLP_Path = "/mnt/dblpXmls";
    private static String DBLP_Backup_Path = "/mnt/dblpBackupXmls";

    public static void receiveXml(int portSelected) throws Exception{
        try {
            // 创建ServerSocket对象
            ServerSocket serverSocket = new ServerSocket(portSelected);

            while (true) {
                // 等待客户端连接
                Socket socket = serverSocket.accept();

                // 创建输入流
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                // 接收文件名信息
                String fileName = inputStream.readUTF();
                System.out.println("Recieved"+fileName);

                // 接收备份标识
                String backupTag = inputStream.readUTF();
                System.out.println("Recieved"+backupTag);

                // 文件路径
                String filePath;
                // 传输过来的是备份文件
                if (backupTag.equals("isBackup")) {
                    filePath = DBLP_Backup_Path + "/" + String.valueOf(portSelected) + "/" + fileName;
                }
                // 传输过来的不是备份文件
                else {
                    filePath = DBLP_Path + "/" + String.valueOf(portSelected) + "/" + fileName;
                }
                System.out.println("写入至："+filePath);
                // 创建文件输出流
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);

                // 读取输入流中的数据并写入文件
                byte[] buffer = new byte[1024];
                int bytesRead=0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                fileOutputStream.close();
                // 关闭文件输出流和Socket输入流
                fileOutputStream.close();
                inputStream.close();
                socket.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
