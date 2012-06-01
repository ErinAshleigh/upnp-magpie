package net.wellfield.magpie;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.util.Iterator;

import net.wellfield.magpie.upnp.beans.Player;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cybergarage.http.HTTPSocket;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.upnp.control.ActionListener;
import org.cybergarage.upnp.control.ActionRequest;
import org.cybergarage.upnp.control.QueryListener;

public class Server extends Device implements ActionListener, QueryListener {
    private static Log log = LogFactory.getLog(Server.class);
    
    private static int systemUpdateId = 1;
    private static boolean running = false;
    
    public Server(String descriptionFile) throws Exception {
        super(descriptionFile);
//        Debug.on();
        String hostName = InetAddress.getLocalHost().getHostName();
        this.setFriendlyName(this.getFriendlyName().replace("*IP*", hostName));
        this.setUDN("123456magpie7890");

        setActionListener(this, true);
        setQueryListener(this, true);
    }
    
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public boolean start() {
        boolean result = super.start();
        running = true;
        
        return result;
    }
    
    @Override
    public boolean stop() {
        boolean result = super.stop();
        running = false;
        
        return result;
    }
    
    public boolean actionControlReceived(Action action, HTTPSocket sock, ActionRequest req) {
        if (log.isDebugEnabled()) log.debug("actionControlReceived: " + action.getService().getServiceID() + "::" + action.getName() + " (" + action.getService().getDevice().getFriendlyName() + ")");
        
        String address = sock.getSocket().getInetAddress().toString();
        
        Player player;
        Start start = Start.getInstance();
        if (!start.containsPlayer(address)) {
            player = new Player(address, address);
            start.addPlayer(player);
        } else {
            player = start.getPlayer(address);
        }
        player.addAction(action);
        if (log.isDebugEnabled()) {
            for(Iterator<Argument> it=action.getInputArgumentList().iterator();it.hasNext();) {
                Argument arg = it.next();
                log.debug(arg.getName() + ": " + arg.getValue());
            }
            log.debug("remote inet address: " + sock.getSocket().getInetAddress().toString());
            log.debug("header: " + req.getHeader());
        }
        
        player.setHeader(req.getHeader());
        
        if (action.getName().equals("Browse")) {
            
            String objectId = action.getArgumentValue("ObjectID");
            String browseFlag = action.getArgumentValue("BrowseFlag");
            if (browseFlag.equals("BrowseMetadata")) {
                if (objectId.equals("0")) { //root
                    String result = readFileAsString("data/root-md.xml");
                    action.setArgumentValue("Result", result);
                    action.setArgumentValue("NumberReturned", 1);
                    action.setArgumentValue("TotalMatches", 1);
                    action.setArgumentValue("UpdateID", systemUpdateId);
                } else {
                    String result = readFileAsString("data/video-md.xml");
                    action.setArgumentValue("Result", result);
                    action.setArgumentValue("NumberReturned", 1);
                    action.setArgumentValue("TotalMatches", 1);
                    action.setArgumentValue("UpdateID", systemUpdateId);
                }
            } else if (browseFlag.equals("BrowseDirectChildren")) {
                if (objectId.equals("0")) { //root
                    String result = readFileAsString("data/root-dc.xml");
                    action.setArgumentValue("Result", result);
                    action.setArgumentValue("NumberReturned", 5);
                    action.setArgumentValue("TotalMatches", 5);
                    action.setArgumentValue("UpdateID", systemUpdateId);
                } else {
                    String result = readFileAsString("data/item.xml");
                    action.setArgumentValue("Result", result);
                    action.setArgumentValue("NumberReturned", 2);
                    action.setArgumentValue("TotalMatches", 2);
                    action.setArgumentValue("UpdateID", systemUpdateId);
                }
            }
            
            return true;
        } else if (action.getName().equals("GetSystemUpdateID")) {
            
            action.setArgumentValue("Id", systemUpdateId);
            
            return true;
        }

        return false;
    }

    public boolean queryControlReceived(StateVariable stateVar) {
        if (log.isDebugEnabled()) log.debug("queryControlReceived: " + stateVar.getName());
        return false;
    }
    
    private static String readFileAsString(String filePath) {
        try {
            StringBuffer fileData = new StringBuffer(1000);
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            char[] buf = new char[1024];
            int numRead=0;
            while((numRead=reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
            
            return fileData.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

}
