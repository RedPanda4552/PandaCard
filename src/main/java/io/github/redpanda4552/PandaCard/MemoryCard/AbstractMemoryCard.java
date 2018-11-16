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
import java.util.ArrayList;

import io.github.redpanda4552.PandaCard.util.PS2File;

public abstract class AbstractMemoryCard {

    protected File hostFile;
    protected Directory directory;
    protected boolean formatted, modified = false;
    
    public AbstractMemoryCard(File file) {
        this.hostFile = file;
    }

    /**
     * Get the file/folder on the host file system that contains this memory card.
     */
    public File getHostFile() {
        return hostFile;
    }
    
    public Directory getRootDirectory() {
        return directory;
    }
    
    public boolean isFormatted() {
        return formatted;
    }
    
    public void setModified(boolean b) {
        modified = b;
    }
    
    /**
     * Returns whether or not the current memory card state is modified from the card on disk.
     */
    public boolean isModified() {
        return modified;
    }
    
    public boolean containsDirectory(String directoryName) {
        for (Directory dir : directory.getSubdirectories()) {
            if (dir.getDirectoryName().equals(directoryName)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void insertDirectory(String directoryName, ArrayList<PS2File> ps2Files) {
        Directory baseDir = new Directory(directoryName, null);
        
        for (PS2File ps2File : ps2Files) {
            baseDir.addSubdirectory(new Directory(ps2File.getFileName(), ps2File));
        }
        
        directory.addSubdirectory(baseDir);
    }
    
    public int getFileCount() {
        return getFileCount(directory);
    }
    
    private int getFileCount(Directory dir) {
        int ret = dir.getPS2File() != null ? 1 : 0;
        
        for (Directory sub : dir.getSubdirectories()) {
            ret += getFileCount(sub);
        }
        
        return ret;
    }
}
