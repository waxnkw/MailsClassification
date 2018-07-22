package reducer;

import confs.MyConf;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import utility.DocsNumUtility;
import utility.MyEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChooseTermReducer extends Reducer<Text, IntWritable, Text, DoubleWritable> {
    private String curTerm;
    private int termSum = 0;
    private List<Map.Entry<String, Integer>> termList= new ArrayList<Map.Entry<String, Integer>>();

    private Text keyToWrite = new Text();
    private DoubleWritable valToWrite= new DoubleWritable();

    @Override
    public void reduce(Text key, Iterable<IntWritable> values,
                       Reducer<Text, IntWritable, Text, DoubleWritable>.Context context)
            throws IOException, InterruptedException{

        String term = key.toString().split(MyConf.Delim)[0];
        //String docCls = key.toString().split(MyConf.Delim)[1];

        int tempSum = 0;
        for (IntWritable times: values){
            tempSum += times.get();
        }

        if (curTerm==null){curTerm=term;}

        if (term.equals(curTerm)){
            termList.add(new MyEntry<String, Integer>(key.toString(), tempSum));
            termSum += tempSum;
        }
        else {
            writeChi2Result(context);
            curTerm = term;
            termList.clear();
            termSum = 0;

            termList.add(new MyEntry<String, Integer>(key.toString(), tempSum));
            termSum += tempSum;
        }

    }

    @Override
    public void cleanup(Reducer<Text, IntWritable, Text, DoubleWritable>.Context context)
            throws IOException, InterruptedException{
        writeChi2Result(context);
    }

    /**
     * 计算chi2得分并写出
     * */
    private void writeChi2Result(Context context) throws IOException, InterruptedException{
        for (Map.Entry<String, Integer> entry : termList){
            String termAndCls = entry.getKey();

            String term = termAndCls.split(MyConf.Delim)[0];
            String className = termAndCls.split(MyConf.Delim)[1];
            int times = entry.getValue();
            double a = times;
            double b = termSum-a;
            double c = DocsNumUtility.getDocNumsOfClass(className)-a;
            double d = DocsNumUtility.getTotalNumOfDocs()-a-b-c;
            double chi2 = Math.pow(a*d-b*c, 2)/((a+b)*(c+d)*(a+c)*(b+d));
            keyToWrite.set(termAndCls);
            valToWrite.set(chi2);
            //context.write(keyToWrite, valToWrite);
            if (!className.equals(term)){context.write(keyToWrite, valToWrite);}//不加入当前种类
        }
    }


}
