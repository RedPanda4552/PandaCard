package io.github.redpanda4552.PandaCard.JavaFX;

import javafx.scene.text.Text;

/**
 * Convenience class. Literally just the JavaFX Text object,
 * but we force the font color to white, since we need white with our dark theme,
 * and these Text objects refuse to acknowledge the CSS that literally everything else does.
 */
public class StyledText extends Text {

    public StyledText(String text) {
        setText(text);
        setStyle("-fx-fill: white;");
    }
}
