package partitioner;

import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;
import writable.ClsMarkKey;
import writable.TermMarkVal;

/**
 * @author zhangao
 * @version  2018.7.18
 * 将<cls mark>按照cls进行分类
 */
public class ChooseTermSortPartitioner extends HashPartitioner<ClsMarkKey, TermMarkVal> {
    //private Text word = new Text();

    @Override
    public int getPartition(ClsMarkKey key, TermMarkVal value, int numReduceTasks) {
        String cls = key.getCls();
        //word.set(cls);
        return (cls.hashCode() & 2147483647) % numReduceTasks;
    }
}
