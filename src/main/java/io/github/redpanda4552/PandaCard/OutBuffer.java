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
package io.github.redpanda4552.PandaCard;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

import io.github.redpanda4552.PandaCard.MemoryCard.AbstractMemoryCard;
import io.github.redpanda4552.PandaCard.MemoryCard.Directory;
import io.github.redpanda4552.PandaCard.util.ECC;
import io.github.redpanda4552.PandaCard.util.PS2Time;

public class OutBuffer {
    
    private final int DOT_MODE = 0x8427, ROOT_DOUBLE_DOT_MODE = 0xa426; 
    private final int WORD = 4, HALF_WORD = 2, PAGE_BYTES = 528, CLUSTER_BYTES = PAGE_BYTES * 2;
    private final byte[]
            WORD_ZERO = new byte[] {
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0
            },
            SINGLE_DOT = new byte[] {
                    (byte) '.'
            },
            DOUBLE_DOT = new byte[] {
                    (byte) '.', (byte) '.'
            };

    private byte[]
            superblock = new byte[528],
            unused = new byte[15 * 528],
            iFAT = new byte[2 * 528],
            FAT = new byte[64 * 528],
            allocatable = new byte[16000 * 528],
            reserved = new byte[270 * 528],
            backup2 = new byte[16 * 528],
            backup1 = new byte[16 * 528];
    
    private int superblockIndex = 0, iFATIndex = 0, FATIndex = 0, allocatableIndex = 0;
    /**
     * Tracks the index of the next allocatable cluster to use.
     * Used to tell the FAT where to point to!
     */
    private int allocatableClusterIndex = 0; //directoryLevel = 0, , fatClusterIndex = 0, fatEntry = 0;
    
    public OutBuffer() {
        for (int i = 0; i < superblock.length; i++)
            superblock[i] = (byte) 0xff;
        
        for (int i = 0; i < unused.length; i++)
            unused[i] = (byte) 0xff;
        
        for (int i = 0; i < iFAT.length; i++)
            iFAT[i] = (byte) 0xff;
        
        for (int i = 0; i < FAT.length; i++)
            FAT[i] = (byte) 0xff;
        
        for (int i = 0; i < allocatable.length; i++)
            allocatable[i] = (byte) 0xff;
        
        for (int i = 0; i < reserved.length; i++)
            reserved[i] = (byte) 0xff;
        
        for (int i = 0; i < backup2.length; i++)
            backup2[i] = (byte) 0xff;
        
        for (int i = 0; i < backup1.length; i++)
            backup1[i] = (byte) 0xff;
    }
    
    public byte[] getSuperblock() {
        return superblock;
    }
    
    public void writeSuperblock(byte[] bytes) {
        for (byte b : bytes)
            superblock[superblockIndex++] = b;
    }
    
    public void autoSuperblock(AbstractMemoryCard memoryCard) {
        // magic
        writeSuperblock(new String("Sony PS2 Memory Card Format ").getBytes());
        // version
        if (memoryCard.isFormatted()) {
            writeSuperblock(memoryCard.getVersion().getBytes());
        } else {
            writeSuperblock(new String("1.2.0.0     ").getBytes());
        }
        // page_len
        writeSuperblock(ByteBuffer.allocate(HALF_WORD).order(ByteOrder.LITTLE_ENDIAN).putChar((char) 512).array());
        // pages_per_cluster
        writeSuperblock(ByteBuffer.allocate(HALF_WORD).order(ByteOrder.LITTLE_ENDIAN).put((byte) 2).array());
        // pages_per_block
        writeSuperblock(ByteBuffer.allocate(HALF_WORD).order(ByteOrder.LITTLE_ENDIAN).put((byte) 16).array());
        // unused?
        writeSuperblock(ByteBuffer.allocate(HALF_WORD).order(ByteOrder.LITTLE_ENDIAN).putChar((char) 0xff00).array());
        // clusters_per_card
        writeSuperblock(ByteBuffer.allocate(WORD).order(ByteOrder.LITTLE_ENDIAN).putInt(8192).array());
        // alloc_offset
        writeSuperblock(ByteBuffer.allocate(WORD).order(ByteOrder.LITTLE_ENDIAN).putInt(41).array());
        // alloc_end
        writeSuperblock(ByteBuffer.allocate(WORD).order(ByteOrder.LITTLE_ENDIAN).putInt(8135).array());
        // rootdir_cluster
        writeSuperblock(ByteBuffer.allocate(WORD).order(ByteOrder.LITTLE_ENDIAN).putInt(0).array());
        // backup_block1
        writeSuperblock(ByteBuffer.allocate(WORD).order(ByteOrder.LITTLE_ENDIAN).putInt(1023).array());
        // backup_block2
        writeSuperblock(ByteBuffer.allocate(WORD).order(ByteOrder.LITTLE_ENDIAN).putInt(1022).array());
        // awkward 8 byte empty space, according to the docs we put a word at 0x44, then a word[32] at 0x50...
        writeSuperblock(WORD_ZERO);
        writeSuperblock(WORD_ZERO);
        // ifc_list
        writeSuperblock(ByteBuffer.allocate(WORD * 32).order(ByteOrder.LITTLE_ENDIAN).putInt(8).array());
        // bad_block_list
        for (int i = 0; i < WORD * 32; i++)
            writeSuperblock(new byte[] {(byte) 0xff});
        // card_type
        writeSuperblock(new byte[] {(byte) 2});
        // card_flags
        writeSuperblock(new byte[] {(byte) 0x2b});
        
        while (superblockIndex < 512)
            writeSuperblock(ByteBuffer.allocate(HALF_WORD).array());
        
        ECC ecc = new ECC(superblock, 0, 512);
        writeSuperblock(ecc.get());
    }
    
