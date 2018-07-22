package mapper;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @author lizhihao wangxingzhao zhangao
 * @version 2018.7.21
 * 初始测试用mapper,无实际作用
 * */
public class MyMapper extends Mapper<Object, Text, Text, IntWritable> {
    private static final IntWritable one = new IntWritable(1);
    private Text word = new Text();

    @Override
    public void map(Object key, Text value,
                    Mapper<Object, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException{
        StringTokenizer tokenizer = new StringTokenizer(value.toString());
        while(tokenizer.hasMoreTokens()){
            word.set(tokenizer.nextToken());
            context.write(word, one);
        }
    }
}
