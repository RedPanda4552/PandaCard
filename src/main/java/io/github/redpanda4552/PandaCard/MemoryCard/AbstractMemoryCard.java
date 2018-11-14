package io.github.redpanda4552.PandaCard.MemoryCard;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.github.redpanda4552.PandaCard.util.PS2File;

public abstract class AbstractMemoryCard {

    protected File hostFile;
    protected HashMap<String, ArrayList<PS2File>> contents = new HashMap<String, ArrayList<PS2File>>();
    protected Directory directory;
    protected boolean formatted, modified = false;
    protected MemoryCardType memoryCardType;
    
    public AbstractMemoryCard(File file, MemoryCardType memoryCardType) {
        this.hostFile = file;
        this.memoryCardType = memoryCardType;
    }

    /**
     * Get the file/folder on the host file system that contains this memory card.
     */
    public File getHostFile() {
        return hostFile;
    }
    
    public HashMap<String, ArrayList<PS2File>> getContents() {
        return contents;
    }
    
    public Directory getRootDirectory() {
        return directory;
    }
    
    public boolean isFormatted() {
        return formatted;
    }
    
    public MemoryCardType getMemoryCardType() {
        return memoryCardType;
    }
    
    public void setModified(boolean b) {
        modified = b;
    }
    
    /**
     * Returns whether or not the current memory card state is modified from the card on disk.
     */
    public boolean isModified() {
        return modified;
    }
}
