package reducer;

import confs.MyConf;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OneHotReducer extends Reducer<Text, Text, Text, Text> {
    private Map<String,Integer> docTermsMap = new HashMap<String, Integer>();
    private Text valToWrite = new Text();
    private Text keyToWrite = new Text();
    private StringBuilder stb = new StringBuilder();

    @Override
    public void reduce(Text key, Iterable<Text> values,
                       Reducer<Text, Text, Text, Text>.Context context)
            throws IOException, InterruptedException{
        String cls = key.toString().split(MyConf.Delim)[1];
        for (Text val: values){
            String [] termAndNum = val.toString().split(MyConf.Delim);
            String term = termAndNum[0];
            int num = Integer.parseInt(termAndNum[1]);
            if (!docTermsMap.containsKey(term)){docTermsMap.put(term, 0);}
            docTermsMap.put(term, docTermsMap.get(term)+num);
            //docTermsMap.put(term, 1);
        }

        for (String term: docTermsMap.keySet()){
            stb.append(term+":"+docTermsMap.get(term)+"\t");
        }
        keyToWrite.set(cls);
        valToWrite.set(stb.toString());
        context.write(keyToWrite, valToWrite);
        docTermsMap.clear();
        stb.delete(0, stb.length());
    }
}
