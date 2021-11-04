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
        BTreeNode rootNode = new BTreeNode(rootNodeBuffer);
        System.out.println(rootNode);

        // 3. Parse the Volume Superblock using info from the CSB BTree
        // TODO: Write general case to parse keys & values
        ByteBuffer volumeSbBuffer = Utils.GetBuffer(pathname, (int) rootNode.bTreeValues.get(0).paddr_t * blockSize, blockSize);
        APFSVolume volumeSb = new APFSVolume(volumeSbBuffer);
        System.out.println(volumeSb);
//
//        // 4. Parse VSB OMap
        ByteBuffer vsbOMapBuffer = Utils.GetBuffer(pathname, (int) volumeSb.apfs_omap_oid * blockSize, blockSize);
        OMap vsbOMap = new OMap(vsbOMapBuffer);
        System.out.println(vsbOMap);
//
//        // 5. Parse VSB B-Tree
        ByteBuffer vsbRootNodeBuffer = Utils.GetBuffer(pathname, (int) vsbOMap.om_tree_oid * blockSize, blockSize);
        BTreeNode vsbRootNode = new BTreeNode(vsbRootNodeBuffer);
        System.out.println("RIGHT HERE");
        System.out.println("RIGHT HERE");
        System.out.println(vsbRootNode);
        // TODO: bad BTreeValues for volume root node bTreeValues=[BTreeValue{ov_flags=18, ov_size=4096, paddr_t=68719476752}]}. expected (flags = 0, size = 4096, paddr_t = 3734)
        // 6. Parse the Root B-Tree using VSB B-Tree's first value (we know it's the value corresponding to the entry with a key equal to btree_root_oid)
        // This is the B-Tree of Inodes.
//        // TODO: Again, we need to make a general case for parsing keys & values instead of getting just the first value
//        ByteBuffer rootBTreeBuffer = Utils.GetBuffer(pathname, (int) vsbRootNode.first_value_offset * blockSize, blockSize);
//        BTreeNode rootBTreeRootNode = new BTreeNode(rootBTreeBuffer);
//        System.out.println("RIGHT HERE");
//        System.out.println(rootBTreeRootNode);

        // 7. TODO: Parse Inodes
    }

    @Override
    public String toString() {
        return "APFS{" +
                "containerSb=" + containerSb +
                ", \n\nvolumeSb=" + volumeSb +
                '}';
    }
}
