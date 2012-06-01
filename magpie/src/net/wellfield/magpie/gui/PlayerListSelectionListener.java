package net.wellfield.magpie.gui;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.wellfield.magpie.Start;

public class PlayerListSelectionListener implements ListSelectionListener {

    public void valueChanged(ListSelectionEvent event) {
        JList list = (JList)event.getSource();
        Start start = Start.getInstance();
        
        if (list.getSelectedIndex() >= 0) {
            if (!event.getValueIsAdjusting()) {
                start.playerSelected();
            }
        }
    }

}
