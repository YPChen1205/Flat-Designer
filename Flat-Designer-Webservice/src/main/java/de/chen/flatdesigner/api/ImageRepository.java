package de.chen.flatdesigner.api;

import org.springframework.data.repository.CrudRepository;

import de.chen.flatdesigner.bean.Image;

public interface ImageRepository extends CrudRepository<Image, Long>{

	Image findById(long id);
}
