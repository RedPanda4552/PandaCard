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
package io.github.redpanda4552.PandaCard.SaveFiles;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;

import io.github.redpanda4552.PandaCard.util.PS2File;

public abstract class AbstractSave {

    protected File file;
    protected byte[] rawData;
    protected String directoryName;
    protected ArrayList<PS2File> files = new ArrayList<PS2File>();
    
    public AbstractSave(File file) throws IOException {
        this.file = file;
        rawData = new byte[(int) file.length()];
        InputStream iStream = Files.newInputStream(file.toPath());
        iStream.read(rawData);
        iStream.close();
    }
    
    public abstract SaveType getSaveType();
    
    /**
     * The name of the directory that holds this save file's contents on an actual PS2 memory card.
     * Trim removes null characters that are present from the actual files and byte stream.
     */
    public String getDirectoryName() {
        return directoryName.trim();
    }
    
    public ArrayList<PS2File> getFiles() {
        return files;
    }
}
