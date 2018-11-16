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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import io.github.redpanda4552.PandaCard.MemoryCard.AbstractMemoryCard;

public class FileMemoryCardPageSuperblock extends FileMemoryCardPage {

    private String formatString = new String();
    private boolean formatted;
    
    public FileMemoryCardPageSuperblock(int pageNumber, byte[] rawData) {
        super(pageNumber, rawData);
        
        for (int i = 0x00; i < 0x1C; i++) {
            formatString += Character.valueOf((char) data[i]).toString();
        }
        
        formatted = formatString.equals(AbstractMemoryCard.MAGIC_STRING);
    }
    
    public boolean isFormatted() {
        return formatted;
    }
    
    public int getPagesPerCluster() {
        byte[] bytes = new byte[2];
        
        for (int i = 0; i < 2; i++) {
            bytes[i] = data[0x2A + i];
        }
        
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public int getAllocatedClustersOffset() {
        byte[] bytes = new byte[4];
        
        for (int i = 0; i < 4; i++) {
            bytes[i] = data[0x34 + i];
        }
        
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
}
