package io.github.redpanda4552.PandaCard.JavaFX;

import java.util.ArrayList;

import io.github.redpanda4552.PandaCard.Main;
import io.github.redpanda4552.PandaCard.MemoryCard.AbstractMemoryCard;
import io.github.redpanda4552.PandaCard.MemoryCard.File.FileMemoryCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class VBoxFAT extends VBox {

	private Main main;
	
	private StyledText title = new StyledText("File Allocation Table");
	private ListView<String> fatContents;
	
	public VBoxFAT(Main main) {
		HBox.setHgrow(this, Priority.ALWAYS);
		this.main = main;
		fatContents = new ListView<String>();
		getChildren().addAll(title, fatContents);
	}
	
	public void update() {
		AbstractMemoryCard memoryCard = main.getMemoryCard();
		fatContents.setItems(null);
		
		if (memoryCard != null && memoryCard instanceof FileMemoryCard) {
			ArrayList<String> list = new ArrayList<String>();
			
			for (Integer i : ((FileMemoryCard) memoryCard).getFAT().getFAT()) {
				list.add(Integer.toHexString(list.size()) + "  \t" + Integer.toHexString(i));
			}
			
			ObservableList<String> items = FXCollections.observableArrayList(list);
			fatContents.setItems(items);
		}
	}
}
