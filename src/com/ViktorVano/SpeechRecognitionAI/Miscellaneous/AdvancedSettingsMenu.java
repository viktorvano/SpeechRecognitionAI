package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AdvancedSettingsMenu {
    public AdvancedSettingsMenu(Stage stageReference)
    {
        final int dialogWidth = 600;
        final int dialogHeight = 300;
        final Stage dialog = new Stage();
        dialog.setTitle("Word Detection Settings");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stageReference);
        Pane dialogPane = new Pane();
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
    }
}
