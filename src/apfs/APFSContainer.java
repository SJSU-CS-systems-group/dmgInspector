package apfs;

import utils.Utils;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class APFSContainer {
    public BlockHeader blockHeader;
    public byte[] magic = new byte[4];
    public int nx_block_size;
    public long nx_block_count;
    public long nx_features;
    public long nx_read_only_compatible_features;
    public long nx_incompatable_features;
    public byte[] nx_uuid = new byte[16];
    public long nx_next_oid;
    public long nx_next_xid;
    public int nx_xp_desc_blocks;
    public int nx_xp_data_blocks;
    public long nx_xp_desc_base;
    public long nx_xp_data_base;
    public int nx_xp_desc_next;
    public int nx_xp_data_next;
    public int nx_xp_desc_index;
    public int nx_xp_desc;
    public int nx_xp_data_index;
    public int nx_xp_data;
    public long nx_spaceman_oid;
    public long nx_omap_oid;
    public long nx_reaper_oid;
    public int nx_test_type;
    public int nx_max_file_systems;
    public long nx_fs_oid;

    public APFSContainer(ByteBuffer buffer){
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        blockHeader = new BlockHeader(buffer.slice(0,32));
        buffer.position(32);
        buffer.get(magic);
        nx_block_size = buffer.getInt();
        nx_block_count = buffer.getLong();
        nx_features = buffer.getLong();
        nx_read_only_compatible_features = buffer.getLong();;
        nx_incompatable_features = buffer.getLong();
        buffer.get(nx_uuid);
        nx_next_oid = buffer.getLong();
        nx_next_xid = buffer.getLong();
        nx_xp_desc_blocks = buffer.getInt();
        nx_xp_data_blocks = buffer.getInt();
        nx_xp_desc_base = buffer.getLong();
        nx_xp_data_base = buffer.getLong();
        nx_xp_desc_next = buffer.getInt();
        nx_xp_data_next = buffer.getInt();
        nx_xp_desc_index = buffer.getInt();
        nx_xp_desc = buffer.getInt();
        nx_xp_data_index = buffer.getInt();
        nx_xp_data = buffer.getInt();
        nx_spaceman_oid = buffer.getLong();
        nx_omap_oid = buffer.getLong();
        nx_reaper_oid = buffer.getLong();
        nx_test_type = buffer.getInt();
        nx_max_file_systems = buffer.getInt();
        nx_fs_oid = buffer.getLong();
    }

    public static APFSContainer parseImage(String path) throws IOException {
        RandomAccessFile ras = new RandomAccessFile(path, "r");
        byte[] superBlockBytes = new byte[512];
        ras.read(superBlockBytes);
        System.out.println(Utils.OriginalBytesToHexString(superBlockBytes));
        ByteBuffer buffer = ByteBuffer.wrap(superBlockBytes);
        APFSContainer block = new APFSContainer(buffer);
        return block;
    }


    @Override
    public String toString() {
        return "APFSSuperBlock{" +
                "blockHeader=" + blockHeader +
                ", magic=" + new String(magic) +
                ", nx_block_size=" + nx_block_size +
                ", nx_block_count=" + nx_block_count +
                ", nx_features=" + nx_features +
                ", nx_read_only_compatible_features=" + nx_read_only_compatible_features +
                ", nx_incompatable_features=" + nx_incompatable_features +
                ", nx_uuid=" + Arrays.toString(nx_uuid) +
                ", nx_next_oid=" + nx_next_oid +
                ", nx_next_xid=" + nx_next_xid +
                ", nx_xp_desc_blocks=" + nx_xp_desc_blocks +
                ", nx_xp_data_blocks=" + nx_xp_data_blocks +
                ", nx_xp_desc_base=" + nx_xp_desc_base +
                ", nx_xp_data_base=" + nx_xp_data_base +
                ", nx_xp_desc_next=" + nx_xp_desc_next +
                ", nx_xp_data_next=" + nx_xp_data_next +
                ", nx_xp_desc_index=" + nx_xp_desc_index +
                ", nx_xp_desc=" + nx_xp_desc +
                ", nx_xp_data_index=" + nx_xp_data_index +
                ", nx_xp_data=" + nx_xp_data +
                ", nx_spaceman_oid=" + nx_spaceman_oid +
                ", nx_omap_oid=" + nx_omap_oid +
                ", nx_reaper_oid=" + nx_reaper_oid +
                ", nx_test_type=" + nx_test_type +
                ", nx_max_file_systems=" + nx_max_file_systems +
                ", nx_fs_oid=" + nx_fs_oid +
                '}';
    }
}