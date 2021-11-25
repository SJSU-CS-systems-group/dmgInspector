package apfs;

import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

public class APFS {
    private APFSContainer containerSb;
    private APFSVolume volumeSb;

    public APFS(String imagePath) throws IOException {
        // Parse the Container Superblock (CSB)
        containerSb = APFSContainer.parseContainer(imagePath);
        int blockSize = containerSb.nx_block_size;

        // Get the CSB Object Map (OMAP), which maps OIDs to values containing a physical address
        // nx_omap_oid is a physical address
        int csbOmapOffset = (int) containerSb.nx_omap_oid * blockSize;
        ByteBuffer csbOMapBuffer = Utils.GetBuffer(imagePath, csbOmapOffset, blockSize);
        OMap csbOMap = new OMap(csbOMapBuffer, imagePath, blockSize);
        System.out.println(csbOMap);

        // Parse the Volume Superblock (VSB)
        // Get the VSB physical address by searching for nx_fs_oid in the CSB OMAP
        // nx_fs_oid: contains OIDs for VSBs - see APFS Reference pg. 32
        int vsbOffset = csbOMap.parsedOmap.get(containerSb.nx_fs_oid).intValue() * blockSize;
        ByteBuffer volumeSbBuffer = Utils.GetBuffer(imagePath, vsbOffset, blockSize);
        APFSVolume volumeSb = new APFSVolume(volumeSbBuffer);

        // Parse the VSB OMap
        // VSB apfs_omap_oid field is the physical block number (See APFS Reference pg. 55)
        // TODO: Finish OMAP BTree Parsing in OMap.java -- right now, we're only parsing the root node
        // TODO: MUST parse child nodes! If we don't, our values will be wrong.
        // BTREE structure (Many Files.dmg APFS image)!
        // Start at VSB OMap's BTree Root Node (oid 5403)

        // 0                [1028, 1139]
        // 1  [1028, 1030 to 1139]   [1139 to 1145]

        //  -btn_level = 1, meaning it has 1 level of child nodes below it
        //  -contains BTree Nodes with OMAP keys (oids 1028 and 1139)
        //          1028's children are Leaf Nodes: oids 1028, 1030 to 1139
        //          1139's children are Leaf Nodes: oids 1139 to 1145
        //              We want to add each leaf node to the OMAP BTree -- their paddr's point us to actual FS Objects

        int vsbOMapOffset = (int) volumeSb.apfs_omap_oid * blockSize;
        System.out.println(volumeSb.apfs_omap_oid);
        ByteBuffer vsbOMapBuffer = Utils.GetBuffer(imagePath, vsbOMapOffset, blockSize);
        OMap vsbOMap = new OMap(vsbOMapBuffer, imagePath, blockSize);
        System.out.println(vsbOMap);

        // TODO: Since we're not parsing child nodes, oid 1028 maps to 68719476752 -- should be 5393.
        // (1028 parent node has a child w/ ID 1028, which has the paddr we want)

        int fsTreeOffset = vsbOMap.parsedOmap.get(volumeSb.apfs_root_tree_oid).intValue() * blockSize;
        ByteBuffer vsbRootNodeBuffer = Utils.GetBuffer(imagePath, fsTreeOffset, blockSize);
        BTreeNode vsbRootNode = new BTreeNode(vsbRootNodeBuffer);

        int offset = 0; // PLACEHOLDER
        ByteBuffer inodeBTreeRootNodeBuffer = Utils.GetBuffer(imagePath, offset, blockSize);
        BTreeNode inodeBTreeRootNode = new BTreeNode(inodeBTreeRootNodeBuffer);
        System.out.println("\n\n\n");


        // TODO: Parse BTree Nodes to get all inode, extent, drec

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
                    if (!drecRecords.containsKey(fskv.key.hdr.obj_id)) {
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
        for (FSKeyValue fsKeyValue : possible_roots) {
            DRECValue value = (DRECValue) fsKeyValue.value;
            if (value.fileId == 2) {
                root = fsKeyValue;
                break;
            }
        }

        // See APFS reference page 100
        // DREC record flags will tell us whether an entry is a Directory or a regular File.
        int DT_DIR = 4;
        int DT_FILE = 8;

        queue.add(new Tuple<>(root, "/"));
        while (!queue.isEmpty()) {
            Tuple<FSKeyValue, String> tuple = queue.removeFirst();
            FSKeyValue curr = tuple.x;
            String path = tuple.y;

            System.out.println("\n" + path + " -> " + curr);

            DRECKey key = (DRECKey) curr.key;
            DRECValue value = (DRECValue) curr.value;

            File folder = new File("./output" + path);
            if (!folder.exists()) {
                folder.mkdir();
            }

            if (value.flags == DT_FILE) {
                // No new files to add since this record is a Regular File
                // Find related extent
                FSKeyValue extentKv = extentRecords.get(value.fileId);
                EXTENTKey extentKey = (EXTENTKey) extentKv.key;
                EXTENTValue extentValue = (EXTENTValue) extentKv.value;

                System.out.println(extentValue);

                // TODO: Parse file from the extent
                String name = new String(key.name);
                name = name.substring(0, name.length() - 1); // Remove null terminator character
                String fileOutPath = "./output" + path + name;
                System.out.println("\n" + fileOutPath + " -> ");
                Utils.extentRangeToFile(imagePath, fileOutPath, extentValue.physBlockNum * blockSize, extentValue.length);

                continue;
            }

            // Add new files to the queue since this record is a Directory

            ArrayList<FSKeyValue> children = drecRecords.get(value.fileId);
            if (children != null) {
                for (FSKeyValue child : children) {
                    String name = new String(key.name);
                    name = name.substring(0, name.length() - 1); // Remove null terminator character
                    queue.add(new Tuple<>(child, path + name + "/"));
                }
            }

            //
            //
        }

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