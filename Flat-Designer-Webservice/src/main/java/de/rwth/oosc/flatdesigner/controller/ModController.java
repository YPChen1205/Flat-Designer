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
public class ModController {

	@Autowired
	private ImageRepository repository;
	
	@GetMapping("/mod/images")
	public String getImages(Model model) {
		List<Image> images = StreamSupport.stream(repository.findAll().spliterator(), false).toList();
		model.addAttribute("images", images);
		model.addAttribute("isMod", true);
		return "images";
	}
	
	@GetMapping("/mod/images/{id}/approve")
	public String approveImage(@PathVariable("id") long id, HttpServletRequest request) {
		Image image = repository.findById(id);
		image.setApproved(true);
		repository.save(image);
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}
	
	@GetMapping("/mod/images/{id}/deny")
	public String denyImage(@PathVariable("id") long id, HttpServletRequest request) {
		Image image = repository.findById(id);
		image.setApproved(false);
		repository.save(image);
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}
}
