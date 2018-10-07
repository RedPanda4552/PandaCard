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

import java.io.IOException;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class UpdateChecker {

    public static boolean UPDATE_AVAILABLE = false;
    public static String CURRENT_VERSION;
    
    public UpdateChecker() {
        final Properties p = new Properties();
        
        try {
            p.load(Main.class.getResourceAsStream("/maven.properties"));
            CURRENT_VERSION = p.getProperty("version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.github.com/repos/RedPanda4552/PandaCard/releases/latest").asJson();
            JSONObject obj = jsonResponse.getBody().getObject();
            String newVersion = obj.getString("tag_name");
            
            if (!newVersion.equals(CURRENT_VERSION))
                UPDATE_AVAILABLE = true;
        } catch (UnirestException | JSONException e) {
            e.printStackTrace();
        }
    }
}
