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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import io.github.redpanda4552.PandaCard.Main;
import io.github.redpanda4552.PandaCard.UpdateChecker;
import io.github.redpanda4552.PandaCard.util.PandaCardLogo;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AlertAbout extends Alert {

    private Main main;
    
    private ImageView imageView;
    
    public AlertAbout() {
        super(AlertType.INFORMATION, "", ButtonType.CLOSE);
        
        // Set the icon
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new PandaCardLogo());
        
        // Title
        setTitle("About PandaCard");
        
        // Right hand graphic
        imageView = new ImageView(new PandaCardLogo());
        setGraphic(imageView);
        
        // Header content
        StringBuilder sb = new StringBuilder("PandaCard ");
        sb.append("v").append(UpdateChecker.CURRENT_VERSION).append("\n")
          .append("Written by \"RedPanda4552\" aka \"pandubz\"\n");
        setHeaderText(sb.toString());
        
        // Body content
        Hyperlink hyperlink = new Hyperlink("https://github.com/RedPanda4552/PandaCard");
        hyperlink.setOnAction((event) -> {
            try {
                Desktop.getDesktop().browse(new URI(hyperlink.getText()));
            } catch (IOException | URISyntaxException e) {
                main.console("Failed to launch web browser!");
                e.printStackTrace();
            }
        });
        FlowPane flowPane = new FlowPane();
        
        if (UpdateChecker.UPDATE_AVAILABLE) {
            Text t = new Text("An update is available! Click the link below to download the latest version.");
            flowPane.getChildren().add(t);
        }
        
        flowPane.getChildren().addAll(hyperlink);
        getDialogPane().contentProperty().set(flowPane);
    }
}
