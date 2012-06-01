package net.wellfield.magpie;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.NotifyListener;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.event.EventListener;
import org.cybergarage.upnp.ssdp.SSDPPacket;

/**
 * Takes care of getting information about UPnP devices which respond to UPnP broadcasts
 * 
 * Devices which act only as players but do not implement the MediaRenderer service will be handled
 * by the Server class
 * 
 * @author martin.gutenbrunner@gmx.at
 *
 */
public class Controller extends Thread implements NotifyListener, EventListener, SearchResponseListener, DeviceChangeListener {
	private static Log log = LogFactory.getLog(Controller.class);
	
	private ControlPoint cp;
	
	@Override
	public void run() {
		super.run();
		
		cp = new ControlPoint();
		
		cp.addEventListener(this);
		cp.addNotifyListener(this);
		cp.addSearchResponseListener(this);
		cp.addDeviceChangeListener(this);

		cp.start();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		cp.stop();
		if (log.isDebugEnabled()) log.debug("Controller finalized");
	}
	
	public void deviceNotifyReceived(SSDPPacket ssdpPacket) {
//	    if (log.isDebugEnabled()) log.debug("deviceNotifyReceived: " + ssdpPacket.toString());
	}
	
	public void deviceSearchResponseReceived(SSDPPacket ssdpPacket) {
//		if (log.isDebugEnabled()) log.debug("deviceSearchResponseReceived: " + ssdpPacket.toString());
		
	}
	
	public void eventNotifyReceived(String uuid, long seq, String varName, String value) {
//		if (log.isDebugEnabled()) log.debug("eventNotifyReceived");
		
	}
	
	public void deviceAdded(Device dev) {
	    if (log.isDebugEnabled()) log.debug("Device added: " + dev.getFriendlyName());
//		if (log.isDebugEnabled()) log.debug("Device found: " + dev.getFriendlyName() + ", " + dev.getDeviceType() + ", " + dev.getUDN() + ": " + dev.getURLBase());

	    /*
	     * when a new device is recognized, several things have to be done:
	     * - save all automatically extracted information
	     * - check, if the information given by xml is correct (ie does search really work?)
	     * - try to save invokation information to database (ie what does CreateObject on Twonky require?)
	     * 
	     */
	    
	    String deviceType = dev.getDeviceType();

        if (deviceType.startsWith("urn:schemas-upnp-org:device:MediaRenderer")) {
            Start.getInstance().addDevice(dev);
        } else if (deviceType.startsWith("urn:schemas-upnp-org:device:MediaServer")) {
            Start.getInstance().addDevice(dev);
        }
	    /*
	    if (log.isDebugEnabled()) {
    		String deviceType = dev.getDeviceType();
    		if (deviceType.startsWith("urn:schemas-upnp-org:device:MediaRenderer")) {
    			log.debug("can see renderer " + dev.getFriendlyName());
    		} else if (deviceType.startsWith("urn:schemas-upnp-org:device:MediaServer")) {
    		    log.debug("can see server " + dev.getFriendlyName());
    		} else {
    		    /*
    		    if (log.isInfoEnabled()) log.info("Unknown device: '" + dev.getFriendlyName() + "' with type '" + deviceType + "'. Iterating services");
    		    UPnPUtil.iterateServices(dev.getServiceList());
    		    if (log.isInfoEnabled()) log.info("Iterating services done");
    		    *
    		}
	    }
        */
//		UPnPUtil.iterateServices(dev.getServiceList());
	}
	
	public void deviceRemoved(Device dev) {
		
	    Start.getInstance().removeDevice(dev.getUDN());
	    
	    if (log.isDebugEnabled()) log.debug("Device removed: " + dev.getFriendlyName());
		/*
	    if (dev.getDeviceType().startsWith("urn:schemas-upnp-org:device:MediaRenderer")) {

		    
		} else if (dev.getDeviceType().startsWith("urn:schemas-upnp-org:device:MediaServer")) {

		}
		*/
	}
	
	public void search() {
	    cp.search();
	}
}
