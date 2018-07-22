package mapper;

import confs.MyConf;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @author lizhihao
 * @version 2018.7.21
 * bayes训练参数的mapper
 * */
public class BayesMapper extends Mapper<Object, Text, Text, IntWritable> {
    private Text keyToWrite = new Text();
    private final IntWritable one = new IntWritable(1);

    /**
     * 写 <cls,term>,1形式键值对
     * */
    @Override
    public void map(Object key, Text value,
                    Mapper<Object, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException{
        StringTokenizer tokenizer = new StringTokenizer(value.toString());
        if (!tokenizer.hasMoreTokens()){return;}
        String cls = tokenizer.nextToken().trim();

        while(tokenizer.hasMoreTokens()){
            String temp = tokenizer.nextToken().trim();
            String term = temp.split(":")[0];
            keyToWrite.set(cls+MyConf.Delim+term);
            context.write(keyToWrite, one);
        }
    }
}
