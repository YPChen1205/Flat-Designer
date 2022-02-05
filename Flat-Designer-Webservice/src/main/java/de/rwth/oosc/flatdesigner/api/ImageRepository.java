package de.rwth.oosc.flatdesigner.api;

import org.springframework.data.repository.CrudRepository;

import de.rwth.oosc.flatdesigner.bean.Image;

public interface ImageRepository extends CrudRepository<Image, Long>{

	Image findById(long id);
}
