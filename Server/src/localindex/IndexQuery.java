package localindex;

import java.awt.*;

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
     * @param vMachinePort 当前虚拟机对应的端口 例：8820，8821
     * @param xmlFileName 要查询的xml片段文件的文件名
     * @param author 要查询的author
     * @param beginYear 查询限定的起始年份
     * @param endYear 查询限定的终止年份
     * @return 计数
     */
    public static int queryByIndex(boolean is_copy,int vMachinePort,String xmlFileName,String author,String beginYear,String endYear){
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
     * queryByIndex函数使用示例代码
     * @param args
     */
    public static void main(String[] args){
        int result=queryByIndex(false,8820,"dblp16.xml","Yuval Yarom","2017","2022");
        System.out.println(result);
    }
}
