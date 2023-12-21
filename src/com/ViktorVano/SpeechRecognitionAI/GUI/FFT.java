package com.ViktorVano.SpeechRecognitionAI.GUI;

import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;

public class FFT {
    public FFT(Stage stageReference, XYChart.Series<Number, Number> fftSeries, String classifierName, String classifierMatch)
    {
        final int dialogWidth = 1000;
        final int dialogHeight = 800;
        final Stage dialog = new Stage();
        final StackPane stackPane = new StackPane();

        Color background = new Color(
                ((double)background_red)/255.0,
                ((double)background_green)/255.0,
                ((double)background_blue)/255.0,
                1.0);
        stackPane.setBackground(new Background(new BackgroundFill(background, null, null)));

        if(classifierName.length() == 0)
            dialog.setTitle("FFT Chart: [artifact] " + classifierMatch);
        else
            dialog.setTitle("FFT Chart: " + classifierName + " " + classifierMatch);
        dialog.initModality(Modality.NONE);
        dialog.initOwner(stageReference);

        stackPane.setBackground(new Background(new BackgroundFill(background, null, null)));
        Scene dialogScene = new Scene(stackPane, dialogWidth, dialogHeight);
        dialog.setMinWidth(stackPane.getWidth());
        dialog.setMinHeight(stackPane.getHeight());
        dialog.setResizable(true);
        dialog.setScene(dialogScene);
        dialog.show();
        try
        {
            Image icon = new Image(getClass().getResourceAsStream("../images/icon3.png"));
            dialog.getIcons().add(icon);
            System.out.println("Icon loaded from IDE...");
        }catch(Exception e)
        {
            try
            {
                Image icon = new Image("com/ViktorVano/SpeechRecognitionAI/images/icon3.png");
                dialog.getIcons().add(icon);
                System.out.println("Icon loaded from exported JAR...");
            }catch(Exception e1)
            {
                System.out.println("Icon failed to load...");
            }
        }

        LineChart<Number,Number> lineChart;
        NumberAxis xAxis;
        NumberAxis yAxis;

        xAxis = new NumberAxis();
        xAxis.setLabel("Hz");
        xAxis.setLowerBound(0.0);
        xAxis.setUpperBound(11000.0);
        yAxis = new NumberAxis();
        yAxis.setLabel("Relative Amplitude");
        yAxis.setLowerBound(0.0);
        yAxis.setUpperBound(1.0);
        yAxis.setAutoRanging(false);
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        lineChart.setPrefWidth(dialogScene.getWidth()-25);

        lineChart.setTitle("FFT");
        lineChart.setCreateSymbols(false);

        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        lineChart.getData().add(fftSeries);
        stackPane.getChildren().add(lineChart);

    }
}
