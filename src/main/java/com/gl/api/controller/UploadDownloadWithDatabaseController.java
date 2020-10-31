package com.gl.api.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gl.api.dto.FileDocument;
import com.gl.api.dto.FileUploadResponse;
import com.gl.api.service.DocFileDao;

@RestController
public class UploadDownloadWithDatabaseController {

	private DocFileDao docFileDao;

	public UploadDownloadWithDatabaseController(DocFileDao docFileDao) {
		this.docFileDao = docFileDao;
	}

	@PostMapping("/single/uploadDB")
	public FileUploadResponse singleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {

		FileDocument fileDocument = new FileDocument();
		String name = StringUtils.cleanPath(file.getOriginalFilename());

		fileDocument.setFileName(name);
		fileDocument.setDocFile(file.getBytes());

		docFileDao.save(fileDocument);

		// http://localhost:8081/download/abc.jpg
		String url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFromDB/").path(name)
				.toUriString();

		String contentType = file.getContentType();

		FileUploadResponse response = new FileUploadResponse(name, contentType, url);

		return response;
	}

	@GetMapping("/downloadFromDB/{fileName}")
	public ResponseEntity<byte[]> downloadSingleFile(@PathVariable String fileName, HttpServletRequest request) {

		FileDocument doc = docFileDao.findByFileName(fileName);

		String mimeType = request.getServletContext().getMimeType(doc.getFileName());

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + doc.getFileName()).body(doc.getDocFile());
		// .header(HttpHeaders.CONTENT_DISPOSITION, "attached;fileName=" +
		// resource.getFilename()).body(resource);
	}

}
