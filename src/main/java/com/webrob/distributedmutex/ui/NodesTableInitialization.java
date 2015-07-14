package com.webrob.distributedmutex.ui;

import com.webrob.distributedmutex.model.Node;
import com.webrob.distributedmutex.ui.cell.IDAddressEditableCell;
import com.webrob.distributedmutex.ui.cell.IPAddressEditableCell;
import com.webrob.distributedmutex.ui.cell.IntegerEditableCell;
import com.webrob.distributedmutex.utils.GlobalParameters;
import com.webrob.distributedmutex.utils.InitializeHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 * Created by Robert on 2014-12-12.
 */
public class NodesTableInitialization
{
    private TableView<Node> nodesTableView;
    private TableColumn<Node, String> IPTableColumn;
    private TableColumn<Node, Integer> portTableColumn;
    private TableColumn<Node, String> idTableColumn;

    public NodesTableInitialization(TableView<Node> nodesTableView, TableColumn<Node, String> IPTableColumn, TableColumn<Node, Integer> portTableColumn, TableColumn<Node, String> idTableColumn)
    {
	this.nodesTableView = nodesTableView;
	this.IPTableColumn = IPTableColumn;
	this.portTableColumn = portTableColumn;
	this.idTableColumn = idTableColumn;
    }

    public void initialize()
    {
	IPTableColumn.setCellValueFactory(new PropertyValueFactory<>("IP"));
	portTableColumn.setCellValueFactory(new PropertyValueFactory<>("port"));
	idTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));


	putFirstNodeToNodesTable();
	makeNodesTableColumnsEditable();
    }

    // ugly way to initialize
    private void putFirstNodeToNodesTable()
    {
	ObservableList<Node> nodes = FXCollections.observableArrayList();

	int value = InitializeHelper.getValue();
	Node node;
	if (value == 0)
	{
	    node = new Node();
	    nodes.add(node);

	    node = new Node();
	    nodes.add(node);

	    node = new Node();
	    nodes.add(node);

	    InitializeHelper.incrementValue();
	}
	else if (value == 1)
	{
	    node = new Node("127.0.0.1", GlobalParameters.DEFAULT_PORT , "1");
	    nodes.add(node);
	    node = new Node("127.0.0.1", GlobalParameters.DEFAULT_PORT +2, "3");
	    nodes.add(node);
	    node = new Node("127.0.0.1", GlobalParameters.DEFAULT_PORT +3, "4");
	    nodes.add(node);
	    InitializeHelper.incrementValue();
	}
	else if (value == 2)
	{
	    node = new Node("127.0.0.1", GlobalParameters.DEFAULT_PORT , "1");
	    nodes.add(node);

	    node = new Node("127.0.0.1", GlobalParameters.DEFAULT_PORT + 1, "2");
	    nodes.add(node);
	    node = new Node("127.0.0.1", GlobalParameters.DEFAULT_PORT + 3, "4");
	    nodes.add(node);
	    InitializeHelper.incrementValue();
	}
	else if (value == 3)
	{
	    node = new Node("127.0.0.1", GlobalParameters.DEFAULT_PORT , "1");
	    nodes.add(node);

	    node = new Node("127.0.0.1", GlobalParameters.DEFAULT_PORT + 1, "2");
	    nodes.add(node);
	    node = new Node("127.0.0.1", GlobalParameters.DEFAULT_PORT + 2, "3");
	    nodes.add(node);
	    InitializeHelper.resetValue();
	}
	nodesTableView.setItems(nodes);
    }

    private void makeNodesTableColumnsEditable()
    {
	Callback<TableColumn<Node, Integer>, TableCell<Node, Integer>> portCellFactory = p -> new IntegerEditableCell();
	Callback<TableColumn<Node, String>, TableCell<Node, String>> IPCellFactory = p -> new IPAddressEditableCell();
	Callback<TableColumn<Node, String>, TableCell<Node, String>> idCellFactory = p -> new IDAddressEditableCell<>();


	portTableColumn.setCellFactory(portCellFactory);
	IPTableColumn.setCellFactory(IPCellFactory);
	idTableColumn.setCellFactory(idCellFactory);

	portTableColumn.setOnEditCommit(
			t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).setPort(t.getNewValue()));
	IPTableColumn.setOnEditCommit(
			t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).setIP(t.getNewValue()));
	idTableColumn.setOnEditCommit(
			t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).setId(t.getNewValue()));

    }
}