    public byte[] getUnused() {
        return unused;
    }
    
    public byte[] getIndirectFAT() {
        return iFAT;
    }
    
    public void writeIndirectFAT(byte[] bytes) {
        for (byte b : bytes)
            iFAT[iFATIndex++] = b;
    }
    
    public byte[] getFAT() {
        return FAT;
    }
    
    public void writeFAT(byte[] bytes) {
        for (byte b : bytes)
            FAT[FATIndex++] = b;
    }
    
    public byte[] getAllocatable() {
        return allocatable;
    }
    
    public void writeAllocatable(byte[] bytes) {
        for (byte b : bytes)
            allocatable[allocatableIndex++] = b;
    }
    
    public void autoWrite(Directory rootDirectory) {
        // Indirect FAT
        for (int i = 9; i < 0x28; i++) {
            byte[] buf = ByteBuffer.allocate(4).putInt(i).order(ByteOrder.LITTLE_ENDIAN).array();
            writeIndirectFAT(buf);
        }
        
        // Files/FAT
        recurseSubdirectories(rootDirectory, true);
    }
    
    private void recurseSubdirectories(Directory dir, boolean isRoot) {
        if (!dir.getSubdirectories().isEmpty()) {
            // .
            byte[] mode, length, currentTime;
            mode = ByteBuffer.allocate(4).putInt(DOT_MODE).order(ByteOrder.LITTLE_ENDIAN).array();
            length = ByteBuffer.allocate(4).putInt(dir.getSubdirectories().size()).order(ByteOrder.LITTLE_ENDIAN).array();
            currentTime = PS2Time.fromCurrent().asByteArr();
            writeDirectory(mode, length, currentTime, WORD_ZERO, WORD_ZERO, currentTime, WORD_ZERO, SINGLE_DOT, false);
            
            // ..
            mode = ByteBuffer.allocate(4).putInt(isRoot ? ROOT_DOUBLE_DOT_MODE : DOT_MODE).order(ByteOrder.LITTLE_ENDIAN).array();
            writeDirectory(mode, length, currentTime, WORD_ZERO, WORD_ZERO, currentTime, WORD_ZERO, DOUBLE_DOT, dir.getSubdirectories().isEmpty());
            
            // Subdirectories
            Iterator<Directory> iter = dir.getSubdirectories().iterator();
            
            while (iter.hasNext()) {
                Directory sub = iter.next();
                mode = ByteBuffer.allocate(4).putInt(DOT_MODE).order(ByteOrder.LITTLE_ENDIAN).array();
                int len = dir.getSubdirectories().size();
                
                if (len == 0 && dir.getPS2File() != null)
                    len = dir.getPS2File().getSize();
                
                length = ByteBuffer.allocate(4).putInt(len).order(ByteOrder.LITTLE_ENDIAN).array();
                PS2Time created = dir.getCreated(), modified = dir.getModified();
                
                if (created == null)
                    created = PS2Time.fromCurrent();
                
                if (modified == null)
                    modified = created;
                
                writeDirectory(mode, length, created.asByteArr(), WORD_ZERO, WORD_ZERO, modified.asByteArr(), WORD_ZERO, dir.getDirectoryName().getBytes(), !iter.hasNext());
                recurseSubdirectories(sub, false);
            }
        } else if (dir.getPS2File() != null) {
            writeFile(dir.getPS2File().getData());
        }
    }
    
    private void writeDirectory(byte[] mode, byte[] length, byte[] created, byte[] cluster, byte[] dir_entry, byte[] modified, byte[] attr, byte[] name, boolean isLast) {
        // directory data
        int startIndex = allocatableIndex;
        writeAllocatable(mode);
        writeAllocatable(length);
        writeAllocatable(created);
        writeAllocatable(cluster);
        writeAllocatable(dir_entry);
        writeAllocatable(modified);
        writeAllocatable(attr);
        writeAllocatable(name);
        
        // filler
        writeFiller();
        
        int endIndex = allocatableIndex;
        
        // ECC
        ECC ecc = new ECC(allocatable, startIndex, endIndex);
        writeAllocatable(ecc.get());
        
        // FAT
        byte[] buf = ByteBuffer.allocate(4).putInt(isLast ? 0xffffffff : ++allocatableClusterIndex + 0x80000000).order(ByteOrder.LITTLE_ENDIAN).array();
        writeFAT(buf);
    }
    
    private void writeFile(byte[] bytes) {
        int startIndex = allocatableIndex;
        int byteGroup = 0;
        
        // file data
        while (byteGroup * PAGE_BYTES < bytes.length) {
            
            if (bytes.length - (byteGroup * (PAGE_BYTES - 16)) < 0) {
                
            }
            
            byte[] pageBytes = null;
            
            if (true) {
                pageBytes = ByteBuffer.wrap(bytes, byteGroup * (PAGE_BYTES - 16), 512).order(ByteOrder.LITTLE_ENDIAN).array();
            }
            
            writeAllocatable(pageBytes);
        }
        
        writeAllocatable(bytes);
        
        // filler
        writeFiller();
        
        int endIndex = allocatableIndex;
        
        // ECC
        ECC ecc = new ECC(allocatable, startIndex, endIndex);
        writeAllocatable(ecc.get());
        
        // FAT
        //byte[] buf = ByteBuffer.allocate(4).putInt(value)
    }
    
    /**
     * Write zeros until the end of the current page's data section.
     */
    private void writeFiller() {
        while (allocatableIndex % (PAGE_BYTES - 16) != 0)
            allocatable[allocatableIndex++] = 0;
    }
}
