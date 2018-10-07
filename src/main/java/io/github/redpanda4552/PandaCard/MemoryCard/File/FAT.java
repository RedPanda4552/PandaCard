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

public class FAT {

    private final int ENTRIES_PER_PAGE = 128, FAT_PAGES = 64, UNUSED_CLUSTER = 0x7FFFFFFF;
    
    private int[] fat = new int[FAT_PAGES * ENTRIES_PER_PAGE];
    
    public FAT(MemoryCardPageFAT[] table) {
        for (int i = 0; i < table.length; i++) {
            MemoryCardPageFAT page = table[i];
            int[] entries = page.getFAT();
            
            for (int j = 0; j < entries.length; j++) {
                fat[(i * ENTRIES_PER_PAGE) + j] = entries[j];
            }
        }
    }
    
    public int[] getFAT() {
        return fat;
    }
    
    /**
     * Finds the first occurrence of a gap large enough to fit a number of clusters.
     */
    public int getFirstSpaceForClusters(int clusterCount) {
        int counter = 0, position = -1;
        
        for (int cluster : fat) {
            if (cluster == UNUSED_CLUSTER) {
                counter++;
                position = cluster;
                
                if (counter >= clusterCount)
                    return position;
            } else {
                counter = 0;
                position = -1;
            }
        }
        
        return -1;
    }
}
