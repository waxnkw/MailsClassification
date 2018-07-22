package mapper;

import confs.MyConf;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import tasks.FilePaths;
import utility.MyEntry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

/**
 * @author wangxingzhao
 * @version 2018.7.20
 * 进行测试集准确率统计的mapper
 * */
public class TestMapper1 extends Mapper<Object, Text, Text, BooleanWritable>  {
    private List<MyEntry<String, ArrayList<String>>> bayesParamsList
            = new ArrayList<MyEntry<String, ArrayList<String>>>();

    //保证能分到一个reducer
    private final Text keyToWrite = new Text("a");
    private BooleanWritable valToWrite = new BooleanWritable();

    /**
     * 讲所有种类及其对应关键词读入
     * */
    @Override
    public void setup(Mapper<Object, Text, Text, BooleanWritable>.Context context)
            throws IOException, InterruptedException{
        URI[] caches = DistributedCache.getCacheFiles(context.getConfiguration());
        URI uri = null;
        for (URI possibleUri: caches){
            if (possibleUri.toString().contains(FilePaths.Sorted_Term_Result)){uri = possibleUri;}
        }
        if (uri==null){System.out.println(FilePaths.Bayes_Param+" doesn't exist");}
        FileSystem fs = FileSystem.get(uri, context.getConfiguration());
        InputStream in = fs.open(new Path(caches[0]));
        Scanner input = new Scanner(in);

        while(input.hasNextLine()){
            String cls = null;
            ArrayList<String> list = new ArrayList<String>();
            for (int i=0; i<MyConf.Term_Nums_In_Each_Class; i++){
                String line = input.nextLine().trim();
                String [] clsAndTerm = line.split("\t");
                cls = clsAndTerm[0];
                list.add(clsAndTerm[1].trim());
            }
            bayesParamsList.add(new MyEntry<String, ArrayList<String>>(cls, list));
        }
    }

    private List<MyEntry<String, Integer>> termAndNumsList = new ArrayList<MyEntry<String, Integer>>();
    private String predictedCls;
    private int maxProb = 0;

    @Override
    public void map(Object key, Text value,
                    Mapper<Object, Text, Text, BooleanWritable>.Context context)
            throws IOException, InterruptedException{
        String vec = value.toString();
        StringTokenizer tokenizer = new StringTokenizer(vec);
        String realCls = tokenizer.nextToken().trim();
        while (tokenizer.hasMoreTokens()){
            //vecTermSet.add(tokenizer.nextToken().trim().split(":")[0]);
            String []termAndNum = tokenizer.nextToken().trim().split(":");
            int num = Integer.parseInt(termAndNum[1]);
            termAndNumsList.add(new MyEntry<String, Integer>(termAndNum[0], num));
        }

        for (MyEntry<String, ArrayList<String>> bayesParam: bayesParamsList){
            int prob = 0;
            String cls = bayesParam.getKey();
            ArrayList<String> termAndProbList = bayesParam.getValue();
            int size = MyConf.Term_Nums_In_Each_Class;
            for (MyEntry<String,Integer> entry: termAndNumsList){
                String term = entry.getKey();
                int num = entry.getValue();
                if (termAndProbList.contains(term)){prob += (size-termAndProbList.indexOf(term))*num;}
            }

            //如果比当前的评分高,则更换当前预测种类
            if (prob>maxProb){
                maxProb = prob;
                predictedCls = cls;
            }
        }

        //如果预测正确,输出(xx,1),否则(xx,0)
        if (realCls.equals(predictedCls)){
            valToWrite.set(true);
        }
        else {valToWrite.set(false);}
        context.write(keyToWrite, valToWrite);

        //清空全局变量
        predictedCls = null;
        maxProb = 0;
        //vecTermSet.clear();
        termAndNumsList.clear();
    }
}
