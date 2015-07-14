package com.webrob.distributedmutex;

import com.webrob.distributedmutex.model.Node;
import com.webrob.distributedmutex.model.Timeout;
import com.webrob.distributedmutex.network.AllConnectionsManager;
import com.webrob.distributedmutex.network.AllConnectionsManagerEmpty;
import com.webrob.distributedmutex.network.AllConnectionsManagerImpl;
import com.webrob.distributedmutex.ui.NodesTableInitialization;
import com.webrob.distributedmutex.ui.TextAreaControllerSingleton;
import com.webrob.distributedmutex.ui.TimeoutsTableInitialization;
import com.webrob.distributedmutex.utils.ButtonDelay;
import com.webrob.distributedmutex.utils.ButtonDelayListener;
import com.webrob.distributedmutex.utils.GlobalParameters;
import com.webrob.distributedmutex.utils.InitializeHelper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Robert
 */
public class MainWindowController implements Initializable, ButtonDelayListener
{
    @FXML private TableView<Timeout> timeoutsTableView;
    @FXML private TableColumn<Timeout, Integer> timeoutTableColumn;
    @FXML private TableColumn<Timeout, String> descriptionTimeoutTableColumn;

    @FXML private TableView<Node> nodesTableView;
    @FXML private TableColumn<Node, String> IPTableColumn;
    @FXML private TableColumn<Node, Integer> portTableColumn;
    @FXML private TableColumn<Node, String> idTableColumn;

    @FXML private TextArea applicationStateTextArea;
    @FXML private TextArea receivedDataTextArea;
    @FXML private TextArea sentDataTextArea;

    @FXML private TextField myPortTextField;
    @FXML private TextField myIDTextField;
    @FXML private Label myIPLabel;
    private AllConnectionsManager manager = new AllConnectionsManagerEmpty();
    private ButtonDelay delay = new ButtonDelay(this);


    @FXML
    private void connectButtonPressed()
    {
	List<Node> nodes = nodesTableView.getItems();
	Node myNode = getMyNode();

	manager = new AllConnectionsManagerImpl(nodes, myNode);
	manager.startConnections();
	delay.startTimeMeasure();
    }

    private Node getMyNode()
    {
	String myIP = myIPLabel.getText();
	Integer myPort = Integer.parseInt(myPortTextField.getText());
	String myID = myIDTextField.getText();
	return new Node(myIP, myPort, myID);
    }

    @Override public void initialize(URL location, ResourceBundle resources)
    {
	initMyIPandPortTextFields();
	initTextAreaControllerSingleton();

	NodesTableInitialization nodesTable = new NodesTableInitialization(nodesTableView, IPTableColumn,
			portTableColumn, idTableColumn);
	nodesTable.initialize();

	TimeoutsTableInitialization timeoutsTable = new TimeoutsTableInitialization(timeoutsTableView,
			descriptionTimeoutTableColumn, timeoutTableColumn);
	timeoutsTable.initialize();

	try
	{
	    myIPLabel.setText(InetAddress.getLocalHost().getHostAddress());
	}
	catch (UnknownHostException ignored)
	{
	}
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

	int value = InitializeHelper.getValue();

	switch (value)
	{
	    case 0:
	    {
		myPortTextField.setText(Integer.toString(GlobalParameters.DEFAULT_PORT));
		myIDTextField.setText(GlobalParameters.DEFAULT_ID);
		break;
	    }
	    case 1:
	    {
		myPortTextField.setText(Integer.toString(GlobalParameters.DEFAULT_PORT + 1));
		myIDTextField.setText("2");
		break;
	    }
	    case 2:
	    {
		myPortTextField.setText(Integer.toString(GlobalParameters.DEFAULT_PORT + 2));
		myIDTextField.setText("3");
		break;
	    }
	    case 3:
	    {
		myPortTextField.setText(Integer.toString(GlobalParameters.DEFAULT_PORT + 3));
		myIDTextField.setText("4");
		break;
	    }
	}
    }

    @FXML
    private void addNodeButtonPressed()
    {
	ObservableList<Node> items = nodesTableView.getItems();
	items.add(new Node());
    }

    @FXML
    private void removeNodeButtonPressed()
    {
	ObservableList<Node> items = nodesTableView.getItems();
	Node selectedItem = nodesTableView.getSelectionModel().getSelectedItem();

	int itemsAmount = items.size();
	if (itemsAmount > 1)
	{
	    items.remove(selectedItem);
	}
    }

    @FXML
    private void enterButtonPressed()
    {
	if (delay.canPress())
	{
	    manager.wantEnterToSection();
	}
    }

    public void setStage(Stage stage)
    {
	stage.setOnCloseRequest(event ->
	{
	    InitializeHelper.resetValue();
	    //manager.closeAllSockets()
	})
	;
    }

    public void leaveButtonPressed()
    {
	manager.leaveSection();
    }

    @Override public void delayTimeout()
    {
	manager.wantEnterToSection();
    }
}
