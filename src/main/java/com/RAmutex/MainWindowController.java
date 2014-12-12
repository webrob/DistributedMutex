package com.RAmutex;

import com.RAmutex.model.Node;
import com.RAmutex.model.Timeout;
import com.RAmutex.utils.GlobalParameters;
import com.RAmutex.utils.NodesTableInitialization;
import com.RAmutex.utils.TimeoutsTableInitialization;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Robert
 */
public class MainWindowController implements Initializable
{
    @FXML private TableView<Timeout> timeoutsTableView;
    @FXML private TableColumn<Timeout, Integer> timeoutTableColumn;
    @FXML private TableColumn<Timeout, String> descriptionTimeoutTableColumn;

    @FXML private TableView<Node> nodesTableView;
    @FXML private TableColumn<Node, String> IPTableColumn;
    @FXML private TableColumn<Node, Integer> portTableColumn;

    @FXML private TextArea applicationStateTextArea;
    @FXML private TextArea receivedDataTextArea;
    @FXML private TextArea sentDataTextArea;

    @FXML private TextField myPortTextField;
    @FXML private TextField myIPTextField;


    @FXML private void connectButtonPressed(ActionEvent actionEvent)
    {

    }



    private void initMyIPandPortTextFields()
    {
	myIPTextField.setText(GlobalParameters.LOCALHOST);
	myPortTextField.setText(Integer.toString(GlobalParameters.DEAFULT_PORT));
    }

    @Override public void initialize(URL location, ResourceBundle resources)
    {
	NodesTableInitialization nodesTable = new NodesTableInitialization(nodesTableView, IPTableColumn, portTableColumn);
	nodesTable.initialize();

	TimeoutsTableInitialization timeoutsTable = new TimeoutsTableInitialization(timeoutsTableView, descriptionTimeoutTableColumn, timeoutTableColumn);
	timeoutsTable.initialize();

	initMyIPandPortTextFields();
    }

    @FXML private void addNodeButtonPressed(ActionEvent actionEvent)
    {
	ObservableList<Node> items = nodesTableView.getItems();
	items.add(new Node());
    }

    @FXML private void removeNodeButtonPressed(ActionEvent actionEvent)
    {
	ObservableList<Node> items = nodesTableView.getItems();
	Node selectedItem = nodesTableView.getSelectionModel().getSelectedItem();

	int itemsAmount = items.size();
	if (itemsAmount > 1)
	{
	    items.remove(selectedItem);
	}
    }
}
