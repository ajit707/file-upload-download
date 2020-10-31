package com.gl.api.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gl.api.dto.FileUploadResponse;
import com.gl.api.service.FileStorageService;

@RestController
public class UploadDownloadWithFileSystemController {

	private FileStorageService fileStorageService;

	public UploadDownloadWithFileSystemController(FileStorageService fileStorageService) {
		this.fileStorageService = fileStorageService;
	}

	@PostMapping("/single/upload")
	public FileUploadResponse singleFileUpload(@RequestParam("file") MultipartFile file) {

		String fileName = fileStorageService.storeFile(file);

		// http://localhost:8081/download/abc.jpg
		String url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/").path(fileName)
				.toUriString();

		String contentType = file.getContentType();

		FileUploadResponse response = new FileUploadResponse(fileName, contentType, url);

		return response;
	}

	@GetMapping("/download/{fileName}")
	public ResponseEntity<Resource> downloadSingleFile(@PathVariable String fileName, HttpServletRequest request) {

		Resource resource = fileStorageService.downloadFile(fileName);

		String mimeType;
		try {
			mimeType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException e) {
			mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + resource.getFilename()).body(resource);
		// .header(HttpHeaders.CONTENT_DISPOSITION, "attached;fileName=" +
		// resource.getFilename()).body(resource);
	}

	@PostMapping("/multiple/upload")
	public List<FileUploadResponse> multipleFileUpload(@RequestParam("files") MultipartFile[] files) {

		List<FileUploadResponse> fileUploadResponseList = new ArrayList<>();

		Arrays.asList(files).stream().forEach(file -> {

			String fileName = fileStorageService.storeFile(file);

			// http://localhost:8081/download/abc.jpg
			String url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/").path(fileName)
					.toUriString();

			String contentType = file.getContentType();

			FileUploadResponse response = new FileUploadResponse(fileName, contentType, url);

			fileUploadResponseList.add(response);

		});

		return fileUploadResponseList;
	}

	@GetMapping("/zipDownload")
	public void zipDownload(@RequestParam("fileName") String[] files, HttpServletResponse response) throws IOException {

		try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {

			Arrays.asList(files).stream().forEach(file -> {

				Resource resource = fileStorageService.downloadFile(file);

				ZipEntry zipEntry = new ZipEntry(resource.getFilename());

				try {
					zipEntry.setSize(resource.contentLength());
					zos.putNextEntry(zipEntry);
					StreamUtils.copy(resource.getInputStream(), zos);
					zos.closeEntry();
				} catch (IOException e) {
					System.out.println("Some exception while zipping");
				}

			});
			
			zos.finish();
		}
	}

}
