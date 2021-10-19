package apfs;

import utils.Utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

public class APFSVolume {
    public BlockHeader blockHeader;
    public int apfs_magic;
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

    public APFSVolume(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        blockHeader = new BlockHeader(buffer);
        apfs_magic = buffer.getInt();
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
    }

    public static APFSVolume parseVolume(String path, int volumeOffset, int volumeLength) throws IOException {
        ByteBuffer buffer = Utils.GetBuffer(path, volumeOffset, volumeLength);
        ByteBuffer buffer2 = Utils.GetBuffer(path, volumeOffset, volumeLength);
        // 1. Find the OMAP
        byte[] b = new byte[buffer2.remaining()];
        buffer2.get(b);
        System.out.println(b);

        // 2. Parse the OMAP to a BTree

        // 3. Plug in Volume OID to get volume offset!

        APFSVolume block = new APFSVolume(buffer);
        return block;
    }

    @Override
    public String toString() {
        return "APFSVolume{" +
                "blockHeader=" + blockHeader +
                ", apfs_magic=" + apfs_magic +
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


