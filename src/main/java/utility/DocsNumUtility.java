package utility;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangao
 * @version 7.16
 * 得到每一个cls下doc数目和总的数目的utility
 * */
public class DocsNumUtility {
    private static  Map<String, Integer> docs_map ;
    private static int total_num = 19997;

    /**
     * 初始化存储的map
     * */
    private static void initMap(){
        docs_map = new HashMap<String, Integer>();
        docs_map.put("sci.crypt", 1000);
        docs_map.put("rec.sport.hockey", 1000);
        docs_map.put("alt.atheism", 1000);
        docs_map.put("soc.religion.christian", 997);
        docs_map.put("comp.sys.mac.hardware", 1000);
        docs_map.put("comp.os.ms-windows.misc", 1000);
        docs_map.put("sci.electronics", 1000);
        docs_map.put("comp.windows.x", 1000);
        docs_map.put("rec.sport.baseball", 1000);
        docs_map.put("talk.politics.guns", 1000);
        docs_map.put("misc.forsale", 1000);
        docs_map.put("talk.politics.mideast", 1000);
        docs_map.put("rec.autos", 1000);
        docs_map.put("comp.sys.ibm.pc.hardware", 1000);
        docs_map.put("comp.graphics", 1000);
        docs_map.put("talk.religion.misc", 1000);
        docs_map.put("talk.politics.misc", 1000);
        docs_map.put("rec.motorcycles", 1000);
        docs_map.put("sci.med", 1000);
        docs_map.put("sci.space", 1000);
    }

    /**
     * 得到当前种类的邮件总数目
     * */
    public static int getDocNumsOfClass(String className){
        if (docs_map==null){initMap();}
        return docs_map.get(className);
    }

    /**
     * 得到所有邮件的总数
     * */
    public static int getTotalNumOfDocs(){
        return total_num;
    }

}
