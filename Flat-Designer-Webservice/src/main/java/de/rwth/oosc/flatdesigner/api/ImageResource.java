package de.rwth.oosc.flatdesigner.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.rwth.oosc.flatdesigner.bean.Image;

@RestController
@RequestMapping("/api/images")
public class ImageResource {
	/**
     * A a logger you may use for logging
     */
    private Logger logger;

    /**
     * List of all images
     */
    private List<Image> images = new ArrayList<>();

    /**
     * Next new image's id
     */
    private int nextId = 0;

    /**
     * Get all images in the standard representation format defined by the Domain Entities
     **/
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Image> getAllImages() {

        logger.info("Sending {} Images", images.size());
        return images;
    }

    /**
     * Gets a single image for a given id
     * @param id id
     * @return Image object or error 404 not found if no image exists for the given id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Image getImageRequest(@PathVariable("id") int id) {

        Image image = getImageForGivenId(id) ;
        if(image != null) {
            return image;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found.");
        }
    }

    @PostMapping("/create")
    public Image createImageRequest(/* todo */) {
        // todo: implement (you may need to add more than just the method signature and body)
        return null;
    }

    public Image updateImagePropertiesRequest(/* todo */) {
        // todo: implement (you may need to add more than just the method signature and body)
        return null;
    }

    public void deleteImageRequest(/* todo */) {
        // todo: implement (you may need to add more than just the method signature and body)
    }

    /**
     * Setup called on initialization
     */
    @PostConstruct
    public void setup() throws Exception {

        logger = LoggerFactory.getLogger(this.getClass());

        // add some demonstration data
        // todo: remove this once you implemented persistence
        createImage(new URL("http://via.placeholder.com/640x360")).setFavorite(true);
        for (int id = 1; id <= 12; id++) {
            createImage(new URL("http://via.placeholder.com/640x360"));
        }
    }

    /**
     * Upload and create a new image
     * @param imageData image data string
     * @return created image
     * @throws MalformedURLException
     */
    private Image uploadAndCreateImage(String imageData) throws MalformedURLException {

        // todo: implement

        return createImage(new URL("http://todo"));
    }

    /**
     * Create a new image
     * @param url URL to the image
     * @return created image
     */
    private Image createImage(URL url) {
        Image image = new Image(nextId, url);
        nextId++;
        images.add(image);
        return image;
    }

    /**
     * Searches in the list of all images for the image with the given id
     * @param id id
     * @return image with given id or null
     */
    private Image getImageForGivenId(int id) {
        return images.stream().filter(img -> img.getId() == id).findAny().orElse(null);
    }

}
