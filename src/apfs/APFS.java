package apfs;

import java.io.IOException;

public class APFS {
    private APFSContainer containerSb;
    private APFSVolume volumeSb;

    public APFS(String pathname) throws IOException {
        containerSb = APFSContainer.parseContainer(pathname);

        // TODO: Find Volume Superblock using an offset from the Container Superblock.
        // IMPORTANT: The following volume superblock uses the incorrect offset. We need to fix this.
        volumeSb = APFSVolume.parseVolume(pathname, (int) (containerSb.nx_next_oid * containerSb.nx_block_size), containerSb.nx_block_size);
    }

    @Override
    public String toString() {
        return "APFS{" +
                "containerSb=" + containerSb +
                ", \n\nvolumeSb=" + volumeSb +
                '}';
    }
}
