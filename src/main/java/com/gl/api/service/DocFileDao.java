package com.gl.api.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gl.api.dto.FileDocument;

@Repository
public interface DocFileDao extends CrudRepository<FileDocument, Long>{

	FileDocument findByFileName(String fileName);

}
