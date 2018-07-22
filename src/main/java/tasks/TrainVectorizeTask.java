package tasks;

import mapper.OneHotMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import reducer.OneHotReducer;

import java.net.URI;

/**
 * 训练数据向量化的task
 * */
public class TrainVectorizeTask {
    private Job job;
    private Configuration conf;

    private void confJob(String argPath) throws Exception{
        job = Job.getInstance(conf, "train data vectorize");
        job.setMapperClass(OneHotMapper.class);
        job.setReducerClass(OneHotReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(argPath));
        FileOutputFormat.setOutputPath(job, new Path(FilePaths.Vectorized_Doc));

        //删除已存在输出目录
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path(FilePaths.Vectorized_Doc);
        if (fs.exists(path)){
            fs.delete(path, true);
        }
    }

    public TrainVectorizeTask(String [] args) throws Exception {
        conf = new Configuration();
        DistributedCache.addCacheFile(new URI(FilePaths.Sorted_Term_Result+"/part-r-00000"), conf);
        confJob(args[0]);
    }

    public void execute() throws Exception{
        job.waitForCompletion(true);
    }
}
