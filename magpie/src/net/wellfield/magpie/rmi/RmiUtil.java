package net.wellfield.magpie.rmi;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wellfield.birdsnest.rmi.DataService;

public class RmiUtil {
    private static Log log = LogFactory.getLog(RmiUtil.class);
    
    private DataService dataService;
    
    public Integer doIt(Map params) {
        if (log.isDebugEnabled()) log.debug("Calling RMI method");
        
        Integer deviceId = dataService.receiveData(params);
        if (log.isDebugEnabled()) log.debug("deviceId: " + deviceId);
        
        return deviceId;
    }
    
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
}
