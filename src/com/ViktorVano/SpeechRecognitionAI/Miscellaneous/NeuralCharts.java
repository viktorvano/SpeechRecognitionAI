package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public class NeuralCharts {
    public NeuralCharts(Stage stageReference, ArrayList<XYChart.Series<Number, Number>> charts, ArrayList<Classifier> classifier, String classifierName)
    {
        final int dialogWidth = 900;
        final int dialogHeight = 600;
        final Stage dialog = new Stage();
        if(classifierName.length() == 0)
            dialog.setTitle("Neural Charts");
        else
            dialog.setTitle("Neural Charts: " + classifierName);
        dialog.initModality(Modality.NONE);
        dialog.initOwner(stageReference);
        VBox dialogPane = new VBox();
        Scene dialogScene = new Scene(dialogPane, dialogWidth, dialogHeight);
        dialog.setMinWidth(dialogPane.getWidth());
        dialog.setMinHeight(dialogPane.getHeight());
        dialog.setResizable(false);
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
        for(int i=0; i<charts.size(); i++)
        {
            xAxis = new NumberAxis();
            yAxis = new NumberAxis();
            lineChart = new LineChart<>(xAxis, yAxis);
            dialogPane.getChildren().add(lineChart);
        }
    }
}
