package apfs;

import utils.Tuple;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class APFSVolume {
    // "root" dir will always have an inode number of 1
    // See APFS reference pg 96
    public static final long ROOT_DIR_OID = 1L;

    // DREC record flags will tell us whether an entry is a Directory or a regular File.
    // See APFS reference page 100
    int DT_DIR = 4;
    int DT_FILE = 8;

    private int blockSize;
    private String imagePath;

    // Volume fields
    public BlockHeader blockHeader;
    public byte[] apfs_magic = new byte[4];
    public int apfs_fs_index;
    public long apfs_features;
    public long apfs_readonly_compatible_features;
    public long apfs_incompatible_features;
    public long apfs_unmount_time;
    public long apfs_fs_reserve_block_count;
    public long apfs_fs_quota_block_count;
    public long apfs_fs_alloc_count;
    public short wrapped_crypto_state_t_major_version;
    public short wrapped_crypto_state_t_minor_version;
    public int wrapped_crypto_state_t_cpflags;
    public int wrapped_crypto_state_t_persistent_class;
    public int wrapped_crypto_state_t_key_os_version;
    public short wrapped_crypto_state_t_key_revision;
    public short wrapped_crypto_state_t_key_len;
    public int apfs_root_tree_oid_type;
    public int apfs_extentref_tree_oid_type;
    public int apfs_snap_meta_tree_oid_type;
    public long apfs_omap_oid;
    public long apfs_root_tree_oid;
    public long apfs_extentref_tree_oid;
    public long apfs_snap_meta_tree_oid;
    public long apfs_revert_to_xid;
    public long apfs_revert_to_sblock_oid;
    public long apfs_next_obj_id;
    public long apfs_num_files;
    public long apfs_num_directories;
    public long apfs_num_symlinks;
    public long apfs_num_other_fsobjects;
    public long apfs_num_snapshots;
    public long apfs_total_blocks_alloced;
    public long apfs_total_blocks_freed;
    public byte[] apfs_vol_uuid = new byte[16];
    public long apfs_last_mod_time;
    public long apfs_fs_flags;
    public byte[] apfs_modified_by_t_formatted_by_id = new byte[32];
    public long apfs_modified_by_t_formatted_by_timestamp;
    public long apfs_modified_by_t_formatted_by_last_xid;
    public byte[] apfs_modified_by_t_modified_by_id = new byte[32];
    public long apfs_modified_by_t_modified_by_timestamp;
    public long apfs_modified_by_t_modified_by_last_xid;
    public byte[] apfs_modified_by_t_modified_by_1_7 = new byte[336];
    public byte[] apfs_volname = new byte[256];
    public int apfs_next_doc_id;
    public short apfs_role;
    public short apfs_reserved;
    public long apfs_root_to_xid;
    public long apfs_er_state_oid;

    public OMap volumeOMap;

    public HashMap<Long, FSKeyValue> inodeRecords = new HashMap<>();
    public HashMap<Long, FSKeyValue> extentRecords = new HashMap<>();
    public HashMap<Long, ArrayList<FSKeyValue>> drecRecords = new HashMap<>();
    public ArrayList<Tuple<String, EXTENTValue>> files = new ArrayList<>();

    public APFSVolume(ByteBuffer buffer, int blockSize, String imagePath) throws IOException {
        this.blockSize = blockSize;
        this.imagePath = imagePath;

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        blockHeader = new BlockHeader(buffer);
        buffer.get(apfs_magic);
        apfs_fs_index = buffer.getInt();
        apfs_features = buffer.getLong();
        apfs_readonly_compatible_features = buffer.getLong();
        apfs_incompatible_features = buffer.getLong();
        apfs_unmount_time = buffer.getLong();
        apfs_fs_reserve_block_count = buffer.getLong();
        apfs_fs_quota_block_count = buffer.getLong();
        apfs_fs_alloc_count = buffer.getLong();
        wrapped_crypto_state_t_major_version = buffer.getShort();
        wrapped_crypto_state_t_minor_version = buffer.getShort();
        wrapped_crypto_state_t_cpflags = buffer.getInt();
        wrapped_crypto_state_t_persistent_class = buffer.getInt();
        wrapped_crypto_state_t_key_os_version = buffer.getInt();
        wrapped_crypto_state_t_key_revision = buffer.getShort();
        wrapped_crypto_state_t_key_len = buffer.getShort();
        apfs_root_tree_oid_type = buffer.getInt();
        apfs_extentref_tree_oid_type = buffer.getInt();
        apfs_snap_meta_tree_oid_type = buffer.getInt();
        apfs_omap_oid = buffer.getLong();
        apfs_root_tree_oid = buffer.getLong();
        apfs_extentref_tree_oid = buffer.getLong();
        apfs_snap_meta_tree_oid = buffer.getLong();
        apfs_revert_to_xid = buffer.getLong();
        apfs_revert_to_sblock_oid = buffer.getLong();
        apfs_next_obj_id = buffer.getLong();
        apfs_num_files = buffer.getLong();
        apfs_num_directories = buffer.getLong();
        apfs_num_symlinks = buffer.getLong();
        apfs_num_other_fsobjects = buffer.getLong();
        apfs_num_snapshots = buffer.getLong();
        apfs_total_blocks_alloced = buffer.getLong();
        apfs_total_blocks_freed = buffer.getLong();
        buffer.get(apfs_vol_uuid);
        apfs_last_mod_time = buffer.getLong();
        apfs_fs_flags = buffer.getLong();
        buffer.get(apfs_modified_by_t_formatted_by_id);
        apfs_modified_by_t_formatted_by_timestamp = buffer.getLong();
        apfs_modified_by_t_formatted_by_last_xid = buffer.getLong();
        buffer.get(apfs_modified_by_t_modified_by_id);
        apfs_modified_by_t_modified_by_timestamp = buffer.getLong();
        apfs_modified_by_t_modified_by_last_xid = buffer.getLong();
        buffer.get(apfs_modified_by_t_modified_by_1_7);
        buffer.get(apfs_volname);
        apfs_next_doc_id = buffer.getInt();
        apfs_role = buffer.getShort();
        apfs_reserved = buffer.getShort();
        apfs_root_to_xid = buffer.getLong();
        apfs_er_state_oid = buffer.getLong();
        volumeOMap = getVolumeOMap();
        files = getFiles();
        printFiles();
        extractAllFiles();
    }

    public void printFiles() {
        for(int c = 0; c < files.size(); c++) {
            System.out.printf("File Path: %-45sExtent Value: %s\n", files.get(c).x, files.get(c).y);
        }
    }

    public void extractFile(int fileId) throws IOException{
        Tuple<String, EXTENTValue> fileToExtract = files.get(fileId);
        EXTENTValue fileExtent = fileToExtract.y;
        Utils.extentRangeToFile(imagePath, fileToExtract.x, fileExtent.physBlockNum * blockSize, fileExtent.length);
    }

    public void extractAllFiles() throws  IOException{
        for (Tuple<String, EXTENTValue> fileToExtract: files) {
            EXTENTValue fileExtent = fileToExtract.y;
            Utils.extentRangeToFile(imagePath, fileToExtract.x, fileExtent.physBlockNum * blockSize, fileExtent.length);
        }
    }

    private ArrayList<Tuple<String, EXTENTValue>> getFiles() {
        ArrayList<Tuple<String, EXTENTValue>> files = new ArrayList<>();
        // TODO: Finish file structure parsing
        // Start parsing from Root Directory
        ArrayDeque<Tuple<FSKeyValue, String>> queue = new ArrayDeque<>();
        ArrayList<FSKeyValue> possible_roots = drecRecords.get(ROOT_DIR_OID);
        FSKeyValue root = null;
        for (FSKeyValue fsKeyValue : possible_roots) {
            DRECValue value = (DRECValue) fsKeyValue.value;
            if (value.fileId == 2) {
                root = fsKeyValue;
                break;
            }
        }

        queue.add(new Tuple<>(root, "/"));
        while (!queue.isEmpty()) {
            Tuple<FSKeyValue, String> tuple = queue.removeFirst();
            FSKeyValue curr = tuple.x;
            String path = tuple.y;

            DRECKey key = (DRECKey) curr.key;
            DRECValue value = (DRECValue) curr.value;

            File folder = new File("./output" + path);
            if (!folder.exists()) {
//                folder.mkdir();
            }

            if (value.flags == DT_FILE) {
                // No new files to add since this record is a Regular File
                // Find related extent
                FSKeyValue extentKv = extentRecords.get(value.fileId);
                EXTENTKey extentKey = (EXTENTKey) extentKv.key;
                EXTENTValue extentValue = (EXTENTValue) extentKv.value;

                // Parse file from the extent
                String name = new String(key.name);
                name = name.substring(0, name.length() - 1); // Remove null terminator character
                String fileOutPath = "./output" + path + name;
                files.add(new Tuple<>(fileOutPath, extentValue));
//                Utils.extentRangeToFile(imagePath, fileOutPath, extentValue.physBlockNum * blockSize, extentValue.length);

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
        }
        return files;
    }

    private OMap getVolumeOMap() throws IOException {
        // Parse the VSB OMap
        // Example using "Many Files.dmg":
        // 0                [1028, 1139]
        // 1  [1028, 1030 to 1139]   [1139 to 1145]
        //  -contains BTree Nodes with OMAP keys (oids 1028 and 1139)
        //          1028's children are Leaf Nodes: oids 1028, 1030 to 1139
        //          1139's children are Leaf Nodes: oids 1139 to 1145
        //              We want to add each leaf node to the OMAP BTree -- their paddr's point us to actual FS Objects
        int vsbOMapOffset = (int) apfs_omap_oid * blockSize;
        ByteBuffer vsbOMapBuffer = Utils.GetBuffer(imagePath, vsbOMapOffset, blockSize);
        OMap volumeOMap = new OMap(vsbOMapBuffer, imagePath, blockSize);


        // Parse BTree Nodes to get all inode, extent, drec
        // Organize FS Objects by record object identifiers
        // TODO: Find a better way to traverse the FS Object Tree
        // Right now, we read ALL the nodes in the VSB OMAP since it looks like they're all FS Object Nodes anyways.
        // This might not be the proper way, but it works for both "bigandsmall.dmg" and "Many Files.dmg"

        for (Long addr : volumeOMap.parsedOmap.values()) {
            int offset = addr.intValue() * blockSize;
            ByteBuffer fsObjNodeBuff = Utils.GetBuffer(imagePath, offset, blockSize * 2);
            BTreeNode node = new BTreeNode(fsObjNodeBuff);

            for (FSKeyValue fskv : node.fsKeyValues) {
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
        }
        return volumeOMap;
    }

    @Override
    public String toString() {
        return "APFSVolume{" +
                "blockHeader=" + blockHeader +
                ", apfs_magic=" + new String(apfs_magic) +
                ", apfs_fs_index=" + apfs_fs_index +
                ", apfs_features=" + apfs_features +
                ", apfs_readonly_compatible_features=" + apfs_readonly_compatible_features +
                ", apfs_incompatible_features=" + apfs_incompatible_features +
                ", apfs_unmount_time=" + apfs_unmount_time +
                ", apfs_fs_reserve_block_count=" + apfs_fs_reserve_block_count +
                ", apfs_fs_quota_block_count=" + apfs_fs_quota_block_count +
                ", apfs_fs_alloc_count=" + apfs_fs_alloc_count +
                ", wrapped_crypto_state_t_major_version=" + wrapped_crypto_state_t_major_version +
                ", wrapped_crypto_state_t_minor_version=" + wrapped_crypto_state_t_minor_version +
                ", wrapped_crypto_state_t_cpflags=" + wrapped_crypto_state_t_cpflags +
                ", wrapped_crypto_state_t_persistent_class=" + wrapped_crypto_state_t_persistent_class +
                ", wrapped_crypto_state_t_key_os_version=" + wrapped_crypto_state_t_key_os_version +
                ", wrapped_crypto_state_t_key_revision=" + wrapped_crypto_state_t_key_revision +
                ", wrapped_crypto_state_t_key_len=" + wrapped_crypto_state_t_key_len +
                ", apfs_root_tree_oid_type=" + apfs_root_tree_oid_type +
                ", apfs_extentref_tree_oid_type=" + apfs_extentref_tree_oid_type +
                ", apfs_snap_meta_tree_oid_type=" + apfs_snap_meta_tree_oid_type +
                ", apfs_omap_oid=" + apfs_omap_oid +
                ", apfs_root_tree_oid=" + apfs_root_tree_oid +
                ", apfs_extentref_tree_oid=" + apfs_extentref_tree_oid +
                ", apfs_snap_meta_tree_oid=" + apfs_snap_meta_tree_oid +
                ", apfs_revert_to_xid=" + apfs_revert_to_xid +
                ", apfs_revert_to_sblock_oid=" + apfs_revert_to_sblock_oid +
                ", apfs_next_obj_id=" + apfs_next_obj_id +
                ", apfs_num_files=" + apfs_num_files +
                ", apfs_num_directories=" + apfs_num_directories +
                ", apfs_num_symlinks=" + apfs_num_symlinks +
                ", apfs_num_other_fsobjects=" + apfs_num_other_fsobjects +
                ", apfs_num_snapshots=" + apfs_num_snapshots +
                ", apfs_total_blocks_alloced=" + apfs_total_blocks_alloced +
                ", apfs_total_blocks_freed=" + apfs_total_blocks_freed +
                ", apfs_vol_uuid=" + Arrays.toString(apfs_vol_uuid) +
                ", apfs_last_mod_time=" + apfs_last_mod_time +
                ", apfs_fs_flags=" + apfs_fs_flags +
                ", apfs_modified_by_t_formatted_by_id=" + Arrays.toString(apfs_modified_by_t_formatted_by_id) +
                ", apfs_modified_by_t_formatted_by_timestamp=" + apfs_modified_by_t_formatted_by_timestamp +
                ", apfs_modified_by_t_formatted_by_last_xid=" + apfs_modified_by_t_formatted_by_last_xid +
                ", apfs_modified_by_t_modified_by_id=" + Arrays.toString(apfs_modified_by_t_modified_by_id) +
                ", apfs_modified_by_t_modified_by_timestamp=" + apfs_modified_by_t_modified_by_timestamp +
                ", apfs_modified_by_t_modified_by_last_xid=" + apfs_modified_by_t_modified_by_last_xid +
                ", apfs_modified_by_t_modified_by_1_7=" + Arrays.toString(apfs_modified_by_t_modified_by_1_7) +
                ", apfs_volname=" + Arrays.toString(apfs_volname) +
                ", apfs_next_doc_id=" + apfs_next_doc_id +
                ", apfs_role=" + apfs_role +
                ", apfs_reserved=" + apfs_reserved +
                ", apfs_root_to_xid=" + apfs_root_to_xid +
                ", apfs_er_state_oid=" + apfs_er_state_oid +
                '}';
    }
}


