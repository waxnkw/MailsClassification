package partitioner;

import confs.MyConf;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;

/**
 * @author zhangao
 * @version  2018.7.18
 * 将<term # termTimesInDoc>按照term进行分类
 */
public class ChooseTermPartitioner extends HashPartitioner<Text, IntWritable> {
    private Text word = new Text();

    @Override
    public int getPartition(Text key, IntWritable value, int numReduceTasks) {
        String term = key.toString().split(MyConf.Delim)[0];
        word.set(term);
        return super.getPartition(word, value, numReduceTasks);
    }
}
