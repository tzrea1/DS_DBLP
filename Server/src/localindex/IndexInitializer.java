package localindex;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 在本机上初始化xml片段文件对应的索引
 */
public class IndexInitializer {
    private static String DBLP_Path = "/mnt/dblpXmls";
    private static String DBLP_Backup_Path = "/mnt/dblpBackupXmls";
    public static void main(String[] args){

        //建立一个字符串数组用来存四个xml文件的存放路径
        String[] xmlDirs = {
                DBLP_Path+"/8820",
                DBLP_Backup_Path+"/8820",
                DBLP_Path+"/8821",
                DBLP_Backup_Path+"/8821"
        };

        /* 针对两台虚拟机的各自的两个文件夹（正常和备份），共计四个位置创建本地索引文件夹 */
        File outDir=null;
        // 循环建立四个本地索引存放的文件夹
        for (String xmlDir:xmlDirs) {
            outDir = new File(xmlDir+"/localIndex");
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
        }

        /* 扫描四个xml文件存放目录下的所有xml文件，为每个xml文件建立对应的索引存放在刚刚创建的localIndex目录下*/
        for (String xmlDir:xmlDirs) {
            File dir = new File(xmlDir);
            File[] xmlFiles = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".xml");
                }
            });
            /*
            System.out.println(xmlDir);
            for (File xmlFile : xmlFiles) {
                System.out.println(xmlFile.getName());
            }
            */
            for (File xmlFile : xmlFiles) {
                LocalIndex.generateIndex(
                        xmlFile.getAbsolutePath(),
                        xmlDir+"/localIndex"
                );
                System.out.println("文件:\""+xmlFile.getAbsolutePath()+"\"对应索引已建立...");
            }
            System.out.println("目录:\""+xmlDir+"\"内的所有xml文件索引已建立完成\n");
        }
//        LocalIndex.generateIndex("/mnt/xmlLocalIndex");
//        LocalIndex.queryByIndex();
    }
}
