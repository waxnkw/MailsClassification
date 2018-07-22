package mapper;

import confs.MyConf;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import writable.ClsMarkKey;
import writable.TermMarkVal;

import java.io.IOException;

/**
 * 将特征词按照chi2得分将序排列的Mapper
 * @author zhangao
 * @version  2018.7.19
 */
public class ChooseTermSortMapper extends Mapper<Object, Text, ClsMarkKey, TermMarkVal> {
    private ClsMarkKey keyToWrite = new ClsMarkKey();
    private TermMarkVal valToWrite = new TermMarkVal();

    /**
     * 以<cls, mark>,<term, mark>形式写结果
     * 如此以实现按照mark排序
     * val中的mark是为了实现mark在reducer中可访问
     * */
    @Override
    public void map(Object key, Text value,
                    Mapper<Object, Text, ClsMarkKey, TermMarkVal>.Context context)
            throws IOException, InterruptedException{
        String [] strs = value.toString().split("\t");
        String term = strs[0].split(MyConf.Delim)[0];
        String cls = strs[0].split(MyConf.Delim)[1];
        double mark = 0.0;

        //计算结果为NaN时要记为0,否则有bug
        if(!strs[1].equals("NaN")) {mark = Double.parseDouble(strs[1]);}

        keyToWrite.setCls(cls);
        keyToWrite.setMark(mark);

        valToWrite.setTerm(term);
        valToWrite.setMark(mark);

        context.write(keyToWrite, valToWrite);
    }
}
