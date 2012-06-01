package net.wellfield.magpie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.wellfield.magpie.upnp.beans.ActionResult;
import net.wellfield.magpie.upnp.beans.UPnPElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Icon;
import org.cybergarage.upnp.Service;

public class UPnPAnalyzer {
    private static final Log log = LogFactory.getLog(Start.class);
    
    private static Map<String, UPnPAnalyzer> instances = new HashMap<String, UPnPAnalyzer>();
    
    private String deviceData;
    private String ssdpData;
    private Map<String, String> serviceData;
    private String descriptionFilePath;
    private String urlBase;
    private boolean urlBaseDelivered;
    
    private Map<String, byte[]> imgData;
    
    private Map<String, Map<String, String>> argumentData;
    private Map<String, String> additionalData;
    private Map<String, Object> submitMap;
    private StringBuffer reviewData;
    
    public static UPnPAnalyzer getInstance(String uuid) {
        UPnPAnalyzer instance = instances.get(uuid);
        if (instance == null) {
            instance = new UPnPAnalyzer();
            instances.put(uuid, instance);
        }
        return instance;
    }

    public Map<String, Object> getSubmitMap() {
        return submitMap;
    }
    
    public Map<String, byte[]> getImgData() {
        return imgData;
    }
    
    public void analyzeDevice(Device device) {
        deviceData = device.getRootNode().toString();
        ssdpData = device.getSSDPPacket().toString();
        
        descriptionFilePath = device.getDescriptionFilePath();
        if (descriptionFilePath == null || descriptionFilePath.length() == 0) {
            descriptionFilePath = device.getLocation();
        }
        urlBase = device.getURLBase();
        urlBaseDelivered = !(urlBase == null || urlBase.length() == 0);
        
        if (!urlBaseDelivered) {
            urlBase = descriptionFilePath.substring(0, descriptionFilePath.indexOf("/", 7));
        }
        if (!urlBase.endsWith("/")) urlBase += "/";
        if (log.isDebugEnabled()) log.debug("URL Base: " + urlBase);
        
        serviceData = new HashMap<String, String>(); //key is serviceType, value is scpdXml
        for(Iterator<Service> it=device.getServiceList().iterator();it.hasNext();) {
            Service service = it.next();
            
            try {
                String dest = urlBase + service.getSCPDURL();
                dest = dest.replaceAll("//", "/");
                dest = dest.replaceFirst("http:/", "http://");
                URL url = new URL(dest);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                
                StringBuffer sb = new StringBuffer();
                String str;
                while ((str = in.readLine()) != null) {
                  sb.append(str);
                }
                in.close();

                if (log.isDebugEnabled()) log.debug("Service - serviceId: " + service.getServiceID() + "=" + sb.toString());
                
                serviceData.put(service.getServiceID(), sb.toString());
                
            } catch (Exception e) {
                if (log.isErrorEnabled()) log.error(e.getMessage(), e);
            }
        }
        
        //get icon data
        imgData = new HashMap<String, byte[]>();
        boolean fetchIcons = !(urlBase==null || urlBase.length()==0); 
        for(Iterator<Icon> it=device.getIconList().iterator();it.hasNext();) {
            Icon icon = it.next();
            
            if (!fetchIcons) continue;
            //download binary icon data
            try {
                String dest = urlBase + icon.getURL();
                dest = dest.replaceAll("//", "/");
                dest = dest.replaceFirst("http:/", "http://");
                if (log.isDebugEnabled()) log.debug("getting icon from " + dest);
                URL url = new URL(dest);
                
                int length = url.openConnection().getContentLength();
                byte[] buffer = loadBytes(url.openStream(), length);
                
                imgData.put(icon.getURL(), buffer);
            } catch (Exception e) {
                if (log.isErrorEnabled()) log.error(e.getMessage(), e);
            }
        }
        
        //execute actions
        argumentData = new HashMap<String , Map<String, String>>();
        additionalData = new HashMap<String, String>();
        String deviceType = device.getDeviceType();
        if (deviceType.startsWith("urn:schemas-upnp-org:device:MediaServer:")) {
            analyzeServerBehaviour(device);
        } else if (deviceType.startsWith("urn:schemas-upnp-org:device:MediaRenderer:")) {
            analyzeRendererBehaviour(device);
        }
        
        try {
            // Construct submit data
            submitMap = new HashMap<String, Object>();
            
            submitMap.put("deviceData", deviceData);
            submitMap.put("ssdp", ssdpData);
            submitMap.put("dfp", descriptionFilePath);
            submitMap.put("ubd", urlBaseDelivered?"1":"0");
            submitMap.put("rub",urlBase);
            submitMap.put("serviceData", serviceData);
            submitMap.put("argumentData", argumentData);
            submitMap.put("additionalData", additionalData);

            //construct review data (same as construct data but not encoded - for human readability)
            reviewData = new StringBuffer("deviceData=" + deviceData);
            reviewData.append("&ssdp=" + ssdpData);
            reviewData.append("&dfp=" + descriptionFilePath);
            reviewData.append("&ubd=" + (urlBaseDelivered?"1":"0"));
            reviewData.append("&rub=" + urlBase);

            for(String key : serviceData.keySet()) {
                reviewData.append("&" + key + "=" + serviceData.get(key));
            }
            
            for(String actionName : argumentData.keySet()) {
                Map<String, String> actionParams = argumentData.get(actionName);
                for(String paramName : actionParams.keySet()) {
                    String paramValue = actionParams.get(paramName);
                    reviewData.append("&" + actionName + "=" + paramName + ";" + paramValue);
                }
            }
            
            for (String addKey : additionalData.keySet()) {
                reviewData.append("&additionalData=" + addKey + ";" + additionalData.get(addKey));
            }
            
            Start.getInstance().updateMessageArea(reviewData.toString());
            
//            log.debug(submitData.toString());
            if (log.isInfoEnabled()) log.info("analysis done");            
        } catch (Exception e) {
            e.printStackTrace();
        }
        

    }
    
