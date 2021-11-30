package apfs.kv.keys;

import java.nio.ByteBuffer;

// OMAP Key structure for fixed-length keys -- see APFS reference pg. 46
public class OMAPKey implements Comparable<OMAPKey> {
    public long ok_oid;
    public long ok_xid;

    public OMAPKey(ByteBuffer buffer) {
        ok_oid = buffer.getLong();
        ok_xid = buffer.getLong();
    }


    @Override
    public int compareTo(OMAPKey o) {
        return (int) (this.ok_oid - o.ok_oid);
    }

    @Override
    public String toString() {
        return "OMapKey{" +
                "ok_oid=" + ok_oid +
                ", ok_xid=" + ok_xid +
                '}';
    }
}


// See object types at APFS refrence pg.84


// Later? (Can ignore for now since we're just interested in parsing Inodes)
// TODO: Parse x-attr (4)
// TODO: Parse dstream (4)


