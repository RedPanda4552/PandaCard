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

import java.io.File;
import java.util.ArrayList;

import io.github.redpanda4552.PandaCard.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class VBoxMemoryCardPanel extends VBox {

    private Main main;
    
    private Text hostFolderName;
    private ListView<String> memoryCardContents = new ListView<String>();
    
    public VBoxMemoryCardPanel(Main main) {
        HBox.setHgrow(this, Priority.ALWAYS);
        this.main = main;
        hostFolderName = new Text("File > Select PCSX2 Folder Memory Card");
        this.getChildren().addAll(hostFolderName, memoryCardContents);
    }
    
    public void update() {
        if (main.getFolderMemoryCard() != null) {
            hostFolderName.setText(main.getFolderMemoryCard().getRoot().toString());
            ArrayList<String> fileNames = new ArrayList<String>();
            
            for (File file : main.getFolderMemoryCard().getContents()) {
                fileNames.add(file.getName());
            }
            
            ObservableList<String> items = FXCollections.observableArrayList(fileNames);
            memoryCardContents.setItems(items);
        }
    }
    
}
