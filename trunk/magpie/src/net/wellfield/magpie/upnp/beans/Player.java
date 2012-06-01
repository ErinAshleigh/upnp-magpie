package net.wellfield.magpie.upnp.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;

public class Player {

    private String address;
    private String name;
    private List<Action> actions;
    private String header;
    
    public Player() {
        
    }
    
    public Player(String address, String name) {
        this();
        this.address = address;
        this.name = name;
        this.actions = new ArrayList<Action>();
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress() {
        return address;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
    public List<Action> getActions() {
        return actions;
    }
    public void addAction(Action action) {
        //if (!containsAction(action.getName())) {
            this.actions.add(action);
            System.out.println(this.toString());
        //}
    }
    public boolean containsAction(String actionName) {
        for(Action a : actions) {
            if (a.getName().equals(actionName)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(name + "-");
        for(Action action : actions) {
            result.append(action.getName() + ":");
            for(Iterator<Argument> argIt = action.getInputArgumentList().iterator();argIt.hasNext();) {
                Argument arg = argIt.next();
                String value = arg.getValue().replaceAll(":", "&colon;");
                result.append(arg.getName() + "=" + value + "#");
            }
            result.append("-");
        }
        //result.append("header:" + header);
        
        return result.toString();
    }
    public void setHeader(String header) {
        this.header = header;
    }
    public String getHeader() {
        return header;
    }
}
