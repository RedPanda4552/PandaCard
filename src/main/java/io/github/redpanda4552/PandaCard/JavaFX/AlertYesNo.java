package io.github.redpanda4552.PandaCard.JavaFX;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertYesNo extends Alert {

	public AlertYesNo(String title, String bodyText) {
		super(AlertType.NONE, bodyText, ButtonType.YES, ButtonType.NO);
		this.setTitle(title);
	}

}
