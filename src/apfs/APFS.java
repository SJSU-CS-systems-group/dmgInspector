package apfs;

import utils.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

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
//        System.out.println(rootNode);

        // 3. Parse the Volume Superblock using info from the CSB BTree
        // TODO: Write general case to parse keys & values
        ByteBuffer volumeSbBuffer = Utils.GetBuffer(pathname, (int) rootNode.bTreeValues.get(0).paddr_t * blockSize, blockSize);
        APFSVolume volumeSb = new APFSVolume(volumeSbBuffer);
//        System.out.println(volumeSb);
//
//        // 4. Parse VSB OMap
        ByteBuffer vsbOMapBuffer = Utils.GetBuffer(pathname, (int) volumeSb.apfs_omap_oid * blockSize, blockSize);
        OMap vsbOMap = new OMap(vsbOMapBuffer);
//        System.out.println(vsbOMap);

        // 5. Parse VSB B-Tree
        ByteBuffer vsbRootNodeBuffer = Utils.GetBuffer(pathname, (int) vsbOMap.om_tree_oid * blockSize, blockSize);
        BTreeNode vsbRootNode = new BTreeNode(vsbRootNodeBuffer);
        System.out.println(vsbRootNode);

        // 6. Parse FS Object B Tree
        // TODO: Actually parse B-Tree. We also need to parse OMAP BTrees properly.
        ByteBuffer inodeBTreeRootNodeBuffer = Utils.GetBuffer(pathname, (int) vsbRootNode.bTreeValues.get(0).paddr_t * blockSize, blockSize);
        BTreeNode inodeBTreeRootNode = new BTreeNode(inodeBTreeRootNodeBuffer);
        System.out.println("\n\n\n");
        System.out.println(inodeBTreeRootNode);

        // 7. Organize FS Objects by record object identifiers
        HashMap<Long, FSKeyValue> inodeRecords = new HashMap<>();
        HashMap<Long, FSKeyValue> extentRecords = new HashMap<>();
        HashMap<Long, ArrayList<FSKeyValue>> drecRecords = new HashMap<>();

        for (FSKeyValue fskv : inodeBTreeRootNode.fsKeyValues) {
            int type = (int) fskv.key.hdr.obj_type;
            switch (type) {
                case FSObjectKeyFactory.KEY_TYPE_INODE:
                    inodeRecords.put(fskv.key.hdr.obj_id, fskv);
                    break;
                case FSObjectKeyFactory.KEY_TYPE_EXTENT:
                    extentRecords.put(fskv.key.hdr.obj_id, fskv);
                    break;
                case FSObjectKeyFactory.KEY_TYPE_DREC:
                    if (!drecRecords.containsKey(fskv.key.hdr.obj_id)){
                        drecRecords.put(fskv.key.hdr.obj_id, new ArrayList<>());
                    }
                    drecRecords.get(fskv.key.hdr.obj_id).add(fskv);
                    break;
            }
        }

        // a. Start parsing from Root Directory -- will always have inode number of 1
        ArrayDeque<Tuple<FSKeyValue, String>> queue = new ArrayDeque<>();
        // get the root folder
        ArrayList<FSKeyValue> possible_roots = drecRecords.get(1L);
        FSKeyValue root = null;
        for (FSKeyValue fsKeyValue : possible_roots){
            DRECValue value = (DRECValue) fsKeyValue.value;
            if (value.fileId == 2){
                root = fsKeyValue;
                break;
            }
        }
        queue.add(new Tuple<>(root, "/"));
        while (!queue.isEmpty()){
            Tuple<FSKeyValue, String> tuple = queue.removeFirst();
            FSKeyValue curr = tuple.x;
            String path = tuple.y;

            int type = (int) curr.key.hdr.obj_type;
            switch (type) {
                case FSObjectKeyFactory.KEY_TYPE_INODE:
//                    inodeRecords.put(curr.key.hdr.obj_id, fskv);
                    break;
                case FSObjectKeyFactory.KEY_TYPE_EXTENT:
//                    extentRecords.put(fskv.key.hdr.obj_id, fskv);
                    break;
                case FSObjectKeyFactory.KEY_TYPE_DREC:
                    System.out.println(path + " -> " + curr);
                    DRECKey key = (DRECKey) curr.key;
                    DRECValue value = (DRECValue) curr.value;
                    ArrayList<FSKeyValue> children = drecRecords.get(value.fileId);
                    for (FSKeyValue child : children) {
                        queue.add(new Tuple<>(child, path + key.name + "/"));
                    }
                    break;


            }
        }


        // 8. Parse files according to FS Object records
        // TODO: build directory structure w/ files
    }

    @Override
    public String toString() {
        return "APFS{" +
                "containerSb=" + containerSb +
                ", \n\nvolumeSb=" + volumeSb +
                '}';
    }
}

// Single Files
// Map Object Identifier -> Inode Records
// Map Object Identifier -> Extend Records

// Directories
// Map Object Identifier -> Drec Records

class Tuple<X, Y> {
    public final X x;
    public final Y y;
    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}
// https://stackoverflow.com/questions/2670982/using-pairs-or-2-tuples-in-java