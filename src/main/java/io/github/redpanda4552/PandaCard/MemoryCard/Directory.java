/**
 * This file is part of PandaCard, licensed under the MIT License (MIT)
 * 
 * Copyright (c) 2018 Brian Wood
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.redpanda4552.PandaCard.MemoryCard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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
     * Low tech, recursive constructor for an initial directory build on a folder memory card.
     */
    public Directory(File file, String directoryName) {
        this.directoryName = directoryName;
        
        if (file.isDirectory()) {
            for (File sub : file.listFiles()) {
                if (sub.getName().equals(AbstractMemoryCard.SUPERBLOCK_NAME))
                    continue;
                
                subdirectories.add(new Directory(sub, sub.getName()));
            }
        } else if (file.isFile()) {
            try {
                InputStream iStream = Files.newInputStream(file.toPath());
                byte[] fileBytes = new byte[(int) file.length()];
                iStream.read(fileBytes);
                iStream.close();
                ps2File = new PS2File(file.getName(), fileBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * High tech, recursive constructor for an initial directory build on a file memory card.
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
