package com.ViktorVano.SpeechRecognitionAI.GUI;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class ColoredListCell<T> extends javafx.scene.control.ListCell<T> {
    private boolean odd;
    private Color oddColor;
    private Color evenColor;

    public ColoredListCell(Color color)
    {
        this.oddColor = color;
        double red = color.getRed()*2.0;
        double green = color.getGreen()*2.0;
        double blue = color.getBlue()*2.0;
        if(red > 1.0)
            red = 1.0;

        if(green > 1.0)
            green = 1.0;

        if(blue > 1.0)
            blue = 1.0;
        this.evenColor = new Color(red, green, blue, 1.0);
    }
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            setBackground(null);
        } else {
            setText(item.toString());
            odd = getIndex() % 2 == 0;
            updateBackground();
        }
    }

    private void updateBackground() {
        if (odd) {
            setBackground(new Background(new BackgroundFill(oddColor, null, null)));
        } else {
            setBackground(new Background(new BackgroundFill(evenColor, null, null)));
        }
    }
}
