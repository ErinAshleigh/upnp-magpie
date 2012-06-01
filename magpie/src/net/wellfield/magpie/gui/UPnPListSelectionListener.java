package net.wellfield.magpie.gui;

import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.wellfield.magpie.Start;
import net.wellfield.magpie.UPnPAnalyzer;

import org.cybergarage.upnp.Device;
import org.jdesktop.swingworker.SwingWorker;

public class UPnPListSelectionListener implements ListSelectionListener {

    public void valueChanged(ListSelectionEvent event) {
        final JList list = (JList)event.getSource();
        Start start = Start.getInstance();
        
        if (list.getSelectedIndex() >= 0) {
            if (!event.getValueIsAdjusting()) {
                
                new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        Start start = Start.getInstance();
                        start.deviceSelected();
                        Device dev = (Device)list.getSelectedValue();
                        UPnPAnalyzer ana = UPnPAnalyzer.getInstance(dev.getUDN());
                        
                        ana.analyzeDevice(dev);
                        
                        start.enableBtnSubmitUPnP();

                        
                        
                        return null;
                    }
                }.execute();
            }
        } else {
            start.deviceUnselected();
        }
    }
}
