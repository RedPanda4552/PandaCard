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

public class FileMemoryCardPage {

    protected final int PAGE_DATA_SIZE_BYTES = 512;
    protected final int PAGE_SPARE_SIZE_BYTES = 16;
    
    protected int pageNumber;
    protected byte[] data = new byte[PAGE_DATA_SIZE_BYTES];
    protected byte[] spare = new byte[PAGE_SPARE_SIZE_BYTES];
    
    public FileMemoryCardPage(int pageNumber, byte[] rawData) {
        this.pageNumber = pageNumber;
        
        for (int i = 0; i < PAGE_DATA_SIZE_BYTES; i++)
            this.data[i] = rawData[i];
        
        for (int j = 0; j < PAGE_SPARE_SIZE_BYTES; j++)
            this.spare[j] = rawData[PAGE_DATA_SIZE_BYTES + j];
    }
    
    public byte[] getData() {
        return data;
    }
    
    public int getPageNumber() {
    	return pageNumber;
    }
    
    public int getClusterNumber() {
    	return (int) Math.floorDiv(pageNumber - 0x52, 2);
    }
    
    public byte[] getSpare() {
        return spare;
    }
}
