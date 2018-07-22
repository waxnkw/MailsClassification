package mapper;

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
public class TestMapper extends Mapper<Object, Text, Text, BooleanWritable> {
    private List<MyEntry<String, ArrayList<MyEntry<String, Double>>>> bayesParamsList
            = new ArrayList<MyEntry<String, ArrayList<MyEntry<String, Double>>>>();

    //保证能分到一个reducer
    private final Text keyToWrite = new Text("a");
    private BooleanWritable valToWrite = new BooleanWritable();


    /**
     * 讲所有bayes参数读入
     * */
    @Override
    public void setup(Mapper<Object, Text, Text, BooleanWritable>.Context context)
            throws IOException, InterruptedException{
        URI[] caches = DistributedCache.getCacheFiles(context.getConfiguration());
        URI uri = null;
        //找贝叶斯参数的路径
        for (URI possibleUri: caches){
            if (possibleUri.toString().contains(FilePaths.Bayes_Param)){uri = possibleUri;}
        }
        if (uri==null){System.out.println(FilePaths.Bayes_Param+" doesn't exist");}

        FileSystem fs = FileSystem.get(uri, context.getConfiguration());
        InputStream in = fs.open(new Path(caches[0]));
        Scanner input = new Scanner(in);
        while(input.hasNextLine()){
            ArrayList<MyEntry<String, Double>> list = new ArrayList<MyEntry<String, Double>>();

            String line = input.nextLine();
            StringTokenizer tokenizer = new StringTokenizer(line);
            String cls = tokenizer.nextToken().trim();
            while(tokenizer.hasMoreTokens()){
                String[] termAndProb = tokenizer.nextToken().trim().split(":");
                String term = termAndProb[0];
                double prob = Double.parseDouble(termAndProb[1]);
                list.add(new MyEntry<String, Double>(term, prob));
            }

            MyEntry<String, ArrayList<MyEntry<String, Double>>> entry
                    = new MyEntry<String, ArrayList<MyEntry<String, Double>>>(cls, list);
            bayesParamsList.add(entry);
        }
    }

    private Set<String> vecTermSet = new HashSet<String>();
    //当前预测的种类
    private String predictedCls;
    //该种类得分
    private double maxProb = 0.0;

    /**
     * 使用朴素贝叶斯进行分类并统计正确与否
     * 结果 a,true/false 形式
     * */
    @Override
    public void map(Object key, Text value,
                    Mapper<Object, Text, Text, BooleanWritable>.Context context)
            throws IOException, InterruptedException{
        String vec = value.toString();
        StringTokenizer tokenizer = new StringTokenizer(vec);
        String realCls = tokenizer.nextToken().trim();
        //读入当前文档对应词向量
        while (tokenizer.hasMoreTokens()){
            vecTermSet.add(tokenizer.nextToken().trim().split(":")[0]);
        }

        for (MyEntry<String, ArrayList<MyEntry<String, Double>>> bayesParam: bayesParamsList){
            double prob = 1.0;
            String cls = bayesParam.getKey();
            ArrayList<MyEntry<String, Double>> termAndProbList = bayesParam.getValue();
            for (MyEntry<String, Double> entry: termAndProbList){
                String term = entry.getKey();
                double termProb = entry.getValue();

                if (term.equals(cls)){continue;}
                if (term.contains("edu")||term.contains("com")){continue;}

                if (vecTermSet.contains(term)){
                    //if (termProb>0.1){ prob *= termProb; }
                    prob *= termProb;
                    //if (termProb==1.0){prob*=MyConf.Bayes_Adj_Param;}
                }
                else {
                    //if (termProb<0.99){prob *= (1-termProb);}
                    //if (!term.equals(cls)){prob *= (1-termProb);}
                    prob *= (1-termProb);
                }
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
        maxProb = 0.0;
        vecTermSet.clear();
    }
}
