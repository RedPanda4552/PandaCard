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
import io.github.redpanda4552.PandaCard.UpdateChecker;
import io.github.redpanda4552.PandaCard.util.PandaCardLogo;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Gui {

    private Stage primaryStage;
    private VBox root;
    private GuiMenuBar guiMainMenuBar;
    
    // The main GridPane, and each of it's panes.
    private HBox content;
    private VBoxMemoryCardPanel memoryCardTablePanel;
    private VBoxSaveFilePanel saveFileTablePanel;
    
    // The console, sitting under the GridPane
    private VBoxConsole console;
    
    public Gui(Main main, Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        try {
            guiMainMenuBar = new GuiMenuBar(main, this);
            root = new VBox(
                    guiMainMenuBar,
                    content = new HBox(),
                    console = new VBoxConsole(main)
            );
            memoryCardTablePanel = new VBoxMemoryCardPanel(main);
            saveFileTablePanel = new VBoxSaveFilePanel(main);
            content.getChildren().addAll(memoryCardTablePanel, saveFileTablePanel);
            
            Scene scene = new Scene(root, 800, 600);
            primaryStage.getIcons().add(new PandaCardLogo());
            StringBuilder sb = new StringBuilder("PandaCard v");
            sb.append(UpdateChecker.CURRENT_VERSION);
            
            if (UpdateChecker.UPDATE_AVAILABLE)
                sb.append(" [Update Available: Help > About PandaCard]");
            
            primaryStage.setTitle(sb.toString());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public void update() {
        guiMainMenuBar.update();
        memoryCardTablePanel.update();
        saveFileTablePanel.update();
        console.update();
    }
}
