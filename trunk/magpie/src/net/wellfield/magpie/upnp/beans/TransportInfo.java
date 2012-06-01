package net.wellfield.magpie.upnp.beans;

public class TransportInfo extends ActionResult {

	public TransportInfo() {}
	
	public TransportInfo(ActionResult result) {
		this.values = result.values;
	}
	
	public String getCurrentTransportState() {
		return values.get("CurrentTransportState");
	}
	
	public String getCurrentTransportStatus() {
		return values.get("CurrentTransportStatus");
	}
	
	public String getCurrentSpeed() {
		return values.get("CurrentSpeed");
	}
}
