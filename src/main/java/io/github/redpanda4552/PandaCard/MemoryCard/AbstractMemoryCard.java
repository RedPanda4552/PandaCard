package io.github.redpanda4552.PandaCard.MemoryCard;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.github.redpanda4552.PandaCard.util.PS2File;

public abstract class AbstractMemoryCard {

    protected File hostFile;
    //protected HashMap<String, ArrayList<PS2File>> contents = new HashMap<String, ArrayList<PS2File>>();
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
    /*
    public HashMap<String, ArrayList<PS2File>> getContents() {
        return contents;
    }
    */
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
    
    public boolean containsDirectory(String directoryName) {
        for (Directory dir : directory.getSubdirectories()) {
            if (dir.getDirectoryName().equals(directoryName)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void insertDirectory(String directoryName, ArrayList<PS2File> ps2Files) {
        Directory baseDir = new Directory(directoryName, null);
        
        for (PS2File ps2File : ps2Files) {
            baseDir.addSubdirectory(new Directory(ps2File.getFileName(), ps2File));
        }
        
        directory.addSubdirectory(baseDir);
    }
    
    public int getFileCount() {
        return getFileCount(directory);
    }
    
    private int getFileCount(Directory dir) {
        int ret = dir.getPS2File() != null ? 1 : 0;
        
        for (Directory sub : dir.getSubdirectories()) {
            ret += getFileCount(sub);
        }
        
        return ret;
    }
}
