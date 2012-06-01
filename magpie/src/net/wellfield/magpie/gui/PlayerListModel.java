package net.wellfield.magpie.gui;

import java.util.Enumeration;

import javax.swing.DefaultListModel;

import net.wellfield.magpie.upnp.beans.Player;

public class PlayerListModel extends DefaultListModel {

    @Override
    public void addElement(Object obj) {
        if (obj instanceof Player) {
            super.addElement(obj);
        }
    }
    
    public void removeElement(String key) {
        for(Enumeration en=super.elements();en.hasMoreElements();) {
            Player player = (Player)en.nextElement();
            if (player.getAddress().equals(key)) {
                super.removeElement(player);
                break;
            }
        }
    }
    
    public boolean containsElement(String key) {
        for(Enumeration en=super.elements();en.hasMoreElements();) {
            Player player = (Player)en.nextElement();
            if (player.getAddress().equals(key)) {
                return true;
            }
        }
        return false;
    }
    
    public Player getElement(String key) {
        for(Enumeration en=super.elements();en.hasMoreElements();) {
            Player player = (Player)en.nextElement();
            if (player.getAddress().equals(key)) {
                return player;
            }
        }
        return null;        
    }
}
