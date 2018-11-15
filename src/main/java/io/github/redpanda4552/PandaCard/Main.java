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
import java.util.ArrayList;
import java.util.Optional;

import io.github.redpanda4552.PandaCard.JavaFX.AlertYesNo;
import io.github.redpanda4552.PandaCard.JavaFX.Gui;
import io.github.redpanda4552.PandaCard.MemoryCard.AbstractMemoryCard;
import io.github.redpanda4552.PandaCard.MemoryCard.File.FileMemoryCard;
import io.github.redpanda4552.PandaCard.MemoryCard.Folder.FolderMemoryCard;
import io.github.redpanda4552.PandaCard.SaveFiles.AbstractSave;
import io.github.redpanda4552.PandaCard.SaveFiles.SaveFactory;
import javafx.application.Application;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Main extends Application {
    
    private static Main self;
    
    public static void main(String[] args) {
        new UpdateChecker();
        launch(args);
    }
    
    public static Main getSelf() {
        return self;
    }
    
    private Gui guiMain;
    private ArrayList<String> consoleFeed = new ArrayList<String>();
    
    private File saveFile, memoryCardFile;
    private AbstractSave save;
    private AbstractMemoryCard memoryCard;
    private FolderMemoryCard folderMemoryCard;
    private boolean attemptToWriteDeleted = false;
    
    @Override
    public void start(Stage primaryStage) {
        self = this;
        guiMain = new Gui(this, primaryStage);
    }
    
    public void console(String... strArr) {
        for (String s : strArr) {
            consoleFeed.add(s);
        }
        
        updateGui();
    }
    
    public void updateGui() {
        guiMain.update();
    }
    
    public ArrayList<String> getConsoleFeed() {
        return consoleFeed;
    }
    
    public void setSaveGame(File saveFile) throws IOException {
        if (saveFile == null) return;
        this.saveFile = saveFile;
        this.save = SaveFactory.buildFromFile(saveFile);
        StringBuilder sb = new StringBuilder("Loaded game save \"");
        sb.append(save.getDirectoryName())
          .append("\" (").append(save.getFiles().size())
          .append(" files)");
        console(sb.toString());
    }
    
    public File getSaveFile() {
        return saveFile;
    }
    
    public AbstractSave getSaveGame() {
        return save;
    }
    
    public void setMemoryCard(File file) throws IOException {
        if (file == null) return;
        
        if (!file.getName().endsWith(".ps2")) {
            console("Not a valid PCSX2, Play!, or Dobiestation memory card! It should have a \".ps2\" extension!");
            return;
        }
        
        this.memoryCardFile = file;
        
        if (file.isDirectory()) {
            memoryCard = new FolderMemoryCard(file);
        } else if (file.isFile()) {
            memoryCard = new FileMemoryCard(file);
        }
        
        StringBuilder sb = new StringBuilder("Loaded memory card \"");
        sb.append(memoryCardFile.getName()).append("\" (")
          .append(memoryCard.getFileCount()).append(" files)");
        console(sb.toString());
        
        if (!memoryCard.isFormatted()) {
            console("WARNING!");
            console("This Memory Card is NOT FORMATTED! Emulators will require formatting, which may WIPE any data present!");
        }
    }
    
    public AbstractMemoryCard getMemoryCard() {
        return memoryCard;
    }
    
    /*
    public void setFolderMemoryCard(File file) throws IOException {
        if (file == null) return;
        
        if (!file.getName().endsWith(".ps2")) {
            console("Not a valid PCSX2 Folder Memory Card! It should have a \".ps2\" extension!");
            return;
        }
        
        this.memoryCardFile = file;
        this.fileMemoryCard = null;
        this.folderMemoryCard = new FolderMemoryCard(file);
        StringBuilder sb = new StringBuilder("PCSX2 Folder Memory Card loaded: \"");
        sb.append(file.getName()).append("\", ")
          .append(folderMemoryCard.getContents().size()).append(" items inside");
        console(sb.toString());
        if (!folderMemoryCard.isFormatted())
            console("Warning! Folder Memory Card is not formatted!");
    }
    */
    
    public File getFolderMemoryCardFile() {
        return memoryCardFile;
    }
    
    /*
    public FolderMemoryCard getFolderMemoryCard() {
        return folderMemoryCard;
    }
    */
    
    public void setAttemptToWriteDeleted(boolean b) {
        this.attemptToWriteDeleted = b;
    }
    
    /**
     * Merge the selected save file into a folder memory card.
     * If no save file is selected, no action is taken.
     */
    public void mergeSave() {
        if (save == null) return;
        
        if (memoryCard.containsDirectory(save.getDirectoryName())) {
            AlertYesNo alert = new AlertYesNo("Overwrite Files", "A directory matching this save file already exists. Overwrite it?");
            Optional<ButtonType> buttonPressed = alert.showAndWait();
            
            if (buttonPressed.isPresent() && buttonPressed.get() != ButtonType.YES) {
                return;
            }
        }
        
        memoryCard.insertDirectory(save.getDirectoryName(), save.getFiles());
        memoryCard.setModified(true);
        StringBuilder sb = new StringBuilder("Adding game save \"");
        sb.append(save.getDirectoryName()).append("\" to Memory Card (")
          .append(save.getFiles().size()).append(" files)");
        console(sb.toString());
        
        /*
        File saveRoot = new File(folderMemoryCard.getHostFile().toString() + "/" + save.getDirectoryName());
        
        if (saveRoot.exists())
            console("A folder for this game already exists in the Folder Memory Card, overwriting its contents...");
        else
            if (!saveRoot.mkdir()) {
                console("Failed to create root folder for this game in the Folder Memory Card!");
                return;
            }
        
        File out = null;
        
        try {
            for (PS2File ps2File : save.getFiles()) {
                out = new File(saveRoot.toString() + "/" + ps2File.getFileName());
                
                if (out.exists())
                    console("File " + ps2File.getFileName() + " already exists, overwriting...");
                else
                    if (!out.createNewFile()) {
                        console("Failed to create empty file \"" + ps2File.getFileName() + "\"!");
                        return;
                    }
                OutputStream oStream = Files.newOutputStream(out.toPath());
                oStream.write(ps2File.getData());
                oStream.close();
                console("Successfully wrote file \"" + ps2File.getFileName() + "\" (" + ps2File.getSize() + " bytes)");
            }
            
            // Forces a refresh of the list once we're done writing to it
            setMemoryCard(memoryCardFile);
        } catch (IOException e) {
            console("An IOException occurred while trying to write file \"" + out.getName() + "\"!");
            e.printStackTrace();
            return;
        }
        */
    }
    
    public void writeToFile(File file) {
        if (memoryCard == null) {
            console("No content to export!");
            return;
        }
    }
    
    public void writeToFolder() {
        if (memoryCard == null) {
            console("No content to export!");
            return;
        }
    }
}
