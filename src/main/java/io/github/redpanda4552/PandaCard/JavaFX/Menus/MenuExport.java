package io.github.redpanda4552.PandaCard.JavaFX.Menus;

import io.github.redpanda4552.PandaCard.Main;
import io.github.redpanda4552.PandaCard.JavaFX.Gui;
import io.github.redpanda4552.PandaCard.JavaFX.GuiMenuBar;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
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
            main.writeToFolder();
        });
        
        attemptDelete = new CheckMenuItem("Attempt to write deleted files");
        attemptDelete.setSelected(false);
        attemptDelete.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            main.setAttemptToWriteDeleted(newValue);
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
