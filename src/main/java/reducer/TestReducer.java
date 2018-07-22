package reducer;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class TestReducer extends Reducer<Text, BooleanWritable, Text, DoubleWritable> {
    private final Text keyToWrite= new Text("准确率:");
    private DoubleWritable valToWrite = new DoubleWritable();

    private int totalSum = 0;
    private int rightSum = 0;

    @Override
    public void reduce(Text key, Iterable<BooleanWritable> values,
                       Reducer<Text, BooleanWritable, Text, DoubleWritable>.Context context)
            throws IOException, InterruptedException{
        for (BooleanWritable rightOrNot: values){
            if (rightOrNot.get()){rightSum += 1;}
            totalSum += 1;
        }

    }

    @Override
    public void cleanup(Context context)throws IOException, InterruptedException{
        valToWrite.set(rightSum/(double)totalSum);
        context.write(keyToWrite, valToWrite);
    }
}
