package partitioner;

import confs.MyConf;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;

/**
 * @author lizhihao
 * @version 2018.7.20
 * <cls,term>按照 term进行分类
 * */
public class BayesPartitioner extends HashPartitioner<Text, IntWritable> {
    private Text word = new Text();
    @Override
    public int getPartition(Text key, IntWritable value, int numReduceTasks) {
        String term = key.toString().split(MyConf.Delim)[0];
        word.set(term);
        return super.getPartition(word, value, numReduceTasks);
    }
}
