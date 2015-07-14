package com.webrob.distributedmutex.ui.cell;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Robert on 2014-12-12.
 */
public class IPAddressEditableCell<S> extends EditableCell<S, String>
{
    @Override protected String getFieldToCommit() throws UnknownHostException
    {
        String address = textField.getText();
        InetAddress.getByName(address);
        return address;
    }
}
