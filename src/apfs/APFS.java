package apfs;

import utils.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;

public class APFS {
    private APFSContainer containerSb;
    private APFSVolume volumeSb;

    public APFS(String pathname) throws IOException {
        containerSb = APFSContainer.parseContainer(pathname);

        int blockSize = containerSb.nx_block_size;

        // 1. Access CSB OMAP via its OID from the Container Superblock
        ByteBuffer csbOMapBuffer = Utils.GetBuffer(pathname, (int) containerSb.nx_omap_oid * blockSize, blockSize);
        OMap csbOMap = new OMap(csbOMapBuffer);

        // 2. Find the CSB BTree using an OID provided by the CSB OMAP
        ByteBuffer rootNodeBuffer = Utils.GetBuffer(pathname, (int) csbOMap.om_tree_oid * blockSize, blockSize);
        BlockHeader rootNodeHeader = new BlockHeader(rootNodeBuffer);
        System.out.println(rootNodeHeader);

        BTreeNode rootNode = new BTreeNode(rootNodeBuffer);
        System.out.println(rootNode);

        // 3. Parse the Volume Superblock using info from the CSB BTree
        // TODO: Write general case to parse keys & values
        ByteBuffer volumeSbBuffer = Utils.GetBuffer(pathname, (int) rootNode.volume_superb_offset * blockSize, blockSize);
        byte[] bytes = new byte[volumeSbBuffer.remaining()];
        APFSVolume volumeSb = new APFSVolume(volumeSbBuffer);
        System.out.println(volumeSb);

        // 4. Parse VSB OMap
        ByteBuffer vsbOMapBuffer = Utils.GetBuffer(pathname, (int) volumeSb.apfs_omap_oid * blockSize, blockSize);
        System.out.println("RIGHT HERE");
        OMap vsbOMap = new OMap(vsbOMapBuffer);
        System.out.println(vsbOMap);

        // 5. Parse VSB B-Tree
    }

    @Override
    public String toString() {
        return "APFS{" +
                "containerSb=" + containerSb +
                ", \n\nvolumeSb=" + volumeSb +
                '}';
    }
}
