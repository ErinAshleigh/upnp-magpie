package net.wellfield.magpie;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.wellfield.magpie.upnp.beans.ActionResult;
import net.wellfield.magpie.upnp.beans.PositionInfo;
import net.wellfield.magpie.upnp.beans.TransportInfo;
import net.wellfield.magpie.upnp.beans.UPnPElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.ActionList;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.ServiceList;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.upnp.UPnPStatus;

public class UPnPUtil {
	private static Log log = LogFactory.getLog(UPnPUtil.class);

	public static StateVariable queryStateVariable(Device dev, String name) {
		StateVariable var = dev.getStateVariable(name);
		
		if (var == null) {
			if (log.isErrorEnabled()) {
				log.error("StateVariable " + name + " not supported by device " + dev.getFriendlyName());
			}
			return null;
		}
		
		boolean result = var.postQuerylAction();
		if (!result) {
			UPnPStatus status = var.getStatus();
			if (log.isErrorEnabled()) {
				log.error("StateVariable " + name + " failed on device " + dev.getFriendlyName() + ": " + status.getDescription() + "(" + status.getCode() + ")");
			}
			return null;
		}
		
		return var;
	}
	
	public static ActionResult postAction(Action a) {
//		Action a = dev.getAction(name);

		if (a == null) {
			if (log.isErrorEnabled()) {
				log.error("Action " + a.getName() + " not supported!!!");
			}
			return null;
		}
		
		//if (arguments != null) a.setArgumentValues(arguments);
		
		boolean actResult = a.postControlAction();
		if (!actResult) {
			UPnPStatus status = a.getStatus();
			if (log.isErrorEnabled()) {
				log.error("Action " + a.getName() + " failed : " + status.getDescription() + "(" + status.getCode() + ")");
			}
			return null;
		}
		
		ActionResult result = new ActionResult();
		
		ArgumentList list = a.getOutputArgumentList();
		for(Iterator it=list.iterator();it.hasNext();) {
			Argument arg = (Argument)it.next();
			result.setValue(arg.getName(), arg.getValue());
		}
		
		if (log.isDebugEnabled()) log.debug(a.getName() + " OK");
		return result;
	}
	
	
	/*************************
	 * AVTransport Service
	 *************************/
	/*
	public static PositionInfo getPositionInfo(Device dev, String instanceId) {
		ArgumentList arguments = new ArgumentList();
		arguments.add(new Argument("InstanceID", instanceId));
		
		Action a = dev.getAction("GetPositionInfo");
		a.setArgumentValues(arguments);
		
		ActionResult result = postAction(dev, "GetPositionInfo", arguments);
		return new PositionInfo(result);
	}
	
	public static TransportInfo getTransportInfo(Device dev, String instanceId) {
		ArgumentList arguments = new ArgumentList();
		arguments.add(new Argument("InstanceID", instanceId));
		
		Action a = dev.getAction("GetTransportInfo");
		a.setArgumentValues(arguments);
		
		ActionResult result = postAction(dev, "GetTransportInfo", arguments);
		return new TransportInfo(result);		
	}
	
	public static boolean setAVTransportURI(Device dev, String instanceId, String currentURI, String metadata) {
		ArgumentList arguments = new ArgumentList();
		
		arguments.add(new Argument("InstanceID", instanceId));
		arguments.add(new Argument("CurrentURI", currentURI));
		arguments.add(new Argument("CurrentURIMetaData", metadata));
		
		/*
		Service svc = dev.getService("urn:schemas-upnp-org:service:AVTransport:1");
		Enumeration e = svc.getServiceStateTable().elements();
		while (e.hasMoreElements()) {
			StateVariable sv = (StateVariable)e.nextElement();
			System.out.println(sv.getName());
			AllowedValueList avl = sv.getAllowedValueList();
			Enumeration ave = avl.elements();
			while (ave.hasMoreElements()) {
				AllowedValue v = (AllowedValue)ave.nextElement();
				System.out.println("  " + v.getValue());
			}
		}
		*
		
		return postAction(dev, "SetAVTransportURI", arguments) != null;
	}
	
	public static boolean play(Device dev, String instanceId) {
		ArgumentList arguments = new ArgumentList();
		arguments.add(new Argument("InstanceID", instanceId));
		arguments.add(new Argument("Speed", "1"));
		
		boolean result = postAction(dev, "Play", arguments) != null;
		if (result) {
			if (log.isDebugEnabled()) log.debug("sent 'play' to device " + dev.getFriendlyName());
		}
		return result;
	}
	
	public static boolean stop(Device dev, String instanceId) {
		ArgumentList arguments = new ArgumentList();		
		arguments.add(new Argument("InstanceID", instanceId));
		
		boolean result = postAction(dev, "Stop", arguments) != null;
		if (result) {
			if (log.isDebugEnabled()) log.debug("sent 'stop' to device " + dev.getFriendlyName());
		}
		return result;
	}
	
	public static boolean pause(Device dev, String instanceId) {
		ArgumentList arguments = new ArgumentList();		
		arguments.add(new Argument("InstanceID", instanceId));
		
		boolean result = postAction(dev, "Pause", arguments) != null;
		if (result) {
			if (log.isDebugEnabled()) log.debug("sent 'pause' to device " + dev.getFriendlyName());
		}
		return result;
	}
	
	public static boolean seek(Device dev, String instanceId, String position) {
		System.out.println("should seek " + dev.getFriendlyName() + " to " + position);
		
		ArgumentList arguments = new ArgumentList();
		arguments.add(new Argument("InstanceID", instanceId));
		arguments.add(new Argument("Unit", "REL_TIME"));
		arguments.add(new Argument("Target", position));					
		
		boolean result = postAction(dev, "Seek", arguments) != null;
		if (result) {
			if (log.isDebugEnabled()) log.debug("sent 'seek' to device " + dev.getFriendlyName());
		}
		return result;
	}
	*/
	/***************************
	 * Content Directory Service
	 ***************************/
	/*
	public static String createObject(Device device, String containerId, Item element) {
	    if (log.isDebugEnabled()) log.debug("CreateObject start: " + device.getFriendlyName());
	    element.setId("");
	    element.setRestricted(false);
	    element.getRes().setProtocolInfo("*:*:audio:*");
	    
	    XStream xs = new XStream();
	    xs.alias("item", Item.class);
	    xs.alias("res", Res.class);
	    
	    //UPnPElement
	    xs.aliasField("upnp:class", UPnPElement.class, "clazz");
	    xs.aliasField("dc:title", UPnPElement.class, "title");
	    xs.aliasField("dc:artist", UPnPElement.class, "artist");
	    xs.aliasField("dc:album", UPnPElement.class, "album");
	    xs.aliasField("dc:genre", UPnPElement.class, "genre");
	    xs.addImplicitCollection(UPnPElement.class, "res");
	    xs.omitField(UPnPElement.class, "parentID");
	    xs.useAttributeFor(UPnPElement.class, "id");
	    xs.useAttributeFor(UPnPElement.class, "restricted");
	    
	    //Res
        xs.useAttributeFor(Res.class, "protocolInfo");
//        xs.useAttributeFor(Res.class, "duration");
//        xs.useAttributeFor(Res.class, "size");
//        xs.useAttributeFor(Res.class, "bitrate");
        xs.omitField(Res.class, "duration");
        xs.omitField(Res.class, "size");
        xs.omitField(Res.class, "bitrate");
        xs.omitField(Res.class, "value");
        
	    String xml = xs.toXML(element);
	    xml = "<DIDL-Lite xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite\">" + xml + "</DIDL-Lite>";
	    log.debug("xml: " + xml);
	    
	    Action action = device.getAction("CreateObject");
	    action.setArgumentValue("ContainerID", containerId);
	    action.setArgumentValue("Elements", xml);
	    
	    boolean actionOk = action.postControlAction();
	    UPnPStatus status = action.getStatus();
	    if (!actionOk) {
	        if (log.isErrorEnabled()) {
	            
	            log.error("CreateObject failed: " + status.getDescription()  + "(" + status.getCode() + ")");
	        }
	        return null;
	    } else {
	        if (log.isDebugEnabled()) log.debug("CreateObject OK: " + status.getCode());
	    }

	    String objectId = action.getArgumentValue("ObjectID");
	    if (log.isDebugEnabled()) log.debug("ObjectID: " + objectId);
	    
	    String result = action.getArgumentValue("Result");
	    if (log.isDebugEnabled()) log.debug("result: " + result);
	    Sax sax = new Sax();
	    sax.parseDocument(result);
	    UPnPElement elem = sax.getElements().get(0);

	    String importUri = elem.getRes().getImportUri();
	    if (log.isDebugEnabled()) log.debug("CreateObject end: " + device.getFriendlyName() + ": " + importUri);
	    return importUri;
	}
	*/
	
