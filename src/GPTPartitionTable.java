import utils.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GPTPartitionTable {
    public static final int LBA_SIZE = 512;
    private static GPTPartitionHeader gptHeader;
    public List<GPTPartitionEntry> partitionEntries;

    public GPTPartitionTable (String imgPath) throws IOException {
        gptHeader = GPTPartitionHeader.parseGPTHeader(imgPath);
        partitionEntries = getGPTPartitionEntries(imgPath);
    }

    private static ArrayList<GPTPartitionEntry> getGPTPartitionEntries(String imgPath) throws IOException {
        byte[] gptBytes = Utils.getImageBytes(imgPath, LBA_SIZE * 34);
        int partitionStart = LBA_SIZE * 2;
        int partitionEnd = LBA_SIZE * 2 + (gptHeader.partitionCount * gptHeader.partitionSize);

        byte[] gptPartitionBytes = Arrays.copyOfRange(gptBytes, partitionStart, partitionEnd);

        ArrayList<GPTPartitionEntry> gptPartitionEntries = new ArrayList<>();

        for (int i = 0; i < gptHeader.partitionCount; i++) {
            byte[] entryBytes = Arrays.copyOfRange(gptPartitionBytes, i * gptHeader.partitionSize, i * gptHeader.partitionSize + gptHeader.partitionSize);
            GPTPartitionEntry entry = new GPTPartitionEntry(entryBytes);
            gptPartitionEntries.add(entry);
        }
        return gptPartitionEntries;
    }

    public static GPTPartitionTable parseImage(String imgPath) throws IOException {
        return new GPTPartitionTable(imgPath);
    }

    public void print(String imageName) {
//        System.out.printf("%-45s %-10s %-10s %-10s %-10s %-10s %n", "Partition", "Start", "End", "LBA Sectors", "Size", "Type");
        for (int i = 0; i < partitionEntries.size(); i++) {
            //TODO: Does GPT always parition in order?
//            if(!partitionEntries.get(i).determineIfUnusedPartition())
//                System.out.printf("%-45s %s", imageName + (i + 1), partitionEntries.get(i).toString());
        }
    }
}

class GPTPartitionHeader {
    public int partitionCount;
    public int partitionSize;

    public GPTPartitionHeader(int _partitionCount, int _partitionSize) throws IOException {
        partitionCount = _partitionCount;
        partitionSize = _partitionSize;
    }

    public static GPTPartitionHeader parseGPTHeader(String imgPath) throws IOException {
        byte[] gptHeaderBytes = Utils.getImageBytes(imgPath, GPTPartitionTable.LBA_SIZE * 2);
        ByteBuffer buffer = ByteBuffer.wrap(gptHeaderBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(GPTPartitionTable.LBA_SIZE + 80);
        int partitionCount = buffer.getInt();
        int partitionSize = buffer.getInt();

        return new GPTPartitionHeader(partitionCount, partitionSize);
    }
}


