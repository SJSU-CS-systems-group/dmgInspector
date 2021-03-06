package apfs.kv;


import apfs.kv.keys.FSObjectKey;
import apfs.kv.values.FSObjectValue;

public class FSKeyValue {
    public FSObjectKey key;
    public FSObjectValue value;

    public FSKeyValue(FSObjectKey key, FSObjectValue value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "\tkey=" + key +
                "\n\t\tvalue=" + value + "\n";
    }
}
