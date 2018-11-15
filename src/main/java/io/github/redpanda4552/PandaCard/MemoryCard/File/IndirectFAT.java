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

public class IndirectFAT {

    private final int ENTRIES_PER_PAGE = 128, IFAT_PAGES = 2;
    
    private int[] iFAT = new int[IFAT_PAGES * ENTRIES_PER_PAGE];
    
    public IndirectFAT(FileMemoryCardPageFAT[] table) {
        for (int i = 0; i < table.length; i++) {
            FileMemoryCardPageFAT page = table[i];
            int[] entries = page.getFAT();
            
            for (int j = 0; j < entries.length; j++) {
                iFAT[(i * ENTRIES_PER_PAGE) + j] = entries[j];
            }
        }
    }
    
    public int[] getIFAT() {
        return iFAT;
    }
}
