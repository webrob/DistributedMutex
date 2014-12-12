package com.RAmutex;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author Robert
 */
public class MainWindowController  {
    
    @FXML
    private Label label;

    @FXML
    private Button button;
    

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }

}
