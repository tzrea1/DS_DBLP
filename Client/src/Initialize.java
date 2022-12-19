import javax.xml.stream.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class Initialize {
    private final static String[] ipList = new String[]
            { "212.129.245.31", "1.15.143.17","101.35.155.147"};
    private final static int[] portList = new int[]
            {8820, 8821, 8822};
    /**
     * @Description TODO: 切分 DBLP.xml
     * @return
     * @Author root
     * @Date 2022/12/11 14:35
     * @Version 1.0
     **/
    public static void SplitXml() throws Exception {
        //按块拆分的大类标签
        Set set = new HashSet();
        set.add("article");
        set.add("book");
        set.add("inproceedings");
        set.add("proceedings");
        set.add("incollection");
        set.add("phdthesis");
        set.add("mastersthesis");
        set.add("www");
        set.add("data");

        //输入输出路径
        String inputFile = "/mnt/dblp.xml";
        String outputDir = "/mnt/splitedXmls";

        // 创建一个 XMLInputFactory
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        // 创建一个 XMLStreamReader
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new FileReader(inputFile));

        // 用于记录当前片段的文件名
        String currentFile = null;

        //当前使用的writer流
        XMLStreamWriter currentWriter=null;
        //存储24个writer文件流
        List<XMLStreamWriter> list = new ArrayList<>();

        //随机数，用于随机分配
        Random random1=new Random(10);
        // 开始读取 XML 文档
        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    // 如果是dblp标签开始，则创建n个文件流，准备向n个文件输出内容
                    if ("dblp".equals(reader.getLocalName())) {
                        System.out.println("正在切分DBLP.xml文件");
                        for(int i=0;i<24;i++){
                            currentFile = outputDir + "/dblp" + i + ".xml";
                            // 创建 XMLStreamWriter
                            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
                            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(new FileWriter(currentFile));
                            list.add(writer);
                            // 写入片段的开头
                            list.get(i).writeStartDocument();
                            list.get(i).writeStartElement("dblp");
                        }
                        //dblp标签后还有一个回车，会进入characters的分支，为防止currentWriter指针空，暂且将其设置为第一个输出
                        currentWriter=list.get(0);
                    }
                    else if(set.contains(reader.getLocalName())){
                        //设置当前流为随机的一个writer流，目的是以大的标签块为单位随机分配给拆分的xml文件中
                        currentWriter=list.get(random1.nextInt(list.size()));
                        currentWriter.writeStartElement(reader.getLocalName());
                    }
                    else {
                        // 写入元素的开头
                        currentWriter.writeStartElement(reader.getLocalName());
                        // 写入元素的属性
                        for (int i = 0; i < reader.getAttributeCount(); i++) {
                            currentWriter.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                        }
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    // 写入元素的文本内容
                    currentWriter.writeCharacters(reader.getText());
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    // 如果是某个元素的结束，则关闭当前片段
                    if ("dblp".equals(reader.getLocalName())) {
                        for(int i=0;i<24;i++){
                            // 写入片段的结尾
                            list.get(i).writeEndElement();
                            list.get(i).writeEndDocument();
                            // 关闭 XMLStreamWriter
                            list.get(i).close();
                            // 重置 XMLStreamWriter
                            currentWriter = null;
                        }
                    }
                    else {
                        // 写入元素的结尾
                        currentWriter.writeEndElement();
                    }
                    break;
            }
        }
        // 关闭 XMLStreamReader
        reader.close();
        System.out.println("DBLP.xml文件切分完成");
    }

    /**
     * @Description TODO: 将切分好的DBLP.xml文件传输给其他服务器
     * @return
     * @Author root
     * @Date 2022/12/11 14:58
     * @Version 1.0
     **/
    public static void sendXml(String fileName,String ipSelected,int portSelected,boolean isBackup) throws Exception{
        try {
            // 创建Socket对象
            Socket socket = new Socket(ipSelected, portSelected);
            // 创建输出流
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            // 向Server传递文件名称
            outputStream.writeUTF(fileName);
            outputStream.flush();
            System.out.println("Send:"+fileName);

            // 向Server传递是否为备份文件的信息
            String backupTag;
            if (isBackup == true) {
                backupTag = "isBackup";
            } else {
                backupTag = "notBackup";
            }
            outputStream.writeUTF(backupTag);
            outputStream.flush();
            System.out.println("Send:"+backupTag);

            // 文件路径
            String filePath = "/mnt/splitedXmls/" + fileName;

            // 读取文件并将文件内容写入输出流
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead=0;
            System.out.println("Send:发送文件....");
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            // 关闭文件输入流和Socket输出流
            fileInputStream.close();
            outputStream.close();
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
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
            // Initialize.SplitXml();
            // 将切分好的xml文件发送给各个虚拟机
            Initialize in=new Initialize();
            for(int i=0;i<24;i++) {
                String fileName="dblp"+i+".xml";
                System.out.println("发送"+fileName+"文件");
                // 虚拟机分配顺序：服务器0-端口0，服务器0-端口1，服务器1-端口0....
                // ip序号
                int ipSelect=(i/2)%3;
                // port序号
                int portSelect=i%2;
                // 备份文件的ip序号
                int ipBackupSelect=-1;
                // 备份文件的port序号
                int portBackupSelect=-1;
                if(portSelect==0){
                    ipBackupSelect=ipSelect;
                    portBackupSelect=1;
                }
                else{
                    ipBackupSelect=(ipSelect+1)%3;
                    portBackupSelect=0;
                }
                // 发送xml文件（正式版本）
                System.out.println(fileName+"发送至："+ipList[ipSelect]+":"+portList[portSelect]);
                in.sendXml(fileName, ipList[ipSelect], portList[portSelect], false);
                System.out.println(fileName+"发送至："+ipList[ipSelect]+":"+portList[portSelect]);
                // 发送xml文件（备份版本）：规则为虚拟机序号+1
                in.sendXml(fileName, ipList[ipBackupSelect], portList[portBackupSelect], true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
