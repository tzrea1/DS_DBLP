package localindex;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * @Description 本地索引相关类，用于生成索引以及利用索引进行查询
 * @Author Ice Cream
 * @Date 2022/12/10 20:49
 */
public class LocalIndex {

    public static final int LIST_NUM = 101;


    /**
     * 根据两个字符串计算其对应的范围在[0]-[range-1]的哈希值
     * @param A 第一个字符串
     * @param B 第二个字符串
     * @param range 哈希值结果范围
     * @return
     */
    public static int hashByStrings(String A,String B,int range){
        StringBuilder sb = new StringBuilder();
        sb.append(A);
        sb.append(B);
        return sb.hashCode()%range;
    }
    /**
     * 根据一个字符串计算其对应的范围在[0]-[range-1]的哈希值
     * @param author 作者名
     * @param range 哈希值结果范围
     * @return
     */
    public static int hashByAuthor(String author,int range){
        StringBuilder sb = new StringBuilder();
        sb.append(author);
        byte[] bytes = author.getBytes();
        int result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result += bytes[i];
        }
        return result%range;
    }


    public static void writeObjectToDisk(DataSet dataSet,String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(dataSet);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DataSet readObjectFromDisk(String filePath) {
        DataSet dataSet = null;

        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            dataSet = (DataSet) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return dataSet;
    }

    public static void updateObjectOnDisk(DataSet dataSet,String filePath) {
        // 将对象转换为字节数组
        byte[] data = toByteArray(dataSet);

        try (FileChannel fileChannel = new RandomAccessFile(filePath, "rw").getChannel()) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, data.length);
            mappedByteBuffer.put(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] toByteArray(Object object) {
        byte[] bytes = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            oos.flush();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }



    /**
     * 用于生成索引文件
     * @param inputXMLFilePath 需要建立索引文件的XML文件的完整路径，示例："C:\\Users\\Ice Cream\\分布式系统\\output3.xml"
     * @param outputDirectory 索引文件的保存目录，程序会在此目录下根据xml文件名建立对应目录来存储一系列索引文件，示例："D:\\Idea_Project\\DBLPTest\\outxml"
     */
    public static void generateIndex(String inputXMLFilePath,String outputDirectory){
        /*XML读取流创建*/
        // 创建 XMLInputFactory 对象
        XMLInputFactory factory = XMLInputFactory.newInstance();
        // 创建 XMLStreamReader 对象
        XMLStreamReader reader = null;
        try {
            reader = factory.createXMLStreamReader(new FileInputStream(inputXMLFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //创建一个字符串集合，包含DBLP数据库中所有可能的文章类型
        Set<String> typeSet = new HashSet<>(Arrays.asList(
                "article",
                "inproceedings",
                "proceedings",
                "book",
                "incollection",
                "phdthesis",
                "mastersthesis",
                "person",
                "data"
        ));

        /*索引信息相关变量定义*/
        // 存储某个块中的 author 信息
        List<String> authors = new ArrayList<>();
        // 存储某个块中的 year 信息
        String year = "null";
//        // 存储哈希桶的某一个链表
//        DataSet tmpdataSet=new DataSet();
//        // 创建所有链表文件并将其初始化
//        for(int i=0;i<LIST_NUM;i++){
//            String ouputFilePath=new String(
//                    outputDirectory+"/DataSet"+"-"+i+".ser"
//            );
//            //System.out.println(ouputFilePath);
//            writeObjectToDisk(tmpdataSet,ouputFilePath);
//        }
        // 存储哈希桶的某一个链表
        DataSet[] dataSets =new DataSet[LIST_NUM];
        for (int i = 0; i < dataSets.length; i++) {
            dataSets[i] = new DataSet();
        }
        int cccooonnnttt=0;
        long start = System.nanoTime();
        //读取解析xml文件
        try {
            // 遍历每一个节点
            while (reader.hasNext()) {
                int event = reader.next();
                // 处理不同类型的节点
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        String elementName = reader.getLocalName();
                        if (elementName.equals("author")) {
                            // 处理 author 节点
                            authors.add(reader.getElementText());
                        } else if (elementName.equals("year")) {
                            // 处理 year 节点
                            year = reader.getElementText();
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        elementName = reader.getLocalName();
                        if (typeSet.contains(elementName)) {
//                            // 在块末尾输出 author 和 year 信息
//                            System.out.println("Authors: " + authors);
//                            System.out.println("Year: " + year);
                            cccooonnnttt++;
//                            if(cccooonnnttt%10000==0) {
//                                // 计算时间差
//                                long elapsed = System.nanoTime() - start;
//                                start=System.nanoTime();
//                                // 格式化时间差，保留小数点后两位
//                                double elapsedSeconds = (double) elapsed / 1000000000.0;
//                                System.out.println("Elapsed time: " + elapsedSeconds + " seconds");
//                                System.out.println(cccooonnnttt/10000+"/26\n");
//                            }
                            // 根据author和year信息计算哈希值
                            for (String author:authors) {
                                //此时每一对信息是 [author]+[year]
                                //据此计算哈希值
                                int hashIndex=hashByAuthor(author,LIST_NUM);
                                //根据哈希值确定了其所在的链表（即文件）
//                                //读取对应文件内容到tmpdataset
//                                String tmpFilePath=new String(
//                                        outputDirectory+"/DataSet"+"-"+hashIndex+".ser"
//                                );
//                                //System.out.println("[index="+hashIndex+"] ["+author+"] ["+year+"]\n");
//
//                                tmpdataSet=readObjectFromDisk(tmpFilePath);
//                                //在内存中对tmpdataSet进行操作
//                                tmpdataSet.add(author,year);
//                                //将更新后的tmpdataSet更新回磁盘
//                                updateObjectOnDisk(tmpdataSet,tmpFilePath);
                                dataSets[hashIndex].add(author,year);
                            }
                            // 清空存储的信息
                            authors.clear();
                            year = "null";
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭 XMLStreamReader 对象
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 获取输入xml文件的前缀并据此作为新建的目录名称
        File file = new File(inputXMLFilePath);
        String realIndexDirectory = new String(
                outputDirectory+"/"+file.getName().replaceFirst("[.][^.]+$", "")
        );
        // 建立文件夹
        file = new File(realIndexDirectory);
        if (!file.exists()) {
            file.mkdirs();
        }
        // 写入所有链表文件
        for(int i=0;i<dataSets.length;i++){
            String ouputFilePath=new String(
                    realIndexDirectory+"/DataSet"+"-"+i+".ser"
            );
            writeObjectToDisk(dataSets[i],ouputFilePath);
        }
    }

    /**
     * 传入索引文件所在的实际目录，根据author和year信息进行查询，若beginYear和endYear均为“*”则仅根据author进行查询
     * @param indexDirectory 索引文件的保存目录（索引文件实际的保存目录）,例如"D:\\Idea_Project\\DBLPTest\\outxml\\output3"
     * @param author 作者名
     * @param beginYear 起始年份，为“*”时表示无限制
     * @param endYear 结束年份，为“*”时表示无限制
     */
    public static int queryByIndex(String indexDirectory,String author,String beginYear,String endYear){
        //论文频次总数
        int count=0;
        //计算查询参数的哈希值，以确定其所在的索引文件
        int hashIndex=hashByAuthor(author,LIST_NUM);
        String filePath=new String(
                indexDirectory+"/DataSet"+"-"+hashIndex+".ser"
        );
        //System.out.println("[index="+hashIndex+"]");
        //将其从磁盘上加载回内存的数据结构中
        DataSet dataSet=readObjectFromDisk(filePath);
        //进行查询
        if(beginYear.equals("*")&&endYear.equals("*")){
            count=dataSet.countByAuthor(author);
        }else{
            count=dataSet.countByAuthorAndYear(author,beginYear,endYear);
        }
        //打印
        //System.out.println(count);
        return count;
    }


    /**
     * Main函数，示范了这个类的两个主要函数怎么使用
     * @param args
     */
    public static void main(String[] args) {

        //生成索引
        String inputXMLFilePath = new String(
                "C:\\Users\\Ice Cream\\Desktop\\To do\\002_Assignments\\分布式系统\\output3.xml"
        );
        String outputDirectory = new String(
                "D:\\Idea_Project\\DBLPTest\\outxml"
        );
        generateIndex(inputXMLFilePath,outputDirectory);


        //用索引查询
        String realIndexDirectory = new String(
                "D:\\Idea_Project\\DBLPTest\\outxml\\output3"
        );
        queryByIndex(realIndexDirectory,"Yuval Yarom","2017","2022");


    }
}
