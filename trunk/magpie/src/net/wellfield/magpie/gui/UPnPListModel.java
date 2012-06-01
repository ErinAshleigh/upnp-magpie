package net.wellfield.magpie.gui;

import java.util.Enumeration;

import javax.swing.DefaultListModel;

import org.cybergarage.upnp.Device;

public class UPnPListModel extends DefaultListModel {
    
    @Override
    public void addElement(Object obj) {
        if (obj instanceof Device) {
            super.addElement(obj);
        }
    }

    public void removeElement(String udn) {
        for(Enumeration<Device> en = (Enumeration<Device>) super.elements();en.hasMoreElements();) {
            Device dev = en.nextElement();
            if (dev.getUDN().equals(udn)) {
                super.removeElement(dev);
                break;
            }
        }
    }
}
