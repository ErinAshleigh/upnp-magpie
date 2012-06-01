package net.wellfield.magpie;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.wellfield.magpie.upnp.beans.UPnPElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Sax extends DefaultHandler {
    private static Log log = LogFactory.getLog(Sax.class);
    
	private Map<UPnPElement, List<UPnPElement>> elements;
	private UPnPElement curElement;
	
	public void parseDocument(String xml) {
		SAXParserFactory spf = SAXParserFactory.newInstance();

		// add encoding if necessary
		if (!xml.startsWith("<?")) {
		    xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
		}
		
		try {
			SAXParser parser = spf.newSAXParser();
			InputStream is = new ByteArrayInputStream(xml.getBytes());
			
			parser.parse(is, this);
			
//			if (log.isDebugEnabled()) log.debug(xml);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
				log.error(xml);
			}
			
		}
	}
	
	@Override
	public void startDocument() throws SAXException {
		elements = new HashMap<UPnPElement, List<UPnPElement>>();
	}
	
	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		
		curElement = new UPnPElement(name, curElement);
//		System.out.println(name);

		int nrAtts = attributes.getLength();
		for (int i=0;i<nrAtts;i++) {
		    curElement.addAttribute(attributes.getQName(i), attributes.getValue(i));
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
	    List<UPnPElement> elems = elements.get(curElement.getParent());
	    if (elems == null) {
	        elems = new ArrayList<UPnPElement>();
	        elements.put(curElement.getParent(), elems);
	    }
	    elems.add(curElement);
//	    System.out.println(" " + curElement.getValue());
	    curElement = curElement.getParent();
	    if (curElement != null) curElement.setSubElements(elems);
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		//text.append(ch, start, length);
	    curElement.appendValue(ch,start,length);
	}

	public Map<UPnPElement, List<UPnPElement>> getElements() {
		return elements;
	}
	
	
}
