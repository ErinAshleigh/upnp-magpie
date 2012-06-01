package net.wellfield.magpie.upnp.beans;

public class Res {
	private String protocolInfo;
	private String duration;
	private String size;
	private String bitrate;
	private String sampleFrequency;
	private String bitsPerSample;
	private String nrAudioChannels;
	private String importUri;
	
	private String value;
	
	public String getProtocolInfo() {
		return protocolInfo;
	}
	public void setProtocolInfo(String protocolInfo) {
		this.protocolInfo = protocolInfo;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
    public String getBitrate() {
        return bitrate;
    }
    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }
    public String getSampleFrequency() {
        return sampleFrequency;
    }
    public void setSampleFrequency(String sampleFrequency) {
        this.sampleFrequency = sampleFrequency;
    }
    public String getBitsPerSample() {
        return bitsPerSample;
    }
    public void setBitsPerSample(String bitsPerSample) {
        this.bitsPerSample = bitsPerSample;
    }
    public String getNrAudioChannels() {
        return nrAudioChannels;
    }
    public void setNrAudioChannels(String nrAudioChannels) {
        this.nrAudioChannels = nrAudioChannels;
    }
	public void setImportUri(String importUri) {
        this.importUri = importUri;
    }
	public String getImportUri() {
        return importUri;
    }
	
}
