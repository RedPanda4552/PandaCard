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
package io.github.redpanda4552.PandaCard.MemoryCard.File;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import io.github.redpanda4552.PandaCard.MemoryCard.AbstractMemoryCard;
import io.github.redpanda4552.PandaCard.MemoryCard.Directory;
import io.github.redpanda4552.PandaCard.MemoryCard.MemoryCardType;

public class FileMemoryCard extends AbstractMemoryCard {

    private final int PAGE_COUNT = 16384;
    private final int PAGE_COUNT_IFAT = 2;
    private final int PAGE_COUNT_FAT = 64;
    private final int PAGE_COUNT_DATA = 16000;
    private final int PAGE_TOTAL_SIZE_BYTES = 528;
    private final int PAGE_DATA_SIZE_BYTES = 512;
    private final int PAGE_SPARE_SIZE_BYTES = 16;
    
    private byte[] rawData = new byte[PAGE_COUNT * (PAGE_DATA_SIZE_BYTES + PAGE_SPARE_SIZE_BYTES)];
    private FileMemoryCardPage[] pages = new FileMemoryCardPage[PAGE_COUNT];
    private FileMemoryCardPageData[] dataPages = new FileMemoryCardPageData[PAGE_COUNT_DATA];
    private FAT fat;
    private IndirectFAT iFAT;
    private int currentPage = 0;
    
    public FileMemoryCard(File memoryCardFile) throws IOException {
        super(memoryCardFile, MemoryCardType.FILE_MULTIPLATFORM);
        InputStream iStream = Files.newInputStream(hostFile.toPath());
        int bytesRead = 0, dataCtr = 0;
        byte[] bytesIn = new byte[16];
        
        try {
            while ((bytesRead = iStream.read(bytesIn)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    rawData[dataCtr] = bytesIn[i];
                    dataCtr++;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Memory card exceeded expected byte count! Is it larger than 8 MB?");
        }
        
        
        // Superblock Page (0)
        pages[0x0000] = new FileMemoryCardPageSuperblock(0, getNextPageBytes());

        // Unused Pages (1-15)
        for (int i = 0x0001; i < 0x0010; i++)
            pages[i] = new FileMemoryCardPage(i, getNextPageBytes());
        
        FileMemoryCardPageFAT[] iFATPages = new FileMemoryCardPageFAT[PAGE_COUNT_IFAT];
        
        // Indirect FAT Table (16-17)
        for (int j = 0x0010; j < 0x0012; j++) {
            FileMemoryCardPageFAT page = new FileMemoryCardPageFAT(j, getNextPageBytes());
            pages[j] = page;
            iFATPages[j - 0x10] = page;
        }
        
        iFAT = new IndirectFAT(iFATPages);
        FileMemoryCardPageFAT[] fatPages = new FileMemoryCardPageFAT[PAGE_COUNT_FAT];
        
        // FAT Table (18-81)
        for (int k = 0x0012; k < 0x0052; k++) {
            FileMemoryCardPageFAT page = new FileMemoryCardPageFAT(k, getNextPageBytes()); 
            pages[k] = page;
            fatPages[k - 0x12] = page;
        }
        
        fat = new FAT(fatPages);
        
        // Allocatable Clusters (82-16081)
        for (int w = 0x0052; w < 0x3ED2; w++) {
            FileMemoryCardPageData page = new FileMemoryCardPageData(w, getNextPageBytes());
            pages[w] = page;
            dataPages[w - 0x52] = page;
        }
        
        // Reserved Clusters (16082 - 16351)
        for (int x = 0x3ED2; x < 0x3FE0; x++)
            pages[x] = new FileMemoryCardPage(x, getNextPageBytes());
        
        // Backup Block 2 (16352 - 16367)
        for (int y = 0x3FE0; y < 0x3FF0; y++)
            pages[y] = new FileMemoryCardPage(y, getNextPageBytes());
        
        // Backup Block 1 (16368 - 16383)
        for (int z = 0x3FF0; z <= 0x3FFF; z++)
            pages[z] = new FileMemoryCardPage(z, getNextPageBytes());
        
        directory = new Directory(this, dataPages, getHostFile().getAbsolutePath(), 0, false, true);
        formatted = getSuperblock().isFormatted();
    }
    
    private byte[] getNextPageBytes() {
        byte[] ret = new byte[PAGE_TOTAL_SIZE_BYTES];
        
        for (int i = 0; i < PAGE_TOTAL_SIZE_BYTES; i++)
            ret[i] = rawData[(currentPage * PAGE_TOTAL_SIZE_BYTES) + i];
        
        currentPage++;        
        return ret;
    }
    
    public FileMemoryCardPage[] getPages() {
        return pages;
    }
    
    public FAT getFAT() {
        return fat;
    }
    
    public IndirectFAT getIndirectFAT() {
        return iFAT;
    }
    
    public FileMemoryCardPageData[] getDataPages() {
        return dataPages;
    }
    
    public FileMemoryCardPage[] getPagesInCluster(int cluster) {
        FileMemoryCardPage[] ret = new FileMemoryCardPage[2];
        ret[0] = pages[cluster * 2];
        ret[1] = pages[(cluster * 2) + 1];
        return ret;
    }
    
    public FileMemoryCardPageSuperblock getSuperblock() {
        return (FileMemoryCardPageSuperblock) pages[0];
    }
}
