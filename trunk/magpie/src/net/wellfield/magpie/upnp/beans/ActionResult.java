package net.wellfield.magpie.upnp.beans;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ActionResult {

	protected Map<String, String> values;
	
	public ActionResult() {
		values = new HashMap<String, String>();
	}
	
	public void setValue(String key, String value) {
		values.put(key, value);
	}
	
	public String getValue(String key) {
		return values.get(key);
	}
	
	public Map<String, String> getValues() {
        return values;
    }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<String>it=values.keySet().iterator();it.hasNext();) {
			String key = it.next();
			sb.append(key + ": " + values.get(key) + " \r\n ");
		}
		return sb.toString();
	}
}
