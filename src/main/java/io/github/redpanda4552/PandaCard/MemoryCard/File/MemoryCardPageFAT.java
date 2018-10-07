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

public class MemoryCardPageFAT extends MemoryCardPage {

    private int[] table;
    
    public MemoryCardPageFAT(int pageNumber, byte[] rawData) {
        super(pageNumber, rawData);
        table = new int[PAGE_DATA_SIZE_BYTES / 4];
        byte[] data = getData(), buf = new byte[4];
        int byteRead = 0;
        
        for (int i = 0; i < data.length; i++) {
            if (byteRead == 0) {
                buf[0] = data[i];
                byteRead++;
            } else if (byteRead == 1) {
                buf[1] = data[i];
                byteRead++;
            } else if (byteRead == 2) {
                buf[2] = data[i];
                byteRead++;
            } else if (byteRead == 3) {
                buf[3] = data[i];
                table[Math.floorDiv(i, 4)] = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
                byteRead = 0;
                System.out.print(String.format("0x%08X ", table[Math.floorDiv(i, 4)] = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt()));
            }
        }
        System.out.print("\n");
    }
    
    public int[] getFAT() {
        return table;
    }
}
