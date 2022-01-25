package de.rwth.oosc.flatdesigner.bean;

import java.net.URL;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
public class Image {
	
	@JsonProperty
    private int id;

    @JsonProperty
    private URL url;

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING) // serialize date as ISO 8601 formatted string
    private LocalDateTime publishedAt;

    @JsonProperty
    private boolean favorite;

    public Image(int id, URL url) {
        this(id, url, false);
    }

    public Image(int id, URL url, boolean favorite) {
        this.id = id;
        this.url = url;
        this.publishedAt = LocalDateTime.now();
        this.favorite = favorite;
    }

    public int getId() {
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
    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

	
}
