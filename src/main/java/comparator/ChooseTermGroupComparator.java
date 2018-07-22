package comparator;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import writable.ClsMarkKey;

/**
 * @author zhangao
 * @version 2018.7.21
 * 分词时用来将<cls#mark>的key按照cls分类
 * */
public class ChooseTermGroupComparator extends WritableComparator {
    protected ChooseTermGroupComparator() {
        super(ClsMarkKey.class, true);
    }

    @Override
    public int compare(WritableComparable wc1, WritableComparable wc2){
        ClsMarkKey c1 = (ClsMarkKey)wc1;
        ClsMarkKey c2 = (ClsMarkKey)wc2;
        return c1.getCls().compareTo(c2.getCls());
    }
}
