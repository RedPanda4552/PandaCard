package io.github.redpanda4552.PandaCard.JavaFX;

import java.util.ArrayList;

import io.github.redpanda4552.PandaCard.Main;
import io.github.redpanda4552.PandaCard.MemoryCard.AbstractMemoryCard;
import io.github.redpanda4552.PandaCard.MemoryCard.File.FileMemoryCard;
import io.github.redpanda4552.PandaCard.MemoryCard.File.FileMemoryCardPageData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class VBoxDirectories extends VBox {

	private Main main;
	
	private Text title = new Text("Directories");
	private ListView<String> directoryContents;
	
	public VBoxDirectories(Main main) {
		HBox.setHgrow(this, Priority.ALWAYS);
		this.main = main;
		directoryContents = new ListView<String>();
		getChildren().addAll(title, directoryContents);
	}
	
	public void update() {
		AbstractMemoryCard memoryCard = main.getMemoryCard();
		directoryContents.setItems(null);
		
		if (memoryCard != null && memoryCard instanceof FileMemoryCard) {
			FileMemoryCard mc = (FileMemoryCard) memoryCard;
			ArrayList<String> list = new ArrayList<String>();

			for (FileMemoryCardPageData page : mc.getDataPages()) {
				if (page.isValidDirectory()) {
					StringBuilder sb = new StringBuilder("Page = ").append(Integer.toHexString(page.getPageNumber()));
					sb.append("  \tCluster = ").append(Integer.toHexString(page.getClusterNumber()));
					sb.append("  \tName = ").append(page.getName());
					sb.append("  \tType = ").append((page.isFile() ? (page.isDirectory() ? "Both Flags" : "File") : (page.isDirectory() ? "Directory" : "No Flags")));
					sb.append("  \tLength = ").append(page.getLength());
					sb.append("  \tPoints To = ").append(Integer.toHexString(page.getPointingCluster()));
					if (page.getPointingCluster() >= 0 && page.getPointingCluster() < 0x1F70)
						sb.append("  \tFAT says = ").append(Integer.toHexString(mc.getFAT().getFAT()[page.getClusterNumber()]));
					else
						sb.append("  \tFAT says = Garbage");
					list.add(sb.toString());
				} else {
					StringBuilder sb = new StringBuilder("Page = ").append(Integer.toHexString(page.getPageNumber()));
					sb.append("  \tCluster = ").append(Integer.toHexString(page.getClusterNumber()));
					sb.append("  \t<File Data>");
					list.add(sb.toString());
				}
			}
			
			ObservableList<String> items = FXCollections.observableArrayList(list);
			directoryContents.setItems(items);
		}
	}
}
