package reducer;

import confs.MyConf;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import utility.DocsNumUtility;
import utility.MyEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BayesReducer extends Reducer<Text, IntWritable, Text, Text> {
    private List<MyEntry<String,Double>> clsTermProbList = new ArrayList<MyEntry<String, Double>>();
    private StringBuilder stb = new StringBuilder();
    private String curCls;

    private Text keyToWrite = new Text();
    private Text valToWrite = new Text();


    @Override
    public void reduce(Text key, Iterable<IntWritable> values,
                       Reducer<Text, IntWritable, Text, Text>.Context context)
            throws IOException, InterruptedException{
        int sum = 0;
        String cls = key.toString().split(MyConf.Delim)[0];
        String term = key.toString().split(MyConf.Delim)[1];
        for (IntWritable val: values){
            sum += val.get();
        }
        double prob = sum/((double)DocsNumUtility.getDocNumsOfClass(cls));

        if (curCls == null){curCls = cls;}
        //如果仍然是当前的类别内的统计
        if (curCls.equals(cls)){
            clsTermProbList.add(new MyEntry<String, Double>(term, prob));
        }
        else{
            writeResult(context);

            stb.delete(0, stb.length());
            clsTermProbList.clear();
            curCls = cls;
        }
    }

    @Override
    public void cleanup(Reducer<Text, IntWritable, Text, Text>.Context context)
            throws IOException, InterruptedException{
        writeResult(context);
    }

    private void writeResult(Context context)throws IOException, InterruptedException{
        for (MyEntry<String,Double> entry: clsTermProbList){
            String term = entry.getKey();
            double prob = entry.getValue();
            stb.append(term).append(":").append(prob).append("\t");
        }
        keyToWrite.set(curCls);
        valToWrite.set(stb.toString());
        context.write(keyToWrite, valToWrite);
    }
}
