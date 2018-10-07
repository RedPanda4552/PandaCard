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

public class MemoryCard {

    private final int PAGE_COUNT = 16384;
    private final int PAGE_COUNT_FAT = 64;
    private final int PAGE_TOTAL_SIZE_BYTES = 528;
    private final int PAGE_DATA_SIZE_BYTES = 512;
    private final int PAGE_SPARE_SIZE_BYTES = 16;
    
    private byte[] rawData = new byte[PAGE_COUNT * (PAGE_DATA_SIZE_BYTES + PAGE_SPARE_SIZE_BYTES)];
    private MemoryCardPage[] pages = new MemoryCardPage[PAGE_COUNT];
    private FAT fat;
    private int currentPage = 0;
    
    public MemoryCard(File memoryCardFile) throws IOException {
        InputStream iStream = Files.newInputStream(memoryCardFile.toPath());
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
        pages[0x0000] = new MemoryCardPageSuperblock(0, getNextPageBytes());

        // Unused Pages (1-15)
        for (int i = 0x0001; i < 0x0010; i++)
            pages[i] = new MemoryCardPage(i, getNextPageBytes());
        
        // Indirect FAT Table (16-17)
        for (int j = 0x0010; j < 0x0012; j++)
            pages[j] = new MemoryCardPage(j, getNextPageBytes());
        
        MemoryCardPageFAT[] fatPages = new MemoryCardPageFAT[PAGE_COUNT_FAT];
        
        // FAT Table (18-81)
        for (int k = 0x0012; k < 0x0052; k++) {
            MemoryCardPageFAT page = new MemoryCardPageFAT(k, getNextPageBytes()); 
            pages[k] = page;
            fatPages[k - 0x12] = page;
        }
        
        fat = new FAT(fatPages);
        
        // Allocatable Clusters (82-16081)
        for (int w = 0x0052; w < 0x3ED2; w++)
            pages[w] = new MemoryCardPage(w, getNextPageBytes());
        
        // Reserved Clusters (16082 - 16351)
        for (int x = 0x3ED2; x < 0x3FE0; x++)
            pages[x] = new MemoryCardPage(x, getNextPageBytes());
        
        // Backup Block 2 (16352 - 16367)
        for (int y = 0x3FE0; y < 0x3FF0; y++)
            pages[y] = new MemoryCardPage(y, getNextPageBytes());
        
        // Backup Block 1 (16368 - 16383)
        for (int z = 0x3FF0; z <= 0x3FFF; z++)
            pages[z] = new MemoryCardPage(z, getNextPageBytes());
    }
    
    /**
     * Filter down a byte[] to only the contents between the specified start and end positions.
     * @param data - The data to filter
     * @param startPos - The starting position (inclusive)
     * @param endPos - The ending position (exclusive)
     * @return A byte[] of length endPos - startPos + 1.
     */
    private byte[] getNextPageBytes() {
        byte[] ret = new byte[PAGE_TOTAL_SIZE_BYTES];
        
        for (int i = 0; i < PAGE_TOTAL_SIZE_BYTES; i++)
            ret[i] = rawData[(currentPage * PAGE_TOTAL_SIZE_BYTES) + i];
        
        currentPage++;        
        return ret;
    }
    
    public MemoryCardPage[] getPages() {
        return pages;
    }
    
    public FAT getFAT() {
        return fat;
    }
    
    public MemoryCardPage[] getPagesInCluster(int cluster) {
        MemoryCardPage[] ret = new MemoryCardPage[2];
        ret[0] = pages[cluster * 2];
        ret[1] = pages[(cluster * 2) + 1];
        return ret;
    }
    
    public MemoryCardPageSuperblock getSuperblock() {
        return (MemoryCardPageSuperblock) pages[0];
    }
}
