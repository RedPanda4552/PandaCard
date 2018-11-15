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
package io.github.redpanda4552.PandaCard.JavaFX;

import java.util.ArrayList;

import io.github.redpanda4552.PandaCard.Main;
import io.github.redpanda4552.PandaCard.MemoryCard.AbstractMemoryCard;
import io.github.redpanda4552.PandaCard.MemoryCard.File.FileMemoryCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class VBoxIndirectFAT extends VBox {

    private Main main;
    
    private StyledText title = new StyledText("Indirect File Allocation Table");
    private ListView<String> iFATContents;
    
    public VBoxIndirectFAT(Main main) {
        HBox.setHgrow(this, Priority.ALWAYS);
        this.main = main;
        iFATContents = new ListView<String>();
        getChildren().addAll(title, iFATContents);
    }
    
    public void update() {
        AbstractMemoryCard memoryCard = main.getMemoryCard();
        iFATContents.setItems(null);
        
        if (memoryCard != null && memoryCard instanceof FileMemoryCard) {
            ArrayList<String> list = new ArrayList<String>();
            
            for (Integer i : ((FileMemoryCard) memoryCard).getIndirectFAT().getIFAT()) {
                list.add(Integer.toHexString(list.size()) + "  \t" + Integer.toHexString(i));
            }
            
            ObservableList<String> items = FXCollections.observableArrayList(list);
            iFATContents.setItems(items);
        }
    }
}
