package net.wellfield.magpie.upnp.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UPnPElement {
    private String tagName;
    private List<UPnPElement> subElements = new ArrayList<UPnPElement>();
    private Map<String, String> attributes = new LinkedHashMap<String, String>();
    private StringBuffer value;
    private UPnPElement parent;
    
    public UPnPElement() {}
    
    public UPnPElement(String tagName, UPnPElement parent) {
        this();
        this.tagName = tagName;
        this.parent = parent;
        this.value = new StringBuffer();
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<" + tagName);
        for(Iterator<String>it=attributes.keySet().iterator();it.hasNext();) {
            String key = it.next();
            sb.append(" " + key + "=\"" + attributes.get(key) + "\"");
        }
        sb.append(">" + getValue());
        
        for(Iterator<UPnPElement>it=subElements.iterator();it.hasNext();) {
            UPnPElement el = it.next();
            sb.append(el.toString());
        }
        
        sb.append("</" + tagName + ">\r\n");
        
        return sb.toString();
    }
    
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    public String getTagName() {
        return tagName;
    }
    public List<UPnPElement> getSubElements() {
        return subElements;
    }
    public void setSubElements(List<UPnPElement> subElements) {
        this.subElements = subElements;
    }
    public void addSubElement(UPnPElement element) {
        subElements.add(element);
    }
    public UPnPElement getSubElement(String name) {
        for(Iterator<UPnPElement>it=subElements.iterator();it.hasNext();) {
            UPnPElement el = it.next();
            if (el.getTagName().equals(name)) return el;
        }
        
        return null;
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }
    public Map<String, String> getAttributes() {
        return attributes;
    }
    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }
    public void setParent(UPnPElement parent) {
        this.parent = parent;
    }
    public UPnPElement getParent() {
        return parent;
    }
    public void appendValue(char[]ch, int start, int length) {
        if (ch != null) {
            this.value.append(ch, start, length);
        }
    }
    public String getValue() {
        return value.toString();
    }
    
    public void setValue(String value) {
        this.value = new StringBuffer(value);
    }
}
