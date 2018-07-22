import org.apache.hadoop.conf.Configuration;
import tasks.ChooseTermTask;
import tasks.TestTask;
import tasks.TrainTask;
import tasks.TrainVectorizeTask;

public class Main {

    private static void runChooseTermTask(String mode,String[] args)throws Exception{
        ChooseTermTask task = new ChooseTermTask(new Configuration(), args);
        task.execute();
    }

    private static void runTrainVectorizeTask(String[] args) throws Exception{
        TrainVectorizeTask trainVectorizeTask = new TrainVectorizeTask(args);
        trainVectorizeTask.execute();
    }

    private static void runTrainTask() throws Exception{
        TrainTask trainTask = new TrainTask(null);
        trainTask.execute();
    }

    private static void runTestVectorizeTask(String[]args)throws Exception{
        TrainVectorizeTask testVectorizeTask = new TrainVectorizeTask(args);
        testVectorizeTask.execute();
    }

    private static void runTestAccTask() throws Exception{
        TestTask testTask = new TestTask(null);
        testTask.execute();
    }

    private static void runTestTrainAccTask()throws Exception{
        TestTask testTask = new TestTask(null);
        testTask.execute();
    }

    /**
     * @param args 0: 命令模式
     * @param args 1: 输入train
     * @param args 2: 输入test路径
     * */
    public static void main(String[] args) throws Exception{
        if (args[0].equals("all")){
            runChooseTermTask("both",new String[]{args[1]});
            runTrainVectorizeTask(new String[]{args[1]});
            runTrainTask();
            runTestVectorizeTask(new String[]{args[2]});
            runTestAccTask();
        }
        else if (args[0].equals("choose")){
            runChooseTermTask("choose",new String[]{args[1]});
        }
        else if (args[0].equals("sort")){
            runChooseTermTask("sort",new String[]{args[1]});
        }
        else if (args[0].equals("term")){
            runChooseTermTask("both",new String[]{args[1]});
        }
        else if (args[0].equals("train")){
            runTrainVectorizeTask(new String[]{args[1]});
            runTrainTask();
        }
        else if (args[0].equals("test")){
            runTestAccTask();
        }

    }
}