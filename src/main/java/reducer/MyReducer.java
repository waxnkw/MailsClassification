package reducer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class MyReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    private IntWritable result = new IntWritable();
    @Override
    public void reduce(Text key, Iterable<IntWritable> values,
                       Reducer<Text, IntWritable, Text, IntWritable>.Context context)
            throws IOException, InterruptedException{
        int sum = 0;
        for (IntWritable times: values){
            sum += times.get();
            result.set(sum);
        }
        context.write(key, result);
    }
}
