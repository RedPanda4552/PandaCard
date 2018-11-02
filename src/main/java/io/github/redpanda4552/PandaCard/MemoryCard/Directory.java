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
    private PS2File firstPS2File, secondPS2File;
    private boolean deleted = false;
    
    public Directory(FileMemoryCard memoryCard, FileMemoryCardPageData[] pages, String directoryName, int clusterIndex, boolean deleted, boolean isRoot) {
    	this.directoryName = directoryName;
    	this.deleted = deleted;
    	boolean nextDeleted = false;
    	
        while (clusterIndex != LAST_CLUSTER && clusterIndex != FREE_CLUSTER) {
        	this.firstPage = pages[clusterIndex * 2];
            this.secondPage = pages[(clusterIndex * 2) + 1];
            
            if (firstPage.isValidDirectory()) {
            	if (!firstPage.getName().trim().startsWith(".") && !firstPage.getName().trim().endsWith(".")) {
                	subdirectories.add(new Directory(memoryCard, pages, firstPage.getName().trim(), firstPage.getPointingCluster(), nextDeleted, false));
                }
    		} else {
    			return;
    		}
            
            
            if (secondPage.isValidDirectory()) {
            	if (!secondPage.getName().trim().startsWith(".") && !secondPage.getName().trim().endsWith(".")) {
                	subdirectories.add(new Directory(memoryCard, pages, secondPage.getName().trim(), secondPage.getPointingCluster(), nextDeleted, false));
                }
    		} else {
    			return;
    		}
            
            clusterIndex = memoryCard.getFAT().getFAT()[firstPage.getClusterNumber()];
            
            // If the next directory (according to the FAT) was deleted
            if ((clusterIndex & 0x80000000) == 0) {
            	nextDeleted = true;
            } else {
            	nextDeleted = false;
            }
        }
        
        int pageCount, byteCount = 0;
        byte[] fileBytes;
        FileMemoryCardPageData page;
        
        if (firstPage.isFile()) {
        	pageCount = (int) Math.ceil((double) firstPage.getLength() / PAGE_SIZE);
        	fileBytes = new byte[firstPage.getLength()];
        	
        	// TODO Read bytes from page into fileBytes
        	
        	firstPS2File = new PS2File(firstPage.getName(), fileBytes);
        }
        
        if (secondPage.isFile()) {
        	// TODO Above, again
        }
    }
    
    public String getDirectoryName() {
    	return directoryName;
    }
    
    public ArrayList<Directory> getSubdirectories() {
    	return subdirectories;
    }
    
    public boolean isDeleted() {
    	return deleted;
    }
    
    public PS2File getFirstPS2File() {
    	return firstPS2File;
    }
    
    public PS2File getSecondPS2File() {
    	return secondPS2File;
    }
}
