package utility;

import java.util.Map;

/**
 * 自定义pair
 * */
public class MyEntry<K,V>implements Map.Entry<K,V> {
    private K key;
    private V value;

    public MyEntry(K k, V v){
        this.key = k;
        this.value = v;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public V setValue(V value) {
        this.value = value;
        return value;
    }
}
