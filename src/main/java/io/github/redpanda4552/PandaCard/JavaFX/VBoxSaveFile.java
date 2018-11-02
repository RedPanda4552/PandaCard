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

import io.github.redpanda4552.PandaCard.Main;
import io.github.redpanda4552.PandaCard.util.PS2File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

// Oracle's own docs suggested using the TableColumn and TableView objects
// without templates, and they throw compile errors if you actually do. Sue me.
@SuppressWarnings({ "unchecked", "rawtypes" })
public class VBoxSaveFile extends VBox{

    private Main main;
    
    private Text hostFileName;
    private TableView saveContents;
    
    public VBoxSaveFile(Main main) {
        HBox.setHgrow(this, Priority.ALWAYS);
        this.main = main;
        hostFileName = new Text("File > Select PS2 Save Game File");
        saveContents = new TableView();
        TableColumn ps2FileName = new TableColumn("File Name");
        ps2FileName.setCellValueFactory(new PropertyValueFactory<PS2File, String>("fileName"));
        //ps2FileName.setMinWidth(200);
        TableColumn size = new TableColumn("Size (bytes)");
        size.setCellValueFactory(new PropertyValueFactory<PS2File, Integer>("size"));
        //size.setMinWidth(200);
        saveContents.getColumns().addAll(ps2FileName, size);
        saveContents.setEditable(false);
        this.getChildren().addAll(hostFileName, saveContents);
    }
    
    public void update() {
        if (main.getSaveFile() != null) {
            hostFileName.setText(main.getSaveFile().getName());
            ObservableList<PS2File> files = FXCollections.observableArrayList(main.getSaveGame().getFiles());
            saveContents.setItems(files);
        }
    }
    
}
