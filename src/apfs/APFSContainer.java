package apfs;

import utils.Utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

// See APFS reference page 26 (Container Superblock)
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
    public long nx_omap_oid; // Physical object identifier (physical address) of the CSB OMAP
    public long nx_reaper_oid;
    public int nx_test_type;
    public int nx_max_file_systems;
    public long[] nx_fs_oid;

    public OMap csbOMap;

    public APFSContainer(ByteBuffer buffer, String imagePath) throws IOException {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        blockHeader = new BlockHeader(buffer.slice(0, 32));
        buffer.position(32);
        buffer.get(magic);
        nx_block_size = buffer.getInt();
        nx_block_count = buffer.getLong();
        nx_features = buffer.getLong();
        nx_read_only_compatible_features = buffer.getLong();
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

        nx_fs_oid = new long[nx_max_file_systems];
        for (int i = 0; i < nx_max_file_systems; i++) {
            nx_fs_oid[i] = buffer.getLong();
        }

        // Get the CSB Object Map (OMAP), which maps OIDs to values containing a physical address
        // nx_omap_oid is a physical address
        int csbOmapOffset = (int) nx_omap_oid * nx_block_size;
        ByteBuffer csbOMapBuffer = Utils.GetBuffer(imagePath, csbOmapOffset, nx_block_size);
        csbOMap = new OMap(csbOMapBuffer, imagePath, nx_block_size);
    }

    public static APFSContainer parseContainer(String path) throws IOException {
        ByteBuffer buffer = Utils.GetBuffer(path, 0, 512);
        APFSContainer block = new APFSContainer(buffer, path);
        return block;
    }


    @Override
    public String toString() {
        return String.format("APFS Super Block\nBlock Header %s\nMagic: %s\nBlock Size: %s\nBlock Count: %s\nSpaceman OID: %s\nOMAP OID: %s\nReaper OID: %s\n", blockHeader, new String(magic), nx_block_size, nx_block_count, nx_spaceman_oid, nx_omap_oid, nx_reaper_oid);
    }
}
