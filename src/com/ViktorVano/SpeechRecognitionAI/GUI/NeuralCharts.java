package com.ViktorVano.SpeechRecognitionAI.GUI;

import com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Classifier;
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

public class NeuralCharts {
    public NeuralCharts(Stage stageReference, ArrayList<XYChart.Series<Number, Number>> neuralSeries, ArrayList<Classifier> classifier, String classifierName, String classifierMatch)
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
        for(int i=0; i<neuralSeries.size()-1; i++)
        {
            xAxis = new NumberAxis();
            yAxis = new NumberAxis();
            yAxis.setUpperBound(1.0);
            yAxis.setLowerBound(-1.0);
            yAxis.setAutoRanging(false);
            lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
            lineChart.setPrefWidth(dialogScene.getWidth()-25);
            if(i==0)
            {
                lineChart.setTitle("Input Layer");
                lineChart.setCreateSymbols(false);
            }
            else
            {
                lineChart.setTitle("Hidden Layer " + i);
                lineChart.setCreateSymbols(true);
            }
            lineChart.setLegendVisible(false);
            lineChart.setAnimated(false);
            lineChart.getData().add(neuralSeries.get(i));
            vBox.getChildren().add(lineChart);
        }

        BarChart<String,Number> barChartOutput;
        XYChart.Series outputSeries = new XYChart.Series();
        CategoryAxis xAxisOutputLayer = new CategoryAxis();
        for(int i=0; i<neuralSeries.get(neuralSeries.size()-1).getData().size(); i++)
        {
            if(classifier.get(i).getName().equals(""))
                outputSeries.getData().add(new XYChart.Data("[artifact]",
                        neuralSeries.get(neuralSeries.size()-1).getData().get(i).getYValue()));
            else
                outputSeries.getData().add(new XYChart.Data(classifier.get(i).getName(),
                        neuralSeries.get(neuralSeries.size()-1).getData().get(i).getYValue()));
        }
        yAxis = new NumberAxis();
        yAxis.setUpperBound(1.0);
        yAxis.setLowerBound(-1.0);
        yAxis.setAutoRanging(false);
        barChartOutput = new BarChart<>(xAxisOutputLayer, yAxis);
        barChartOutput.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        barChartOutput.setPrefWidth(dialogScene.getWidth()-25);
        barChartOutput.setTitle("Output Layer");
        barChartOutput.setLegendVisible(false);
        barChartOutput.setAnimated(false);
        barChartOutput.getData().add(outputSeries);
        vBox.getChildren().add(barChartOutput);
    }
}
