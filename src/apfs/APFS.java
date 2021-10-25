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
        // the length 1000 is a random no. big enough for now.. Change later to actual no
        ByteBuffer nodeBuffer = Utils.GetBuffer(pathname, (int) btree_omap_oid, 4096);

        // Expected Bytes: B0 74 1F 1E
//        byte[] nodeBytes = new byte[nodeBuffer.remaining()];
//        nodeBuffer.get(nodeBytes);
//        System.out.println("\n\n" + Utils.OriginalBytesToHexString(nodeBytes));

        BlockHeader rootNodeHeader = new BlockHeader(nodeBuffer);
        System.out.println(rootNodeHeader);

        BTreeNode rootNode = new BTreeNode(nodeBuffer);
        System.out.println(rootNode);


        // TODO: Find Volume Superblock using an offset from the Container Superblock.
        // IMPORTANT: The following volume superblock uses the incorrect offset. We need to fix this.
//        volumeSb = APFSVolume.parseVolume(pathname, (int) (containerSb.nx_next_oid * containerSb.nx_block_size), containerSb.nx_block_size);
        ByteBuffer volumeSbBuffer = Utils.GetBuffer(pathname, (int) rootNode.volume_superb_offset * 4096, 4096);

        byte[] bytes = new byte[volumeSbBuffer.remaining()];

        System.out.println(


                Utils.OriginalBytesToHexString(bytes)
        );
        APFSVolume volumeSb = new APFSVolume(volumeSbBuffer);
        System.out.println(volumeSb);
    }

    @Override
    public String toString() {
        return "APFS{" +
                "containerSb=" + containerSb +
                ", \n\nvolumeSb=" + volumeSb +
                '}';
    }
}
