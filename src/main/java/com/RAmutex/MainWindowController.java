package com.RAmutex;

import com.RAmutex.model.Node;
import com.RAmutex.model.Timeout;
import com.RAmutex.network.AllConnectionsManager;
import com.RAmutex.utils.GlobalParameters;
import com.RAmutex.utils.NodesTableInitialization;
import com.RAmutex.utils.TextAreaControllerSingleton;
import com.RAmutex.utils.TimeoutsTableInitialization;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
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
    @FXML private Label myIPLabel;
    private AllConnectionsManager manager;

    @FXML private void connectButtonPressed(ActionEvent actionEvent)
    {
	List<Node> nodes = nodesTableView.getItems();
	Node myNode = getMyNode();

	manager = new AllConnectionsManager(nodes, myNode);
	manager.startConnections();

        try
        {
            System.out.println(InetAddress.getLocalHost().getHostAddress());
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }

    private Node getMyNode()
    {
	String myIP = myIPLabel.getText();
	Integer myPort = Integer.parseInt(myPortTextField.getText());
	return new Node(myIP, myPort);
    }

    @Override public void initialize(URL location, ResourceBundle resources)
    {
        initTextAreaControllerSingleton();

	NodesTableInitialization nodesTable = new NodesTableInitialization(nodesTableView, IPTableColumn, portTableColumn);
	nodesTable.initialize();

	TimeoutsTableInitialization timeoutsTable = new TimeoutsTableInitialization(timeoutsTableView, descriptionTimeoutTableColumn, timeoutTableColumn);
	timeoutsTable.initialize();

	initMyIPandPortTextFields();

    }

    private void initTextAreaControllerSingleton()
    {
        TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();
        singleton.setApplicationStateTextArea(applicationStateTextArea);
        singleton.setReceivedDataTextArea(receivedDataTextArea);
        singleton.setSentDataTextArea(sentDataTextArea);
    }

    private void initMyIPandPortTextFields()
    {
        myIPLabel.setText(GlobalParameters.LOCALHOST);
	myPortTextField.setText(Integer.toString(GlobalParameters.DEAFULT_PORT));
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

    public void enterButtonPressed(ActionEvent actionEvent)
    {
        manager.sendBroadcastEnterMessage();
    }

    public void leaveButtonPressed(ActionEvent actionEvent)
    {

    }
}
