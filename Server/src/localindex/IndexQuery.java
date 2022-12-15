package localindex;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * 用以根据索引文件查询论文频次
 */
public class IndexQuery {
    private static String DBLP_Path = "/mnt/dblpXmls";
    private static String DBLP_Backup_Path = "/mnt/dblpBackupXmls";

    /**
     * 对LocalInex类中的queryByIndex函数进行了封装
     * 自动根据传入的参数生成索引文件所在目录，并调用LocalInex类中的queryByIndex函数得到结果
     * @param is_copy 要查询的xml片段文件是否为备份
     * @param vMachinePort 虚拟机对应端口
     * @param xmlFileName 要查询的xml片段文件的文件名
     * @param author 要查询的author
     * @param beginYear 查询限定的起始年份
     * @param endYear 查询限定的终止年份
     * @return 计数
     */
    public static int queryByOneBlockIndex(boolean is_copy,int vMachinePort,String xmlFileName,String author,String beginYear,String endYear){
        String indexDir;
        if(is_copy){
            indexDir=DBLP_Backup_Path;
        }
        else{
            indexDir=DBLP_Path;
        }
        indexDir+="/";
        indexDir+=String.valueOf(vMachinePort);
        indexDir+="/localIndex";

        String prefix = xmlFileName.substring(0, xmlFileName.indexOf("."));
        indexDir+="/";
        indexDir+=prefix;

        int result=LocalIndex.queryByIndex(indexDir,author,beginYear,endYear);
        //System.out.println(indexDir);
        return result;
    }

    /**
     * 对LocalInex类中的queryByIndex函数进行了封装
     * 对于本虚拟机对应文件夹（备份或非备份），调用本类中的queryByOneBlockIndex函数得到结果
     * @param is_copy 要查询的xml片段文件是否为备份
     * @param vMachinePort 虚拟机对应端口,示例：8820
     * @param author 要查询的author
     * @param beginYear 查询限定的起始年份
     * @param endYear 查询限定的终止年份
     * @return 计数
     */
    public static String queryByIndex(boolean is_copy,int vMachinePort,String author,String beginYear,String endYear){
        String xmlDir;
        if(is_copy){
            xmlDir=DBLP_Backup_Path;
        }
        else{
            xmlDir=DBLP_Path;
        }
        xmlDir+="/";
        xmlDir+=String.valueOf(vMachinePort);
        File dir = new File(xmlDir);
        File[] xmlFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });
        // 记录频次
        int num=0;
        for(File xmlFile : xmlFiles) {
            int result = 0;
            // 得到某一块的查询结果
            result = queryByOneBlockIndex(is_copy,vMachinePort,xmlFile.getName(),author,beginYear,endYear);
            // 累加频次
            num+=result;
        }
        return String.valueOf(num);
    }
    /**
     * queryByIndex函数使用示例代码
     * @param args
     */
    public static void main(String[] args){
        String result=queryByIndex(false,8820,"Yuval Yarom","2010","2022");
        System.out.println(result);
    }
}
