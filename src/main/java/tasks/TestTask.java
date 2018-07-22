package tasks;

import mapper.TestMapper1;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import reducer.TestReducer;

import java.net.URI;

/**
 * @author wangxingzhao
 * 测试准确率用task
 * */
public class TestTask {
    private Job job;
    private Configuration conf;

    //private String bayesPath = FilePaths.Bayes_Param+"/part-r-00000";
    private String bayesPath = FilePaths.Sorted_Term_Result+"/part-r-00000";

    private void confJob()throws Exception{
        String inputPath = FilePaths.Vectorized_Doc;
        String outPath = FilePaths.Test_Acc_Result;

        job = Job.getInstance(conf, "test algorithm");
        job.setMapperClass(TestMapper1.class);
        job.setReducerClass(TestReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(BooleanWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outPath));

        //删除已存在输出目录
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path(outPath);
        if (fs.exists(path)){
            fs.delete(path, true);
        }
    }



    public TestTask(String[] args) throws Exception{
        this.conf = new Configuration();
        DistributedCache.addCacheFile(new URI(bayesPath), conf);
        confJob();
    }

    public void execute() throws Exception{
       job.waitForCompletion(true);
    }
}
