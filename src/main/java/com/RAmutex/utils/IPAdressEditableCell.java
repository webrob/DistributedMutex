package com.RAmutex.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Robert on 2014-12-12.
 */
public class IPAdressEditableCell<S> extends EditableCell<S, String>
{
    @Override protected String getFieldToCommit() throws UnknownHostException
    {
        String address = textField.getText();
        InetAddress.getByName(address);
        return address;
    }
}