	public static void importResource(Device device, String sourceUri, String destinationUri) {
	    if (log.isDebugEnabled()) log.debug("ImportResource start on " + device.getFriendlyName());
	    
	    Action action = device.getAction("ImportResource");
	    action.setArgumentValue("SourceURI", sourceUri);
	    action.setArgumentValue("DestinationURI", destinationUri);
	    
	    boolean actionOk = action.postControlAction();
	    if (!actionOk) {
	        UPnPStatus status = action.getStatus();
	        if (log.isErrorEnabled()) {
	            log.error("ImportResource on " + device.getFriendlyName() + " failed: " + status.getDescription() + "(" + status.getCode() + ")");
	        }
	        return;
	    }
	    
	    ArgumentList oaList = action.getOutputArgumentList();
	    String transId = oaList.getArgument("TransferID").getValue();
	    
	    //monitor transfer progress
	    action = device.getAction("GetTransferProgress");
	    action.setArgumentValue("TransferID", transId);

	    if (log.isInfoEnabled()) {
	        log.info("src: " + sourceUri);
	        log.info("dst: " + destinationUri);
	    }
	    
	    String status = "IN_PROGRESS";
	    while (status.equals("IN_PROGRESS")) {
    	    action.postControlAction();
    	    if (!actionOk) {
    	        if (log.isErrorEnabled()) log.error("GetTransferProgress failed on TransferID " + transId + ": " + action.getStatus());
    	        return;
    	    }
    	    
            oaList = action.getOutputArgumentList();
            status = oaList.getArgument("TransferStatus").getValue();
            String length = oaList.getArgument("TransferLength").getValue();
            String total = oaList.getArgument("TransferTotal").getValue();
            
    	    if (log.isInfoEnabled()) {
    	        log.info("transportID: " + transId + ", status: " + status + ", length: " + length + ", total: " + total);
    	    }
    	    
    	    try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	    }
	}
	
