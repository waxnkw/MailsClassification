package tasks;

import mapper.BayesMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import reducer.BayesReducer;

/**
 * 训练的task
 * */
public class TrainTask {
    private Job job;
    private Configuration conf;

    private void confJob() throws Exception{
        String inputPath = FilePaths.Vectorized_Doc;
        String outPath = FilePaths.Bayes_Param;

        job = Job.getInstance(conf, "train algorithm");
        job.setMapperClass(BayesMapper.class);
        job.setReducerClass(BayesReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outPath));

        //删除已存在输出目录
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path(outPath);
        if (fs.exists(path)){
            fs.delete(path, true);
        }
    }

    public TrainTask(String []args) throws Exception {
        conf = new Configuration();
        confJob();
    }

    public void execute() throws Exception{
        job.waitForCompletion(true);
    }
}
