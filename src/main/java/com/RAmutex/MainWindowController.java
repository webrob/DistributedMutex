package com.RAmutex;

import com.RAmutex.model.Node;
import com.RAmutex.utils.GlobalParameters;
import com.RAmutex.utils.IntegerEditableCell;
import com.RAmutex.utils.StringEditableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Robert
 */
public class MainWindowController implements Initializable
{

    @FXML private TableColumn<Node, String> IPTableColumn;
    @FXML private TableColumn<Node, Integer> portTableColumn;
    @FXML private TableView<Node> nodesTable;
    @FXML private TextArea receivedDataTextArea;
    @FXML private TextArea sentDataTextArea;
    @FXML private TextField myPortTextField;
    @FXML private TextField myIPTextField;

    @FXML private void connectButtonPressed(ActionEvent actionEvent)
    {

    }

    private void initNodesTableFirstNode()
    {
	ObservableList<Node> nodes = FXCollections.observableArrayList();
	nodes.add(new Node());
	nodesTable.setItems(nodes);
    }

    private void makeNodesTableColumnsEditable()
    {
	Callback<TableColumn<Node, Integer>, TableCell<Node, Integer>> portCellFactory = p -> new IntegerEditableCell();
	Callback<TableColumn<Node, String>, TableCell<Node, String>> IPCellFactory = p -> new StringEditableCell();

	IPTableColumn.setCellValueFactory(new PropertyValueFactory<>("IP"));
	portTableColumn.setCellValueFactory(new PropertyValueFactory<>("port"));

	portTableColumn.setCellFactory(portCellFactory);
	IPTableColumn.setCellFactory(IPCellFactory);

	portTableColumn.setOnEditCommit(
			t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).setPort(t.getNewValue()));
	IPTableColumn.setOnEditCommit(
			t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).setIP(t.getNewValue()));
    }

    private void initMyIPandPortTextFields()
    {
	myIPTextField.setText(GlobalParameters.LOCALHOST);
	myPortTextField.setText(Integer.toString(GlobalParameters.DEAFULT_PORT));
    }

    @Override public void initialize(URL location, ResourceBundle resources)
    {
	initNodesTableFirstNode();
	makeNodesTableColumnsEditable();
	initMyIPandPortTextFields();





    }

    @FXML private void addNodeButtonPressed(ActionEvent actionEvent)
    {
	ObservableList<Node> items = nodesTable.getItems();
	items.add(new Node());
    }

    @FXML private void removeNodeButtonPressed(ActionEvent actionEvent)
    {
	ObservableList<Node> items = nodesTable.getItems();

	int itemsAmount = items.size();
	if (itemsAmount > 1)
	{
	    int lastItemIndex = itemsAmount - 1;
	    items.remove(lastItemIndex);
	}
    }
}
