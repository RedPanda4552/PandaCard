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
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Optional;

import io.github.redpanda4552.PandaCard.JavaFX.AlertYesNo;
import io.github.redpanda4552.PandaCard.JavaFX.Gui;
import io.github.redpanda4552.PandaCard.MemoryCard.AbstractMemoryCard;
import io.github.redpanda4552.PandaCard.MemoryCard.Directory;
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
    private GameDB gameDB;
    private ArrayList<String> consoleFeed = new ArrayList<String>();
    
    private File saveFile, memoryCardFile;
    private AbstractSave save;
    private AbstractMemoryCard memoryCard;
    
    @Override
    public void start(Stage primaryStage) {
        self = this;
        guiMain = new Gui(this, primaryStage);
        gameDB = new GameDB();
    }
    
    public void console(String... strArr) {
        for (String s : strArr) {
            consoleFeed.add(s);
        }
        
        updateGui();
    }
    
    public GameDB getGameDB() {
        return gameDB;
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
    }
    
    public void writeToFile(File file) {
        if (file == null)
            return;
        
        if (memoryCard == null) {
            console("No content to export!");
            return;
        }
        
        try {
            file.createNewFile();
            OutBuffer buf = new OutBuffer();
            buf.autoSuperblock(memoryCard);
            
            Directory rootDir = memoryCard.getRootDirectory();
            
        } catch (IOException e) {
            console("Exception while creating file " + file.getName() + ": " + e.getMessage());
            return;
        }
    }
    
    public void writeToFolder(File file) {
        if (file == null)
            return;
        
        if (memoryCard == null) {
            console("No content to export!");
            return;
        }
        
        OutBuffer buf = new OutBuffer();
        
        try {
            // Superblock
            buf.autoSuperblock(memoryCard);
            File superblockFile = new File(file.getPath() + "/_pcsx2_superblock");
            superblockFile.createNewFile();
            OutputStream superblockStream = Files.newOutputStream(superblockFile.toPath());
            superblockStream.write(buf.getSuperblock());
            superblockStream.close();
            
            // Directories
            Directory rootDir = memoryCard.getRootDirectory();
            
            for (Directory gameDir : rootDir.getSubdirectories()) {
                File gameFolder = new File(file.getPath() + "/" + gameDir.getDirectoryName());
                ArrayList<Directory> writeQueue = new ArrayList<Directory>();
                
                for (Directory fileDir : gameDir.getSubdirectories()) {
                    if (fileDir.getPS2File() != null) {
                        if (!fileDir.isDeleted() || Config.attemptToWriteDeleted) {
                            writeQueue.add(fileDir);
                        }
                    }
                }
                
                if (writeQueue.size() > 0) {
                    gameFolder.mkdir();
                    
                    for (Directory toWrite : writeQueue) {
                        File gameFile = new File(gameFolder.getPath() + "/" + toWrite.getPS2File().getFileName());
                        gameFile.createNewFile();
                        OutputStream gameFileStream = Files.newOutputStream(gameFile.toPath());
                        gameFileStream.write(toWrite.getPS2File().getData());
                        gameFileStream.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        console("Memory Card contents written to " + file.getName());
    }
}
