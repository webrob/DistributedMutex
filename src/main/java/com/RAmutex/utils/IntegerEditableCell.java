package com.RAmutex.utils;

/**
 * Created by Robert on 2014-12-12.
 */
public class IntegerEditableCell extends EditableCell<Integer>
{
    @Override protected Integer getFieldToCommit()
    {
	return Integer.parseInt(textField.getText());
    }
}
