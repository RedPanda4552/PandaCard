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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import io.github.redpanda4552.PandaCard.util.LZAri;
import io.github.redpanda4552.PandaCard.util.PS2File;

public class MaxSave extends AbstractSave {

    private int decompressedSize, filesInArchive;
    
    public MaxSave(File file) throws IOException {
        super(file);
        byte[] dirBuf = new byte[32];
        
        for (int i = 0; i < dirBuf.length; i++) {
            dirBuf[i] = rawData[0x10 + i];
        }
        
        directoryName = new String(dirBuf);
        byte[] filesBuf = new byte[4];
        
        for (int i = 0; i < filesBuf.length; i++) {
            filesBuf[i] = rawData[0x54 + i];
        }
        
        filesInArchive = ByteBuffer.wrap(filesBuf).order(ByteOrder.LITTLE_ENDIAN).getInt();
        byte[] decompressBuf = new byte[4];
        
        for (int i = 0; i < decompressBuf.length; i++) {
            decompressBuf[i] = rawData[0x58 + i];
        }
        
        decompressedSize = ByteBuffer.wrap(decompressBuf).order(ByteOrder.LITTLE_ENDIAN).getInt();
        files = getArchivedFiles();
    }

    private ArrayList<PS2File> getArchivedFiles() throws IOException {
        LZAri lzari = new LZAri();
        byte[] decodedBytes = lzari.decode(rawData, 0x58, rawData.length - 0x58);
        ArrayList<PS2File> ret = new ArrayList<PS2File>();
        int fileOffset = 0;
        
        for (int currentFile = 0; currentFile < getFilesInArchive(); currentFile++) {
            byte[] sizeBuf = new byte[4], nameBuf = new byte[32];
            
            // Grab the size bytes
            for (int i = fileOffset + 0x00; i < fileOffset + 0x04; i++) {
                sizeBuf[i - fileOffset] = decodedBytes[i];
            }
            
            int size = ByteBuffer.wrap(sizeBuf).order(ByteOrder.LITTLE_ENDIAN).getInt();
            byte[] dataBuf = new byte[size];
            
            // Now the name bytes
            for (int i = fileOffset + 0x04; i < fileOffset + 0x24; i++) {
                nameBuf[i - 0x04 - fileOffset] = decodedBytes[i];
            }
            
            String name = new String(nameBuf);
            
            // Data next
            for (int i = fileOffset + 0x24; i < fileOffset + 0x24 + size; i++) {
                dataBuf[i - 0x24 - fileOffset] = decodedBytes[i];
            }
            
            // Finally, detect padding and set our fileOffset accordingly.
            // Skip this step on the last file.
            if (currentFile < this.getFilesInArchive() - 1) {
                boolean scan = true;
                int firstByte = fileOffset + 0x24 + size;
                
                while (scan) {
                    byte[] padBuf = new byte[4]; // Setting this to 2 creates an underflow in the ByteBuffer later?
                    
                    for (int i = firstByte; i < firstByte + 2; i++) {
                        padBuf[i - firstByte] = decodedBytes[i];
                    }
                    
                    // Padding seems to be... unpredictable. We'll scan two bytes at a time for anything nonzero.
                    if (ByteBuffer.wrap(padBuf).order(ByteOrder.LITTLE_ENDIAN).getInt() != 0) {
                        fileOffset = firstByte;
                        break;
                    } else {
                        firstByte += 2;
                    }
                }
            }
                
            ret.add(new PS2File(name, dataBuf));
        }
        
        return ret;
    }
    
    public int getFilesInArchive() {
        return filesInArchive;
    }
    
    public int getDecompressedSize() {
        return decompressedSize;
    }
    
    @Override
    public SaveType getSaveType() {
        return SaveType.AR_MAX;
    }

}
