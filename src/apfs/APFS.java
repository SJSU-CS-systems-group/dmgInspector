package apfs;

import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

public class APFS {

    String imagePath;
    private APFSContainer containerSb;
    private int blockSize;
    public ArrayList<APFSVolume> volumes;


    public APFS(String imagePath) throws IOException {
        this.imagePath = imagePath;

        // Parse the Container Superblock (CSB)
        containerSb = APFSContainer.parseContainer(imagePath);
        // System.out.println(containerSb.toString());
        blockSize = containerSb.nx_block_size;

        volumes = getAPFSVolumes();
        APFSVolume volume = volumes.get(0);
        // System.out.println(volume);
    }


//    public ArrayList<FSKeyValue> getFSRecords(int volIndex) throws IOException {
//        APFSVolume volume = volumes.get(volIndex);
//        // TODO: Get records
//    }

    // Parse the Volume Superblock (VSB)
    // Get the VSB physical address by searching for nx_fs_oid in the CSB OMAP
    // nx_fs_oid: contains OIDs for VSBs - see APFS Reference pg. 32
    private ArrayList<APFSVolume> getAPFSVolumes() throws IOException {
        long[] volumeOIDs = containerSb.nx_fs_oid;
        int blockSize = containerSb.nx_block_size;

        ArrayList<APFSVolume> vols = new ArrayList<>();
        for (long volOID : volumeOIDs) {
            int vsbOffset = containerSb.csbOMap.parsedOmap.get(volOID).intValue() * blockSize;
            ByteBuffer volumeSbBuffer = Utils.GetBuffer(imagePath, vsbOffset, blockSize);
            vols.add(new APFSVolume(volumeSbBuffer, blockSize, imagePath));
        }

        return vols;
    }
}

// Single Files
// Map Object Identifier -> Inode Records
// Map Object Identifier -> Extend Records

// Directories
// Map Object Identifier -> Drec Records

