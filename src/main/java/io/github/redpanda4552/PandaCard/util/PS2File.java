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
package io.github.redpanda4552.PandaCard.util;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * A representation of a single file of a PS2 memory card.
 */
public class PS2File {
    
    private SimpleIntegerProperty size;
    private SimpleStringProperty fileName;
    private byte[] data;
    
    public PS2File(String fileName, byte[] data) {
        this.size = new SimpleIntegerProperty(data.length);
        this.fileName = new SimpleStringProperty(fileName);
        this.data = data;
    }
    
    /**
     * Get the size in bytes of this file.
     * Equals the length of {@link PS2File#getData() getData()}.
     */
    public int getSize() {
        return size.get();
    }
    
    /**
     * Get the name of this file, as it would appear in the PS2 memory card file system.
     * Trim removes null characters that are present from the actual files and byte stream,
     * so that writing the files to the host filesystem later on plays nicely.
     */
    public String getFileName() {
        return fileName.get().trim();
    }
    
    /**
     * Get the file contents as a byte array.
     */
    public byte[] getData() {
        return data;
    }
}
