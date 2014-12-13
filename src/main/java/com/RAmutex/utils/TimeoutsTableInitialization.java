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
	nodes.add(new Timeout(GlobalParameters.maxSectionOccupationTime, GlobalParameters.MAX_SECTION_OCCUPATION_TIME_DESCRIPTION));
	nodes.add(new Timeout(GlobalParameters.reconnectionPeriod, GlobalParameters.RECONNECTION_PERIOD_DESCRIPTION));

	timeoutsTableView.setItems(nodes);
    }

    private void makeTimeoutColumnEditable()
    {
	Callback<TableColumn<Timeout, Integer>, TableCell<Timeout, Integer>> timeoutCellFactory = p -> new IntegerEditableCell();
	timeoutTableColumn.setCellFactory(timeoutCellFactory);

	timeoutTableColumn.setOnEditCommit(
			t -> {
			    int index = t.getTablePosition().getRow();
			    Timeout timeout = t.getTableView().getItems().get(index);
			    Integer newValue = t.getNewValue();
			    timeout.setValueInMilSec(newValue);

			    setNewGlobalTimeouts(timeout.getDescription(), newValue);
			});
    }

    private void setNewGlobalTimeouts(String timeoutDescription, Integer newValue)
    {
	if (timeoutDescription.equals(GlobalParameters.MAX_SECTION_OCCUPATION_TIME_DESCRIPTION))
	{
	    GlobalParameters.maxSectionOccupationTime = newValue;
	}
	else if (timeoutDescription.equals(GlobalParameters.RECONNECTION_PERIOD_DESCRIPTION))
	{
	    GlobalParameters.reconnectionPeriod = newValue;
	}
    }
}