    /*
     * required services for mediaservers are:
     * * ContentDirectory 
     *   - GetSearchCapabilities
     *   - GetSortCapabilities
     *   - GetSystemUpdateID
     *   - Browse
     *   
     * * ConnectionManager
     *   - GetProtocolInfo
     *   - GetCurrentConnectionIDs
     *   - GetCurrentConnectionInfo
     * 
     * optional service for mediaservers
     * * AVTransport
     *   - SetAVTransportURI
     *   - GetMediaInfo
     *   - GetTransportInfo
     *   - GetPositionInfo
     *   - GetDeviceCapabilities
     *   - GetTransportSettings
     *   - Stop
     *   - Play
     *   - Seek
     *   - Next
     *   - Previous
     * 
     */
    private void analyzeServerBehaviour(Device dev) {
        //ContentDirectory
        testAction(dev.getAction("GetSearchCapabilities"));

        testAction(dev.getAction("GetSortCapabilities"));
        
        testAction(dev.getAction("GetSystemUpdateID"));
//        browse(dev, "0", "BrowseMetadata", 0, 10, 0);
        
        if (!argumentData.containsKey("Browse")) {
            Map<String, String> data = new HashMap<String, String>();
            argumentData.put("Browse", data);
        }
        browse(dev, "0", "BrowseDirectChildren", 0, 10, 0);
        

        //ConnectionManager
        testAction(dev.getAction("GetProtocolInfo"));
        
        testAction(dev.getAction("GetCurrentConnectionIDs"));
        
        Action a = dev.getAction("GetCurrentConnectionInfo");
        a.setArgumentValue("ConnectionID", "0");
        testAction(a);
    }
    
    /*
     * required
     * * RenderingControl
     *   - ListPresets
     *   - SelectPreset
     * 
     * * ConnectionManager
     *   - GetProtocolInfo
     *   - GetCurrentConnectionIDs
     *   - GetCurrentConnectionInfo
     * 
     * optional
     * * AVTransport
     */
    private void analyzeRendererBehaviour(Device dev) {
        //ConnectionManager
        testAction(dev.getAction("GetProtocolInfo"));
        
        testAction(dev.getAction("GetCurrentConnectionIDs"));
        
        Action a = dev.getAction("GetCurrentConnectionInfo");
        a.setArgumentValue("ConnectionID", "0");
        testAction(a);
        
        
        //RenderingControl
        a = dev.getAction("ListPresets");
        a.setArgumentValue("InstanceID", "0");
        testAction(a);
        argumentData.get("ListPresets");
        
    }
    
    /**
     * browses the given object including sub-containers
     * after going deeper 1 level, only the first sub-container is browsed
     * @param dev
     * @param objectId
     * @param startingIndex
     * @param requestedCount
     * @param level
     */
    private void browse(Device dev, String objectId, String browseFlag, int startingIndex, int requestedCount, int level) {
        
        Map<String, String> browseData = argumentData.get("Browse");
        
        StringBuffer xml = new StringBuffer();
        ArgumentList result = UPnPUtil.browse(dev.getAction("Browse"), objectId, browseFlag, startingIndex, requestedCount, xml);
        if (level == 0) {
            for(Iterator<Argument>it=result.iterator();it.hasNext();) {
                Argument arg = it.next();
                browseData.put(arg.getName(), arg.getValue());
            }
        }

        Sax sax = new Sax();
        sax.parseDocument(xml.toString());
        
        Map<UPnPElement, List<UPnPElement>> map = sax.getElements();
        
        int conts = 0; //containers found
        for(Iterator<UPnPElement>it=map.keySet().iterator();it.hasNext();) {
            UPnPElement curItem = it.next();
            if (curItem == null) continue;
            
            if (curItem.getTagName().equals("container")) {
                if (level == 0 || conts++ == 0) { //if not root container, browse only the first sub-container
                    String id = curItem.getAttribute("id");
                    String name = curItem.getSubElement("dc:title").getValue();
                    System.out.println("browsing " + name + "(" + id + ")");
                    browse(dev, id, browseFlag, startingIndex, requestedCount, level+1);
                }
            }
            UPnPElement upnpClass = curItem.getSubElement("upnp:class");
            if (upnpClass != null && !additionalData.containsKey(upnpClass.getValue())) {
                additionalData.put(upnpClass.getValue(), curItem.toString());
                System.out.println("added " + upnpClass.getValue());
            }
        }
    }
    
    private void testAction(Action action) {
        if (action == null) return;
        
        String name = action.getName();
        ActionResult result = UPnPUtil.postAction(action);
        if (result != null) {
            
            argumentData.put(name, result.getValues());
            if (log.isDebugEnabled()) log.debug(name + ": " + result);
        } else {
            if (log.isErrorEnabled()) log.error("Action " + name + " threw an error!");
        }
    }
    
    private static byte[] loadBytes( InputStream in, int size ) throws IOException {
        int count, index = 0;
        byte[] b = new byte[size];

        // read in the bytes from input stream
        while( ( count = in.read( b, index, size ) ) > 0 ) {
            size -= count;
            index += count;
        }
        return b;
    }

}
