package com.webrob.distributedmutex.ui.cell;

/**
 * Created by Robert on 2015-01-04.
 */
public class IDAddressEditableCell<S> extends EditableCell<S, String>
{
    @Override
    protected String getFieldToCommit()
    {
	return textField.getText();
    }
}
