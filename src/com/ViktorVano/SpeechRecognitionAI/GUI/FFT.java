package com.ViktorVano.SpeechRecognitionAI.GUI;

import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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
        final ScrollPane scrollPane = new ScrollPane();

        Color background = new Color(
                ((double)background_red)/255.0,
                ((double)background_green)/255.0,
                ((double)background_blue)/255.0,
                1.0);
        scrollPane.setBackground(new Background(new BackgroundFill(background, null, null)));

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        if(classifierName.length() == 0)
            dialog.setTitle("Neural Charts: [artifact] " + classifierMatch);
        else
            dialog.setTitle("Neural Charts: " + classifierName + " " + classifierMatch);
        dialog.initModality(Modality.NONE);
        dialog.initOwner(stageReference);
        VBox vBox = new VBox();
        vBox.setBackground(new Background(new BackgroundFill(background, null, null)));
        Scene dialogScene = new Scene(scrollPane, dialogWidth, dialogHeight);
        dialog.setMinWidth(scrollPane.getWidth());
        dialog.setMinHeight(scrollPane.getHeight());
        dialog.setResizable(false);
        dialog.setScene(dialogScene);
        dialog.show();
        scrollPane.setContent(vBox);
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
        yAxis = new NumberAxis();
        yAxis.setUpperBound(1.0);
        yAxis.setLowerBound(-1.0);
        yAxis.setAutoRanging(false);
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        lineChart.setPrefWidth(dialogScene.getWidth()-25);

        lineChart.setTitle("FFT");
        lineChart.setCreateSymbols(false);

        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        lineChart.getData().add(fftSeries);
        vBox.getChildren().add(lineChart);

    }
}
