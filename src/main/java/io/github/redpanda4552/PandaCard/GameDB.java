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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class GameDB {

    private final String DOWNLOAD_URL = "https://raw.githubusercontent.com/PCSX2/pcsx2/master/bin/GameIndex.dbf";
    private final String DB_LOCATION = "games.db";
    
    private Connection connection;
    
    public GameDB() {
        try {
            File file = new File(DB_LOCATION);
            
            if (!file.exists()) {
                Main.getSelf().console("No game database file found! Game names will not be listed in the memory card panel. The database can be built by going to File > Rebuild Game Database");
                return;
            }
            
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_LOCATION);
            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS games (serial TEXT PRIMARY KEY, name TEXT, region TEXT);");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isConnectionReady() {
        return connection != null;
    }
    
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public String getGameName(String serial) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT name FROM games WHERE serial = ?;");
            ps.setString(1, serial);
            ResultSet res = ps.executeQuery();
            return res.next() ? res.getString("name") : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public String getGameRegion(String serial) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT region FROM games WHERE serial = ?;");
            ps.setString(1, serial);
            ResultSet res = ps.executeQuery();
            return res.next() ? res.getString("region") : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public void rebuild() {
        try {
            URL url = new URL(DOWNLOAD_URL);
            InputStream iStream = url.openStream();
            Scanner scanner = new Scanner(iStream);
            String line = null, serial = null, name = null, region = null;
            
            // Wipe out the table
            PreparedStatement ps = connection.prepareStatement("DELETE FROM games;");
            ps.executeUpdate();
            
            while (scanner.hasNextLine()) {
                line = scanner.nextLine().trim();
                
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("//"))
                    continue;
                
                if (line.startsWith("Serial")) {
                    serial = line.split(" = ")[1];
                    if (serial.equals("TCPS-10172"))
                        System.out.println("Should be done");
                } else if (line.startsWith("Name")) {
                    name = line.split(" = ")[1];
                } else if (line.startsWith("Region")) {
                    region = line.split(" = ")[1];
                }
                
                if (serial != null && name != null && region != null) {
                    ps = connection.prepareStatement("INSERT INTO games (serial, name, region) VALUES (?, ?, ?);");
                    ps.setString(1, serial);
                    ps.setString(2, name);
                    ps.setString(3, region);
                    ps.executeUpdate();
                    serial = null; name = null; region = null;
                }
            }
            
            scanner.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
