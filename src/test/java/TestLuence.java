import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import utility.StopWords;

import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

public class TestLuence {
    static String text = "Xref: cantaloupe.srv.cs.cmu.edu alt.atheism:51148 soc.motss:139981 rec.scouting:5323\n" +
            "Path: cantaloupe.srv.cs.cmu.edu!rochester!news.bbn.com!noc.near.net!uunet!wupost!CSM560.smsu.edu!umn.edu!lynx.unm.edu!carina.unm.edu!anthropo\n" +
            "From: anthropo@carina.unm.edu (Dominick V. Zurlo)\n" +
            "Newsgroups: alt.atheism,soc.motss,rec.scouting\n" +
            "Subject: Re: [soc.motss, et al.] \"Princeton axes matching funds for Boy Scouts\"\n" +
            "Date: 5 Apr 1993 20:27:59 GMT\n" +
            "Organization: University of New Mexico, Albuquerque\n" +
            "Lines: 30\n" +
            "Message-ID: <1pq4ofINNr2i@lynx.unm.edu>\n" +
            "References: <1osnh5INNllm@hoss.usl.com> <1pc81b$4p7@shrike.und.ac.za> <1993Apr5.011255.7295@cbnewsl.cb.att.com>\n" +
            "NNTP-Posting-Host: carina.unm.edu\n" +
            "\n" +
            "In article <1993Apr5.011255.7295@cbnewsl.cb.att.com> stank@cbnewsl.cb.att.com (Stan Krieger) writes:\n" +
            ">Now can we please use rec.scouting for the purpose for which it was\n" +
            ">established?  Clearly we netnews voters decided that we did not want to\n" +
            ">provide a scouting newsgroup to give fringe groups a forum for their\n" +
            ">anti-societal political views.\n" +
            "\n" +
            "Ok, this is the only thing I will comment on from Stan at this time...\n" +
            "part of this forum we call rec.scouting is for policy discussions and\n" +
            "related topics.  This is a policy discussion, and involves related \n" +
            "topics.  this is not a \"fringe\" group discussion.  obviously, it \n" +
            "engenders strong feelings from all sides of the issues at hand. \n" +
            "Wether a particular view is anti-societal or not is your opinion, \n" +
            "and yours alone, don't try to make it seem otherwise. \n" +
            "If you do not wish to engage in this discussion, use a kill file. \n" +
            "If you wish to continue in this discussion, please do so, knowing \n" +
            "full well the implications that apply.\n" +
            "I know for myself that I plan on continuing with the discussion when \n" +
            "i have the wish to have input.  I for one am tired of people trying to \n" +
            "say that this is not a matter significant for this group!  It is, and \n" +
            "quite so. Especially for those of us who feel the impact more closely.\n" +
            "\n" +
            "\n" +
            "****************************************************************\n" +
            "*  Dominick V. Zurlo              *    \"If the world's an      *\n" +
            "*  WWW                            *    oyster, why am I        *\n" +
            "*  Eagle Scout '87                *    allergic to Mollusks?\"  *\n" +
            "*  blacklisted '88                *                            *\n" +
            "****************************************************************\n" +
            "\n";

    private static void testLuence() throws IOException {
        // 自定义停用词
        String[] self_stop_words = StopWords.Stop_Words;
        CharArraySet cas = new CharArraySet(Version.LUCENE_44, 0, true);
        for (int i = 0; i < self_stop_words.length; i++) {
            cas.add(self_stop_words[i]);
        }

        // 加入系统默认停用词
        Iterator<Object> itor = StandardAnalyzer.STOP_WORDS_SET.iterator();
        while (itor.hasNext()) {
            cas.add(itor.next());
        }

        // 标准分词器(Lucene内置的标准分析器,会将语汇单元转成小写形式，并去除停用词及标点符号)
        StandardAnalyzer sa = new StandardAnalyzer(Version.LUCENE_44, cas);

        TokenStream ts = sa.tokenStream("field", text);
        CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);

        ts.reset();
        while (ts.incrementToken()) {
            System.out.println(ch.toString());
        }
        ts.end();
        ts.close();
    }

    public static void main(String[] args) {
//        try {
//            testLuence();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
