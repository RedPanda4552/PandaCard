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

public class MemoryCardPageData extends MemoryCardPage {

    public MemoryCardPageData(int pageNumber, byte[] rawData) {
        super(pageNumber, rawData);
    }

    public boolean isDirectory() {
        return (data[0] & 0x20) == 0x20;
    }
    
    public boolean isFile() {
        return (data[0] & 0x10) == 0x10;
    }
    
    public String getDirectoryName() {
        String ret = new String();
        
        for (int i = 0; i < 32; i++) {
            ret += Character.valueOf((char) data[0x40 + i]);
        }
        
        return ret;
    }

    public String stringifyRearBytes() {
        String ret = new String();
        
        for (int i = 0; i < 416; i++) {
            ret += Character.valueOf((char) data[0x60 + i]);
        }
        
        return ret;
    }
    
    public int getLength() {
        byte[] bytes = new byte[4];
        
        for (int i = 0; i < 4; i++) {
            bytes[i] = data[0x04 + i];
        }
        
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
}
