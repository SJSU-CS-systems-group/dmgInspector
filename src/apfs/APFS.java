package apfs;

import utils.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;

public class APFS {
    private APFSContainer containerSb;
    private APFSVolume volumeSb;

    public APFS(String pathname) throws IOException {
        containerSb = APFSContainer.parseContainer(pathname);


        // 1. Access BTree Omap via its OID from the Container Superblock
        // 2. OID OMAP OID + 1 workaround to find BTREE Root Node
        // TODO: Later: Find BTree using actual oid from omap_phys_t structure
        // IMPORTANT: this +1 likely only works for our test case
        long btree_omap_oid = (containerSb.nx_omap_oid + 1) * containerSb.nx_block_size;
        ByteBuffer nodeBuffer = Utils.GetBuffer(pathname, (int) btree_omap_oid, 1000);

        // Expected Bytes: B0 74 1F 1E
//        byte[] nodeBytes = new byte[nodeBuffer.remaining()];
//        nodeBuffer.get(nodeBytes);
//        System.out.println("\n\n" + Utils.OriginalBytesToHexString(nodeBytes));

        BlockHeader rootNodeHeader = new BlockHeader(nodeBuffer);
        System.out.println(rootNodeHeader);

        // 3. Parse B-Tree structure
        // Try contiguous space after root node?
        // Use node size and node count as specified by root node BTree Info

        // TODO: Find Volume Superblock using an offset from the Container Superblock.
        // IMPORTANT: The following volume superblock uses the incorrect offset. We need to fix this.
//        volumeSb = APFSVolume.parseVolume(pathname, (int) (containerSb.nx_next_oid * containerSb.nx_block_size), containerSb.nx_block_size);
    }

    @Override
    public String toString() {
        return "APFS{" +
                "containerSb=" + containerSb +
                ", \n\nvolumeSb=" + volumeSb +
                '}';
    }
}
