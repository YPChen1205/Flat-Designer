package de.rwth.oosc.flatdesigner.bean;

import java.net.URL;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@JsonAutoDetect
public class Image {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonProperty
    private long id;

    @JsonProperty
    private URL url;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING) // serialize date as ISO 8601 formatted string
    private LocalDateTime publishedAt;

    @JsonProperty
    private boolean approved;
    
    @JsonIgnore
    private byte[] imageData;
    
    public Image() {
    	this(null);
    }
    
    public Image(URL url) {
        this(url, false);
    }

    public Image(URL url, boolean approved) {
        this.url = url;
        this.publishedAt = LocalDateTime.now();
        this.approved = approved;
    }

    public long getId() {
        return id;
    }

    public URL getUrl() {
        return url;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    /**
     * Get favorite status of image (an image can be marked to be a favorite).
     * @return true if image is favorite, false otherwise
     */
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    
    public void setUrl(URL url) {
    	this.url = url;
    }
    
    public void setImageData(byte[] imageData) {
    	this.imageData = imageData;
    }

    public byte[] getImageData() {
		return imageData;
	}
}
