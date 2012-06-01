package net.wellfield.magpie.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.wellfield.magpie.upnp.beans.Player;

public class PlayerCellRenderer extends JLabel implements ListCellRenderer {
    
    /*
     * JList list, // the list Object value, // value to display int index, //
     * cell index boolean isSelected, // is the cell selected boolean
     * cellHasFocus // does the cell have focus
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        Player dev = (Player) value;
        setText(dev.getName());

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);

        return this;
    }
}
