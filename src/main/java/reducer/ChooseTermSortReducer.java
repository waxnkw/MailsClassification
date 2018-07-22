package reducer;

import confs.MyConf;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import writable.ClsMarkKey;
import writable.TermMarkVal;

import java.io.IOException;

public class ChooseTermSortReducer extends Reducer<ClsMarkKey, TermMarkVal, Text, Text> {
    private Text keyToWrite = new Text();
    private Text valToWrite = new Text();
    private final int iterTimes = MyConf.Term_Nums_In_Each_Class;

    @Override
    public void reduce(ClsMarkKey key, Iterable<TermMarkVal> values,
                       Reducer<ClsMarkKey, TermMarkVal, Text, Text>.Context context)
            throws IOException, InterruptedException{
        int i=0;
        for (TermMarkVal val: values){
            if (i>=iterTimes){break;}

            //debug用 输出cls term
            keyToWrite.set(key.getCls()+"\t"+val.getTerm());

            //实际使用
            //keyToWrite.set(val.getTerm());

            valToWrite.set("");
            context.write(keyToWrite, valToWrite);

            i++;
        }
    }
}
