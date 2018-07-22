package writable;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author zhangao
 * @version 2018.7.21
 * 自定义<term,mark>形式value
 * */
public class TermMarkVal implements WritableComparable<TermMarkVal> {
    private String term;
    private double mark;//miao a miao a

    public int compareTo(TermMarkVal o) {
        return term.compareTo(o.term);
    }

    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(term);
        dataOutput.writeDouble(mark);
    }

    public void readFields(DataInput dataInput) throws IOException {
        term = dataInput.readUTF();
        mark = dataInput.readDouble();
    }

    public double getMark() {
        return mark;
    }

    public String getTerm() {
        return term;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