	public static List<UPnPElement> search(Action search, String containerId, String searchCriteria, String filter, String sortCriteria) {
	    search.setArgumentValue("ContainerID", containerId);
	    search.setArgumentValue("SearchCriteria", searchCriteria);
	    search.setArgumentValue("Filter", filter);
	    search.setArgumentValue("StartingIndex", 0);
	    search.setArgumentValue("RequestedCount", 9999);
	    search.setArgumentValue("SortCriteria", sortCriteria);
	    
	    boolean actionOk = search.postControlAction();
	    
	    if (!actionOk) {
	        return new ArrayList<UPnPElement>();
	    }
	    
	    ArgumentList oaList = search.getOutputArgumentList();
	    String result = oaList.getArgument("Result").getValue();
	    if (log.isDebugEnabled()) {
	        log.debug("---------------- search ----------------");
	        log.debug(result);
	    }

	    return null;
	}
	
	public static ArgumentList browse(Action browse, String objectId, String browseFlag, int startingIndex, int requestedCount, StringBuffer xml) {
		if (requestedCount > 9999) requestedCount = 9999;
	    
	    browse.setArgumentValue("ObjectID", objectId);
		browse.setArgumentValue("Filter", "*");
		browse.setArgumentValue("BrowseFlag", browseFlag);
		browse.setArgumentValue("StartingIndex", startingIndex);
		browse.setArgumentValue("RequestedCount", requestedCount);
		
//		Debug.on();
		boolean actionOk = browse.postControlAction();
//		Debug.off();
		
		if (!actionOk) {
			return null;
		}
		
		ArgumentList oaList = browse.getArgumentList();

		String result = oaList.getArgument("Result").getValue();
		xml.append(result);
		
		return oaList;
	}
	
	
	/************************
	 * Debugging Purposes
	 ************************/
	/*
	public static void iterateServices(ServiceList services) {
		for(Iterator it=services.iterator();it.hasNext();) {
			Service service = (Service)it.next();
			
			if (log.isInfoEnabled()) log.info("Service: " + service.getServiceID());
			
			ActionList actions = service.getActionList();
			for (Iterator actIt = actions.iterator(); actIt.hasNext();) {
				Action action = (Action)actIt.next();
				
				if (log.isInfoEnabled()) log.info("  Action: " + action.getName());

				int inArgs = 0;
				int outArgs = 0;
				ArgumentList args = action.getArgumentList();
				for(Iterator argIt = args.iterator();argIt.hasNext();) {
					Argument arg = (Argument)argIt.next();
					
					if (log.isInfoEnabled()) log.info("    " + arg.getDirection() + " - " + arg.getName() + ": " + arg.getValue());
					if (arg.getDirection().equalsIgnoreCase("in")) {
						inArgs++;
					} else if (arg.getDirection().equalsIgnoreCase("out")) {
						outArgs++;
					}
				}
				if (inArgs == 0) {
					if (log.isInfoEnabled()) log.info("    Posting " + action.getName());
					ActionResult result = postAction(service.getDevice(), action.getName(), null);
					if (log.isInfoEnabled()) log.info("      " + result);
				}
			}
			
		}
	}
	*/

}
