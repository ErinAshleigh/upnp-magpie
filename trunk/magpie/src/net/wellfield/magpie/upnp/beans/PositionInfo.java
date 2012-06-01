package net.wellfield.magpie.upnp.beans;


public class PositionInfo extends ActionResult {
	
	public PositionInfo() {}
	
	public PositionInfo(ActionResult result) {
		this.values = result.values;
	}
	
	public int getTrack() {
		String value = values.get("Track");
		return Integer.parseInt(value);
	}
	public String getTrackDuration() {
		return values.get("TrackDuration");
	}
	public String getTrackMetaData() {
		return values.get("TrackMetaData");
	}
	public String getTrackURI() {
		return values.get("TrackURI");
	}
	public String getRelTime() {
		return values.get("RelTime");
	}
	public String getAbsTime() {
		return values.get("AbsTime");
	}
	public String getRelCount() {
		return values.get("RelCount");
	}
	public String getAbsCount() {
		return values.get("AbsCount");
	}
}
