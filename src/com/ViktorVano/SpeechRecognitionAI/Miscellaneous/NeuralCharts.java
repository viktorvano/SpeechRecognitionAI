package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public class NeuralCharts {
    public NeuralCharts(Stage stageReference, ArrayList<XYChart.Series<Number, Number>> charts, ArrayList<Classifier> classifier, String classifierName, String classifierMatch)
    {
        final int dialogWidth = 1000;
        final int dialogHeight = 800;
        final Stage dialog = new Stage();
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        if(classifierName.length() == 0)
            dialog.setTitle("Neural Charts: [artifact] " + classifierMatch);
        else
            dialog.setTitle("Neural Charts: " + classifierName + " " + classifierMatch);
        dialog.initModality(Modality.NONE);
        dialog.initOwner(stageReference);
        VBox vBox = new VBox();
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
        for(int i=0; i<charts.size()-1; i++)
        {
            xAxis = new NumberAxis();
            yAxis = new NumberAxis();
            yAxis.setUpperBound(1.0);
            yAxis.setLowerBound(-1.0);
            yAxis.setAutoRanging(false);
            lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setPrefWidth(dialogScene.getWidth()-15);
            if(i==0)
                lineChart.setTitle("Input Layer");
            else
                lineChart.setTitle("Hidden Layer " + (i+1));
            lineChart.setLegendVisible(false);
            lineChart.setAnimated(false);
            lineChart.getData().add(charts.get(i));
            vBox.getChildren().add(lineChart);
        }

        LineChart<String,Number> lineChartOutput;
        XYChart.Series series = new XYChart.Series();
        CategoryAxis xAxisOutputLayer = new CategoryAxis();
        for(int i=0; i<charts.get(charts.size()-1).getData().size(); i++)
        {
            if(classifier.get(i).getName().equals(""))
                series.getData().add(new XYChart.Data("[artifact]",
                        charts.get(charts.size()-1).getData().get(i).getYValue()));
            else
                series.getData().add(new XYChart.Data(classifier.get(i).getName(),
                        charts.get(charts.size()-1).getData().get(i).getYValue()));
        }
        yAxis = new NumberAxis();
        yAxis.setUpperBound(1.0);
        yAxis.setLowerBound(-1.0);
        yAxis.setAutoRanging(false);
        lineChartOutput = new LineChart<>(xAxisOutputLayer, yAxis);
        lineChartOutput.setPrefWidth(dialogScene.getWidth()-15);
        lineChartOutput.setTitle("Output Layer");
        lineChartOutput.setLegendVisible(false);
        lineChartOutput.setAnimated(false);
        lineChartOutput.getData().add(series);
        vBox.getChildren().add(lineChartOutput);
    }
}
