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
import io.github.redpanda4552.PandaCard.MemoryCard.AbstractMemoryCard;
import io.github.redpanda4552.PandaCard.MemoryCard.Directory;
import io.github.redpanda4552.PandaCard.MemoryCard.File.FileMemoryCard;
import io.github.redpanda4552.PandaCard.MemoryCard.Folder.FolderMemoryCard;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class VBoxMemoryCard extends VBox {

    private Main main;
    
    private StyledText hostFolderName;
    private TreeView<String> memoryCardContents = new TreeView<String>();
    
    public VBoxMemoryCard(Main main) {
        HBox.setHgrow(this, Priority.ALWAYS);
        this.main = main;
        hostFolderName = new StyledText("File > Load Memory Card");
        this.getChildren().addAll(hostFolderName, memoryCardContents);
    }
    
    public void update() {
        AbstractMemoryCard memoryCard = main.getMemoryCard();
        
        if (memoryCard != null) {
            StringBuilder sb = new StringBuilder();
            
            if (memoryCard instanceof FolderMemoryCard) {
                sb.append(" [PCSX2 Folder]");
            } else if (memoryCard instanceof FileMemoryCard) {
                sb.append(" [PCSX2/Play!/DobieStation File]");
            }
            
            if (memoryCard.isModified()) {
                sb.append(" [Modified]");
            }
            
            hostFolderName.setText(sb.toString());
            TreeItem<String> root = createTreeItem(memoryCard.getRootDirectory());
            memoryCardContents.setRoot(root);
        }
    }
    
    private TreeItem<String> createTreeItem(Directory directory) {
        StringBuilder sb = new StringBuilder(directory.getDirectoryName().trim());
        
        if (directory.isDeleted()) {
            sb.append(" [Deleted]");
        }
        
        TreeItem<String> item = new TreeItem<String>(sb.toString());
        
        for (Directory sub : directory.getSubdirectories()) {
            item.getChildren().add(createTreeItem(sub));
        }
        
        return item;
    }
}
