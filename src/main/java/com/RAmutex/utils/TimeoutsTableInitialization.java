package com.RAmutex.utils;

import com.RAmutex.model.Timeout;
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
public class TimeoutsTableInitialization
{
    private TableView<Timeout> timeoutsTableView;
    private TableColumn<Timeout, String> descriptionTimeoutTableColumn;
    private TableColumn<Timeout, Integer> timeoutTableColumn;

    public TimeoutsTableInitialization(TableView<Timeout> timeoutsTableView, TableColumn<Timeout, String> descriptionTimeoutTableColumn, TableColumn<Timeout, Integer> timeoutTableColumn)
    {
	this.timeoutsTableView = timeoutsTableView;
	this.descriptionTimeoutTableColumn = descriptionTimeoutTableColumn;
	this.timeoutTableColumn = timeoutTableColumn;
    }

    public void initialize()
    {
	descriptionTimeoutTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
	timeoutTableColumn.setCellValueFactory(new PropertyValueFactory<>("valueInMilSec"));

	putFirstNodeToNodesTable();
	makeTimeoutColumnEditable();
    }

    private void putFirstNodeToNodesTable()
    {
	ObservableList<Timeout> nodes = FXCollections.observableArrayList();
	nodes.add(new Timeout(10000, "time of section occupation"));
	timeoutsTableView.setItems(nodes);
    }

    private void makeTimeoutColumnEditable()
    {
	Callback<TableColumn<Timeout, Integer>, TableCell<Timeout, Integer>> timeoutCellFactory = p -> new IntegerEditableCell();
	timeoutTableColumn.setCellFactory(timeoutCellFactory);

	timeoutTableColumn.setOnEditCommit(
			t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).setValueInMilSec(
					t.getNewValue()));
    }
}
