package com.RAmutex.utils;

/**
 * Created by Robert on 2014-12-12.
 */
public class IntegerEditableCell<S> extends EditableCell<S, Integer>
{
    @Override protected Integer getFieldToCommit()
    {
	return Integer.parseInt(textField.getText());
    }
}
