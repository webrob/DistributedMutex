package com.RAmutex.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Robert on 2014-12-12.
 */
public class Timeout
{
    private IntegerProperty valueInMilSec;
    private StringProperty description;

    public Timeout (Integer valueInMilSec, String description)
    {
	this.valueInMilSec = new SimpleIntegerProperty(valueInMilSec);
	this.description = new SimpleStringProperty(description);
    }

    public String getDescription()
    {
	return description.get();
    }

    public void setDescription(String description)
    {
	this.description.set(description);
    }

    public Integer getValueInMilSec()
    {
	return valueInMilSec.get();
    }

    public void setValueInMilSec(Integer valueInMilSec)
    {
	this.valueInMilSec.set(valueInMilSec);
    }
}
