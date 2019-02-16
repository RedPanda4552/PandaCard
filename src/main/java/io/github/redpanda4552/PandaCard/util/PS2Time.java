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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class PS2Time {

    private static final String JAPAN_ZONE_ID = "Japan";
    
    public static PS2Time fromCurrent() {
        ZoneId japanZone = ZoneId.of(JAPAN_ZONE_ID);
        ZonedDateTime japanTime = ZonedDateTime.of(LocalDateTime.now(), japanZone);
        return new PS2Time(
                (byte) japanTime.getSecond(),
                (byte) japanTime.getMinute(),
                (byte) japanTime.getHour(),
                (byte) japanTime.getDayOfMonth(),
                (byte) japanTime.getMonthValue(),
                (byte) japanTime.getYear()
        );
    }
    
    private byte sec, min, hour, day, month;
    private int year;
    
    public PS2Time(byte sec, byte min, byte hour, byte day, byte month, int year) {
        this.sec = sec;
        this.min = min;
        this.hour = hour;
        this.day = day;
        this.month = month;
        this.year = year;
    }
    
    public byte getSecond() {
        return sec;
    }
    
    public byte getMinute() {
        return min;
    }
    
    public byte getHour() {
        return hour;
    }
    
    public byte getDay() {
        return day;
    }
    
    public byte getMonth() {
        return month;
    }
    
    public int getYear() {
        return year;
    }
    
    public byte[] asByteArr() {
        byte[] ret = new byte[8];
        
        ret[1] = sec;
        ret[2] = min;
        ret[3] = hour;
        ret[4] = day;
        ret[5] = month;
        byte[] yearArr = ByteBuffer.allocate(4).putInt(year).order(ByteOrder.LITTLE_ENDIAN).array();
        ret[6] = yearArr[0];
        ret[7] = yearArr[1];
        
        return ret;
    }
}
