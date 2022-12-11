package localindex;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description 数据节点，存储[author] [year] [frequency]
 * @Author Ice Cream
 * @Date 2022/12/10 20:32
 */
class DataNode implements Serializable{
    String author;
    String year;
    int frequency;

    // 构造方法
    public DataNode(String author, String year, int frequency) {
        this.author = author;
        this.year = year;
        this.frequency = frequency;
    }
}
/**
 * @Description 数据节点的集合
 * @Author Ice Cream
 * @Date 2022/12/10 20:32
 */
class DataSet implements Serializable {
    Set<DataNode> set; // 使用HashSet来存储节点

    // 构造方法
    public DataSet() {
        set = new HashSet<>();
    }

    // 判断链表中是否存在某一节点满足author和year与传入的参数完全相同
    public boolean contains(String author, String year) {
        for (DataNode node : set) {
            if (node.author.equals(author) && node.year.equals(year)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据传入的author和year信息，查找集合中是否存在，若存在则相应频次++，若不存在则新增该行数据
     * @param author
     * @param year
     */
    public void add(String author, String year){
        for (DataNode node : set) {
            if (node.author.equals(author) && node.year.equals(year)) {
                node.frequency++;
                return;
            }
        }
        //集合中并不存在该行数据
        this.set.add(new DataNode(author,year, 1));
        return;
    }

    /**
     * 判断输入的年份是否在指定的范围内，并返回一个布尔值表示是否在指定范围内.
     * @param year 要判断的年份
     * @param beginYear 起始年份
     * @param endYear 结束年份
     * @return 是否在指定范围内
     */
    private static boolean checkYearInRange(String year, String beginYear, String endYear) {
        if(year==null||year=="null"){
            return false;
        }
        if(beginYear.equals("*") && endYear.equals("*")) {
            return true;
        } else if(beginYear.equals("*")) {
            if(Integer.parseInt(year) <= Integer.parseInt(endYear)) {
                return true;
            } else {
                return false;
            }
        } else if(endYear.equals("*")) {
            if(Integer.parseInt(year) >= Integer.parseInt(beginYear)) {
                return true;
            } else {
                return false;
            }
        } else {
            if(Integer.parseInt(year) >= Integer.parseInt(beginYear)
                    && Integer.parseInt(year) <= Integer.parseInt(endYear)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 查找此数据集合内符合要求的author和year索所对应的总的论文频次总数
     * @param author
     * @param beginYear
     * @param endYear
     * @return 论文频次总数
     */
    public int countByAuthorAndYear(String author,String beginYear,String endYear){
        int count=0;
        for (DataNode node : set) {
            //System.out.println("["+node.author+"] ["+node.year+"]"+ node.frequency);
            if (node.author.equals(author) && checkYearInRange(node.year,beginYear,endYear)) {
                count+=node.frequency;
            }
        }
        return count;
    }
    /**
     * 查找此数据集合内符合要求的author所对应的总的论文频次总数
     * @param author
     * @return 论文频次总数
     */
    public int countByAuthor(String author){
        int count=0;
        for (DataNode node : set) {
            if (node.author.equals(author)) {
                count+=node.frequency;
            }
        }
        return count;
    }
}
