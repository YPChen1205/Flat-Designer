package de.rwth.oosc.flatdesigner.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

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
    @Autowired
    private ImageRepository imageRepository;

    /**
     * Get all images in the standard representation format defined by the Domain Entities
     **/
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Image> getAllImages() {
    	List<Image> images = StreamSupport.stream(imageRepository.findAll().spliterator(), false).toList();
        logger.info("Sending {} Images", images.size());
        
        return images;
    }

    /**
     * Gets a single image for a given id
     * @param id id
     * @return Image object or error 404 not found if no image exists for the given id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Image getImageRequest(@PathVariable("id") long id) {

        Image image = getImageForGivenId(id) ;
        if(image != null) {
            return image;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found.");
        }
    }
    
    @HystrixCommand(fallbackMethod = "fallback")
    @GetMapping("/{id}/show")
    public void showImage(@PathVariable("id") long id, HttpServletResponse response) throws IOException {
    	
		Image image = imageRepository.findById(id);
    	response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
		response.getOutputStream().write(image.getImageData());
		response.getOutputStream().close();
		
    }

    @PostMapping("/create")
    public Image createImageRequest(@RequestPart("image") Part image) {
    	try {
			return createImage(image.getInputStream().readAllBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
    }

    /**
     * Create a new image
     * @param url URL to the image
     * @return created image
     * @throws MalformedURLException 
     */
    @HystrixCommand(fallbackMethod = "fallback")
    private Image createImage(byte[] imageData) throws MalformedURLException {
    	
    	Image image = new Image();
        
       
    	image.setImageData(imageData);
        imageRepository.save(image);
		image.setUrl(new URL("http://localhost:8080/api/images/" + image.getId() + "/show"));
		imageRepository.save(image);
        
        return image;
    }

    /**
     * Searches in the list of all images for the image with the given id
     * @param id id
     * @return image with given id or null
     */
    private Image getImageForGivenId(long id) {
    	return imageRepository.findById(id);
    }

    @SuppressWarnings("unused")
	private String fallback() {
    	return "An error occured in the service";
    }
}
