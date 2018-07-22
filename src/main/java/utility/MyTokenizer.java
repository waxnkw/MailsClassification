package utility;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author zhangao
 * @version 7.16
 * 分词器的单例模式分词器
 * */
public class MyTokenizer {
    private static MyTokenizer instance;
    private CharArraySet cas;
    private String[] self_stop_words;
    private StandardAnalyzer sa;

    private MyTokenizer(){
        self_stop_words = StopWords.Stop_Words;
        cas = new CharArraySet(Version.LUCENE_44, 0, true);
        for (int i = 0; i < self_stop_words.length; i++) {
            cas.add(self_stop_words[i]);
        }
        // 加入系统默认停用词
        Iterator<Object> itor = StandardAnalyzer.STOP_WORDS_SET.iterator();
        while (itor.hasNext()) {
            cas.add(itor.next());
        }
        // 标准分词器(Lucene内置的标准分析器,会将语汇单元转成小写形式，并去除停用词及标点符号)
        sa = new StandardAnalyzer(Version.LUCENE_44, cas);
    }

    public static MyTokenizer getInstance(){
        if (instance==null){instance = new MyTokenizer();}
        return instance;
    }

    public List<String> tokenize(String text){
        List<String> ret = new ArrayList<String>();
        try {
            TokenStream ts = sa.tokenStream("field", text);
            CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                ret.add(ch.toString());
            }
            ts.end();
            ts.close();
        }catch (IOException ex){ex.printStackTrace();}
        return ret;
    }
}
