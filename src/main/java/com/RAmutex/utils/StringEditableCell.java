package com.RAmutex.utils;

/**
 * Created by Robert on 2014-12-12.
 */
public class StringEditableCell<S> extends EditableCell<S, String>
{
    @Override protected String getFieldToCommit()
    {
	return textField.getText();
    }
}
