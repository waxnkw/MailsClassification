package tasks;

import comparator.ChooseTermGroupComparator;
import comparator.ChooseTermSortComparator;
import mapper.ChooseTermMapper;
import mapper.ChooseTermSortMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import reducer.ChooseTermReducer;
import reducer.ChooseTermSortReducer;
import writable.ClsMarkKey;
import writable.TermMarkVal;

/**
 * @author zhangao
 * 选择特征词的task
 * */
public class ChooseTermTask {
    private JobControl jc ;
    private Job job1;
    private Job job2;
    private Configuration conf;

    private void confSortJob()throws Exception{
        job2 = Job.getInstance(this.conf, "chooseTerm");
        job2.setJarByClass(ChooseTermTask.class);
        job2.setMapperClass(ChooseTermSortMapper.class);
        //job2.setCombinerClass(ChooseTermReducer.class);
        job2.setReducerClass(ChooseTermSortReducer.class);
        job2.setMapOutputKeyClass(ClsMarkKey.class);
        job2.setMapOutputValueClass(TermMarkVal.class);
        job2.setSortComparatorClass(ChooseTermSortComparator.class);
        job2.setGroupingComparatorClass(ChooseTermGroupComparator.class);
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job2, new Path(FilePaths.Choose_Term_Result));
        FileOutputFormat.setOutputPath(job2, new Path(FilePaths.Sorted_Term_Result));

        //删除已存在输出目录
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path(FilePaths.Sorted_Term_Result);
        if (fs.exists(path)){
            fs.delete(path, true);
        }
    }

    private void confChooseJob(String argPath) throws Exception{
        job1 = Job.getInstance(this.conf, "chooseTerm");
        job1.setJarByClass(ChooseTermTask.class);
        job1.setMapperClass(ChooseTermMapper.class);
        //job.setCombinerClass(ChooseTermReducer.class);
        job1.setReducerClass(ChooseTermReducer.class);
        job1.setMapOutputKeyClass(Text.class);
        job1.setMapOutputValueClass(IntWritable.class);
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job1, new Path(argPath));
        FileOutputFormat.setOutputPath(job1, new Path(FilePaths.Choose_Term_Result));

        //删除已存在输出目录
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path(FilePaths.Choose_Term_Result);
        if (fs.exists(path)){
            fs.delete(path, true);
        }
    }

    public ChooseTermTask(Configuration conf, String[] args) throws Exception{
        this.conf = conf;
        jc = new JobControl("chooseTermAndSort");
        confChooseJob(args[0]);
        confSortJob();

        ControlledJob chooseJob = new ControlledJob(conf);
        chooseJob.setJob(job1);

        ControlledJob sortJob = new ControlledJob(conf);
        sortJob.setJob(job2);
        sortJob.addDependingJob(chooseJob);

        jc.addJob(chooseJob);
        jc.addJob(sortJob);
    }

    public void execute() throws Exception{
        Thread t = new Thread(jc);
        t.start();
        while(true){
            if (jc.allFinished()){break;}
        }
        System.out.println("choose Term Done");
//        System.exit(0);
//        job1.waitForCompletion(true);
//        job2.waitForCompletion(true);
    }
}
