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

import io.github.redpanda4552.PandaCard.Config;
import io.github.redpanda4552.PandaCard.Main;
import io.github.redpanda4552.PandaCard.JavaFX.Gui;
import io.github.redpanda4552.PandaCard.JavaFX.GuiMenuBar;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MenuExport extends AbstractMenu {

    private MenuItem toFile, toFolder;
    private CheckMenuItem attemptDelete;
    
    public MenuExport(Main main, Gui guiMain, GuiMenuBar guiMainMenuBar, String displayText) {
        super(main, guiMain, guiMainMenuBar, displayText);
        toFile = new MenuItem("To File (PCSX2, Play!, DobieStation)");
        toFile.setDisable(true);
        toFile.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save to Memory Card File");
            fileChooser.getExtensionFilters().add(new ExtensionFilter("Memory Card File", "*.ps2"));
            
            main.writeToFile(fileChooser.showSaveDialog(guiMain.getPrimaryStage()));
        });
        
        toFolder = new MenuItem("To Folder (PCSX2)");
        toFolder.setDisable(true);
        toFolder.setOnAction((event) -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Save to Memory Card Folder");
            
            main.writeToFolder(directoryChooser.showDialog(guiMain.getPrimaryStage()));
        });
        
        attemptDelete = new CheckMenuItem("Attempt to write deleted files");
        attemptDelete.setSelected(false);
        attemptDelete.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            Config.attemptToWriteDeleted = newValue;
        });
        
        getItems().addAll(toFile, toFolder, new SeparatorMenuItem(), attemptDelete);
    }

    @Override
    public void update() {
        boolean disable = main.getMemoryCard() == null || main.getMemoryCard().getRootDirectory() == null;
        toFile.setDisable(disable);
        toFolder.setDisable(disable);
    }

}
