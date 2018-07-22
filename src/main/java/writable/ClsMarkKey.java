package writable;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author zhangao
 * @version 2018.7.21
 * 自定义<cls,mark>形式key
 * */
public class ClsMarkKey implements WritableComparable<ClsMarkKey> {
    private String cls;
    private double mark;

    public int compareTo(ClsMarkKey o) {
        if (cls.compareTo(o.getCls())!=0){return cls.compareTo(o.cls);}
        return Double.compare(mark, o.mark);
    }

    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(cls);
        dataOutput.writeDouble(mark);
    }

    public void readFields(DataInput dataInput) throws IOException {
        cls = dataInput.readUTF();
        mark = dataInput.readDouble();
    }




    public double getMark() {
        return mark;
    }

    public String getCls(){
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }
}
