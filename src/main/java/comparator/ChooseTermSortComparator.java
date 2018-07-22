package comparator;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import writable.ClsMarkKey;

/**
 * @author zhangao
 * @version 2018.7.21
 * 分词时用来按照<cls#mark>的key按照 cls升序,mark降序 排序
 * */
public class ChooseTermSortComparator extends WritableComparator {
    protected ChooseTermSortComparator() {
        super(ClsMarkKey.class, true);
    }

    @Override
    public int compare(WritableComparable wc1, WritableComparable wc2){
        ClsMarkKey c1 = (ClsMarkKey)wc1;
        ClsMarkKey c2 = (ClsMarkKey)wc2;
        return -c1.compareTo(c2);
    }
}
