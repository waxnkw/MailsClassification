package mapper;

import confs.MyConf;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import utility.MyTokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author lizhihao
 * @version 2018.7.20
 * 实现每篇文档的one-hot编码的mapper
 * */
public class OneHotMapper extends Mapper<Object, Text, Text, Text> {
    private List<String> chosenTerms = new ArrayList<String>();
    //private final int one = new IntWritable(1);
    private Text keyToWrite = new Text();
    private Text valToWrite = new Text();


    /**
     * 将选中的所有关键词读入
     * */
    @Override
    public void setup(Mapper<Object, Text, Text, Text>.Context context)
            throws IOException, InterruptedException{
        URI[] caches = DistributedCache.getCacheFiles(context.getConfiguration());
        FileSystem fs = FileSystem.get(caches[0], context.getConfiguration());
        InputStream in = fs.open(new Path(caches[0]));
        Scanner input = new Scanner(in);
        while(input.hasNext()){
            String line = input.next().trim();
            chosenTerms.add(line);
        }
    }

    /**
     * 结果以<fileName,cls>,<term,1>形式写出
     * fileName是为了在reducer中可以统计doc内词频
     * */
    @Override
    public void map(Object key, Text value,
                    Mapper<Object, Text, Text, Text>.Context context)
            throws IOException, InterruptedException{
        String fileName = getFileName(context);
        String cls = getClassName(context);

        List<String> words = MyTokenizer.getInstance().tokenize(value.toString());
        for (String term: words){
            //如果是关键词
            if (chosenTerms.contains(term)){
                keyToWrite.set(fileName+MyConf.Delim+cls);
                valToWrite.set(term+MyConf.Delim+"1");
                context.write(keyToWrite, valToWrite);
            }
        }
    }

    /**
     * 得到当前doc名称
     * */
    private String getClassName(Context context){
        String[] paths = ((FileSplit)context.getInputSplit()).getPath().toString().split("/");
        int len = paths.length;
        return paths[len-2];
    }

    /**
     * 得到当前doc所属种类
     * */
    private String getFileName(Context context){
        return ((FileSplit)context.getInputSplit()).getPath().getName();
    }
}
