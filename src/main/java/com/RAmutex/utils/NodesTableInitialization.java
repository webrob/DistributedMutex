package com.RAmutex.utils;

import com.RAmutex.model.Node;
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

    public NodesTableInitialization(TableView<Node> nodesTableView, TableColumn<Node, String> IPTableColumn, TableColumn<Node, Integer> portTableColumn)
    {
	this.nodesTableView = nodesTableView;
	this.IPTableColumn = IPTableColumn;
	this.portTableColumn = portTableColumn;
    }

    public void initialize()
    {
	IPTableColumn.setCellValueFactory(new PropertyValueFactory<>("IP"));
	portTableColumn.setCellValueFactory(new PropertyValueFactory<>("port"));

	putFirstNodeToNodesTable();
	makeNodesTableColumnsEditable();
    }

    private void putFirstNodeToNodesTable()
    {
	ObservableList<Node> nodes = FXCollections.observableArrayList();
	nodes.add(new Node());
	nodesTableView.setItems(nodes);
    }

    private void makeNodesTableColumnsEditable()
    {
	Callback<TableColumn<Node, Integer>, TableCell<Node, Integer>> portCellFactory = p -> new IntegerEditableCell();
	Callback<TableColumn<Node, String>, TableCell<Node, String>> IPCellFactory = p -> new StringEditableCell();

	portTableColumn.setCellFactory(portCellFactory);
	IPTableColumn.setCellFactory(IPCellFactory);

	portTableColumn.setOnEditCommit(
			t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).setPort(t.getNewValue()));
	IPTableColumn.setOnEditCommit(
			t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).setIP(t.getNewValue()));
    }
}
