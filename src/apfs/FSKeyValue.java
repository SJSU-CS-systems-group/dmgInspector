package apfs;

public class FSKeyValue {
    public FSObjectKey key;
    public FSObjectValue value;

    public FSKeyValue(FSObjectKey key, FSObjectValue value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "\n" +
                "FSKeyValue{" +
                "key=" + key +
                ", value=" + value +
                "}";
    }
}
