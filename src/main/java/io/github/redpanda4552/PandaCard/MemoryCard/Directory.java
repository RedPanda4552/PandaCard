package io.github.redpanda4552.PandaCard.MemoryCard;

import java.util.ArrayList;

import io.github.redpanda4552.PandaCard.MemoryCard.File.FileMemoryCard;
import io.github.redpanda4552.PandaCard.MemoryCard.File.FileMemoryCardPageData;
import io.github.redpanda4552.PandaCard.util.PS2File;

public class Directory {

    private final int PAGE_SIZE = 512, LAST_CLUSTER = 0xffffffff, FREE_CLUSTER = 0x7fffffff;
    
    private String directoryName;
    private FileMemoryCardPageData firstPage, secondPage;
    private ArrayList<Directory> subdirectories = new ArrayList<Directory>();
    private PS2File ps2File;
    private boolean deleted = false;
    
    /**
     * Low tech, single instance constructor for creating new directory entries to insert into an existing directory.
     */
    public Directory(String directoryName, PS2File ps2File) {
        this.directoryName = directoryName;
        this.ps2File = ps2File;
    }
    
    /**
     * High tech, recursive constructor for an initial directory build.
     */
    public Directory(FileMemoryCard memoryCard, FileMemoryCardPageData[] pages, String directoryName, int parentPageIndex, int clusterIndex, boolean isFile, boolean deleted, boolean isRoot) {
        this.directoryName = directoryName;
        this.deleted = deleted;
        boolean nextDeleted = false;
        
        if (!isFile) {
            while (clusterIndex != LAST_CLUSTER && clusterIndex != FREE_CLUSTER) {
                this.firstPage = pages[clusterIndex * 2];
                this.secondPage = pages[(clusterIndex * 2) + 1];
                
                if (firstPage.isValidDirectory()) {
                    if (!firstPage.getName().trim().startsWith(".") && !firstPage.getName().trim().endsWith(".")) {
                        subdirectories.add(new Directory(memoryCard, pages, firstPage.getName().trim(), firstPage.getPageNumberOffset(), firstPage.getPointingCluster(), firstPage.isFile(), nextDeleted, false));
                    }
                } else {
                    return;
                }
                
                
                if (secondPage.isValidDirectory()) {
                    if (!secondPage.getName().trim().startsWith(".") && !secondPage.getName().trim().endsWith(".")) {
                        subdirectories.add(new Directory(memoryCard, pages, secondPage.getName().trim(), secondPage.getPageNumberOffset(), secondPage.getPointingCluster(), secondPage.isFile(), nextDeleted, false));
                    }
                } else {
                    return;
                }
                
                clusterIndex = memoryCard.getFAT().getFAT()[firstPage.getClusterNumberOffset()];
                
                // If the next directory (according to the FAT) was deleted
                if ((clusterIndex & 0x80000000) == 0) {
                    nextDeleted = true;
                } else {
                    nextDeleted = false;
                }
            }
        } else {
            int bytesCopied;
            byte[] fileBytes;
            FileMemoryCardPageData directoryEntryForFile = pages[parentPageIndex], page;
            
            bytesCopied = 0;
            fileBytes = new byte[directoryEntryForFile.getLength()];
            page = pages[directoryEntryForFile.getPointingCluster() * 2];
            
            while (bytesCopied < directoryEntryForFile.getLength()) {
                fileBytes[bytesCopied] = page.getData()[bytesCopied % PAGE_SIZE];
                
                if (++bytesCopied % PAGE_SIZE == 0) {
                    page = pages[page.getPageNumberOffset() + 1];
                }
            }
            
            ps2File = new PS2File(directoryEntryForFile.getName(), fileBytes);
        }
    }
    
    public String getDirectoryName() {
        return directoryName;
    }
    
    public ArrayList<Directory> getSubdirectories() {
        return subdirectories;
    }
    
    public void addSubdirectory(Directory subdirectory) {
        subdirectories.add(subdirectory);
    }
    
    public boolean isDeleted() {
        return deleted;
    }
    
    public PS2File getPS2File() {
        return ps2File;
    }
}
