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
package io.github.redpanda4552.PandaCard.JavaFX.Menus;

import java.io.IOException;

import io.github.redpanda4552.PandaCard.Main;
import io.github.redpanda4552.PandaCard.JavaFX.Gui;
import io.github.redpanda4552.PandaCard.JavaFX.GuiMenuBar;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MenuFile extends AbstractMenu {
    
    public MenuFile(Main main, Gui guiMain, GuiMenuBar guiMainMenuBar, String displayText) {
        super(main, guiMain, guiMainMenuBar, displayText);
        
        MenuItem file = new MenuItem("File (PCSX2, Play!, DobieStation)");
        file.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Memory Card File");
            fileChooser.getExtensionFilters().add(new ExtensionFilter("Memory Card File", "*.ps2"));
            
            try {
                main.setMemoryCard(fileChooser.showOpenDialog(guiMain.getPrimaryStage()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        MenuItem folder = new MenuItem("Folder (PCSX2)");
        folder.setOnAction((event) -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Select Memory Card Folder");
            
            try {
                main.setMemoryCard(dirChooser.showDialog(guiMain.getPrimaryStage()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        Menu loadMemoryCard = new Menu("Load Memory Card");
        loadMemoryCard.getItems().addAll(file, folder);
        
        MenuItem selectSaveFile = new MenuItem("Select PS2 Save Game File");
        selectSaveFile.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select PS2 Save Game File");
            // .cbs and .xps to come later
            fileChooser.getExtensionFilters().add(new ExtensionFilter("PS2 Save File","*.max"));
            
            try {
                main.setSaveGame(fileChooser.showOpenDialog(guiMain.getPrimaryStage()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        CheckMenuItem advancedMode = new CheckMenuItem("Advanced Mode");
        advancedMode.setSelected(false);
        advancedMode.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            guiMain.setAdvancedMode(newValue);
        });
        
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction((event) -> {
            System.exit(0);
        });
        
        getItems().addAll(loadMemoryCard, selectSaveFile, new SeparatorMenuItem(), advancedMode, new SeparatorMenuItem(), exit);
    }
    
    @Override
    public void update() {
        
    }
}
