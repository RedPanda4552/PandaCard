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
package io.github.redpanda4552.PandaCard.MemoryCard.Folder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import io.github.redpanda4552.PandaCard.MemoryCard.AbstractMemoryCard;
import io.github.redpanda4552.PandaCard.MemoryCard.Directory;

public class FolderMemoryCard extends AbstractMemoryCard {

    private File root;
    private File superblockFile;
    private File[] contents;
    
    public FolderMemoryCard(File file) throws IOException {
        super(file);
        this.root = file;
        contents = file.listFiles();
        
        for (File f : contents) {
            if (f.getName().equals(AbstractMemoryCard.SUPERBLOCK_NAME)) {
                superblockFile = f;
                break;
            }
        }
        
        InputStream iStream = Files.newInputStream(superblockFile.toPath());
        byte[] superblockBuf = new byte[MAGIC_SIZE];
        byte[] versionBuf = new byte[12];
        iStream.read(superblockBuf);
        iStream.read(versionBuf);
        iStream.close();
        formatted = new String(superblockBuf).equals(MAGIC_STRING);
        directory = new Directory(getHostFile(), getHostFile().getAbsolutePath());
        version = new String(versionBuf);
    }
    
    public File getRoot() {
        return root;
    }
}
