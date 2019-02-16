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

import io.github.redpanda4552.PandaCard.util.PS2Time;

public class FileMemoryCardPageData extends FileMemoryCardPage {

    public FileMemoryCardPageData(int pageNumber, byte[] rawData) {
        super(pageNumber, rawData);
    }
    
    /**
     * Gets the index of this page, <i>subtracting the offset of the allocatable clusters</i>.
     * 
     */
    public int getPageNumberOffset() {
        return pageNumber - 0x52;
    }
    
    /**
     * Gets the index of this page, <i>subtracting the offset of the allocatable clusters</i>.
     * @return
     */
    public int getClusterNumberOffset() {
        int ret = Math.floorDiv(pageNumber - 0x52, 2);
        
        return (int) Math.floorDiv(pageNumber - 0x52, 2);
    }
    
    /**
     * Check some criteria to verify this is a directory entry, and contains valid data.
     * Note that raw files may still pass this check if just the perfect sequence of bytes
     * is set, so this should not be relied on as a mechanism for determining if a page is
     * a directory entry (pointing to either a directory or a file), or a file itself.
     */
    public boolean isValidDirectory() {
        return (isFile() ^ isDirectory()) &&
                (getPageNumber() >= 0x52 && getPageNumber() < 0x3ed2) &&
                (getPointingCluster() >= 0x00 && getPointingCluster() < 0x1FF0) &&
                (getLength() >= 0 && getLength() <= 8388608) &&
                (getName().trim().length() <= 32);
    }
    
    /**
     * Check if this directory is pointing to a file.
     * @return True if this page's cluster attribute points to a file, false otherwise.
     */
    public boolean isFile() {
        return (data[0] & 0x10) == 0x10;
    }
    
    /**
     * Check if this directory is pointing to another directory.
     * @return True if this page's cluster attribute points to a directory, false otherwise.
     */
    public boolean isDirectory() {
        return (data[0] & 0x20) == 0x20;
    }
    
    public int getMode() {
        byte[] bytes = new byte[4];
        
        for (int i = 0; i < 2; i++)
            bytes[i] = data[i];
        
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    /**
     * Get the number of items in the directory this page points to, if this page is pointing to a directory,
     * or the size in bytes of the file this page points to, if this page is pointing to a file. Only effective
     * for directory entries, file data will likely give back straight garbage.
     */
    public int getLength() {
        byte[] bytes = new byte[4];
        
        for (int i = 0; i < 4; i++) {
            bytes[i] = data[0x04 + i];
        }
        
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    public PS2Time getCreated() {
        byte[] bytes = new byte[8];
        
        for (int i = 0; i < 8; i++) {
            bytes[i] = data[0x08 + i];
        }
        
        ByteBuffer buf = ByteBuffer.wrap(bytes, 6, 2).order(ByteOrder.LITTLE_ENDIAN);
        return new PS2Time(bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], ((ByteBuffer) buf.flip()).getInt());
    }
    
    /**
     * Get the cluster that this page is pointing to.
     * Only effective for directory entries, file data will likely give back straight garbage.
     */
    public int getPointingCluster() {
        byte[] bytes = new byte[4];
        
        for (int i = 0; i < 4; i++) {
            bytes[i] = data[0x10 + i];
        }
        
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    public int getDirEntryInParentDir() {
        byte[] bytes = new byte[4];
        
        for (int i = 0; i < 4; i++) {
            bytes[i] = data[0x14 + i];
        }
        
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    public PS2Time getModified() {
        byte[] bytes = new byte[8];
        
        for (int i = 0; i < 8; i++) {
            bytes[i] = data[0x18 + i];
        }
        
        ByteBuffer buf = ByteBuffer.wrap(bytes, 6, 2).order(ByteOrder.LITTLE_ENDIAN);
        return new PS2Time(bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], ((ByteBuffer) buf.flip()).getInt());
    }
    
    /**
     * Get a chunk of a directory entry that is documented as "user defined".
     * Really no idea what is supposed to be here, if this is supposed to be used at all,
     * it's typically all blank anyways. Only effective for directory entries,
     * file data will likely give back straight garbage.
     */
    public String getUserDef() {
        String ret = new String();
        
        for (int i = 0x20; i < 0x40; i++) {
            ret += Character.valueOf((char) data[i]);
        }
        
        return ret;
    }
    
    /**
     * Get the name of a directory entry. Only effective for directory entries,
     * file data will likely give back straight garbage.
     */
    public String getName() {
        String ret = new String();
        
        // Technically a directory name CAN span the entire dead space of the directory.
        // So for safety we will check it. But likely story is it's just all nulls.
        for (int i = 0x40; i < data.length; i++) {
            ret += Character.valueOf((char) data[i]);
        }
        
        return ret;
    }
}
