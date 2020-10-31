package com.gl.api.controller;

import org.assertj.core.internal.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.gl.api.service.FileStorageService;

@SpringBootTest
@AutoConfigureMockMvc
public class UploadDownloadWithFileSystemControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FileStorageService fileStorageService;

	@Test
	@DisplayName("test to upload single file")
	void shouldUploadFile() throws Exception {
		Mockito.when(fileStorageService.storeFile(any(MultipartFile.class))).thenReturn("test-file.txt");

		MockMultipartFile mmf = new MockMultipartFile("file", "test-file.txt", "text/plain",
				"Green Learner - Arvind".getBytes());

		this.mockMvc.perform(multipart("/single/upload").file(mmf)).andExpect(status().isOk()).andExpect(content().json(
				"{\"fileName\":test-file.txt,\"contentType\":\"text/plain\",\"url\":\"http://localhost/download/test-file.txt\"}"));

		then(this.fileStorageService).should().storeFile(mmf);
	}
}
