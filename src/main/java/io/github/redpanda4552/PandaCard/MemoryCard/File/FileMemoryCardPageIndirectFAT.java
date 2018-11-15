package io.github.redpanda4552.PandaCard.MemoryCard.File;

public class FileMemoryCardPageIndirectFAT extends FileMemoryCardPage {

    private int[] table;
    
    public FileMemoryCardPageIndirectFAT(int pageNumber, byte[] rawData) {
        super(pageNumber, rawData);
    }

}
