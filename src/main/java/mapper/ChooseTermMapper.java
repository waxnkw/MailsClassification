package mapper;

import confs.MyConf;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import utility.MyTokenizer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 选择特征词的Mapper
 * @author zhangao
 * @version  2018.7.19
 */
public class ChooseTermMapper extends Mapper<Object, Text, Text, IntWritable> {
    //当前文件名
    private  String fileName;

    private String delim = MyConf.Delim;

    private Text keyToWrite = new Text();
    private final IntWritable one = new IntWritable(1);

    private Map<String, Integer> docWordsMap = new HashMap<String, Integer>();
    //分词器
    private MyTokenizer tokenizer = MyTokenizer.getInstance();

    /**
     * 以<term # termTimesInDoc>,1形式写结果
     * */
    @Override
    public void map(Object key, Text value,
                    Mapper<Object, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException{
        String curFileName = getFileName(context);
        if (curFileName!=null && curFileName.equals(fileName)){
            putDocWords(value.toString());
        }
        else if (!curFileName.equals(fileName)){
            //clean
            writeDocWords(context);
            putDocWords(value.toString());
            fileName = curFileName;
        }else {System.out.println("file without filename in chooseTermMapper");}
    }

    /**
     * 结束map任务时输出最后一份doc结果
     * */
    @Override
    public void cleanup(Mapper<Object, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException{
        writeDocWords(context);
    }

    /**
     * 写当前doc内所有term的结果
     * 以<term#termTimesInDoc>,1形式写结果
     * */
    private void writeDocWords(Context context)throws IOException, InterruptedException{
        for (Map.Entry<String, Integer> entry: docWordsMap.entrySet()){
            keyToWrite.set(entry.getKey()+delim+getClassName(context));
            context.write(keyToWrite, one);
        }
        docWordsMap.clear();
    }

    /**
     * 暂时存储当前doc的term
     * */
    private void putDocWords(String value){
        for (String word: tokenizer.tokenize(value)){
            docWordsMap.put(word, 1);
        }
    }

    /**
     * 得到当前处理的doc所属于cls
     * */
    private String getClassName(Context context){
        String[] paths = ((FileSplit)context.getInputSplit()).getPath().toString().split("/");
        int len = paths.length;
        return paths[len-2];
    }

    /**
     * 得到当前处理的文件名
     * */
    private String getFileName(Context context){
        return ((FileSplit)context.getInputSplit()).getPath().getName();
    }
}
