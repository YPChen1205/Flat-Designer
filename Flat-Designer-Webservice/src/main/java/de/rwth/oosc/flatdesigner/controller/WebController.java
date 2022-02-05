package de.rwth.oosc.flatdesigner.controller;

import java.util.List;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import de.rwth.oosc.flatdesigner.api.ImageRepository;
import de.rwth.oosc.flatdesigner.bean.Image;

@Controller
public class WebController {
	
	@Autowired
	private ImageRepository repository;
	
	@GetMapping("/images")
	public String getImages(Model model) {
		List<Image> images = StreamSupport.stream(repository.findAll().spliterator(), false).filter(Image::isApproved).toList();
		model.addAttribute("images", images);
		return "images";
	}
	
	@GetMapping("/delete/{id}")
	public String deleteImage(@PathVariable("id") long id, HttpServletRequest request) {
		repository.deleteById(id);
		String referer = request.getHeader("Referer");
	    return "redirect:"+ referer;
	}
}
