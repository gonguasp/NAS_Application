package com.nas.download.controller;

import com.nas.communicationsecurity.service.JwtService;
import com.nas.download.service.DownloadFileService;
import com.nas.persistence.model.File;
import com.nas.persistence.model.UserView;
import com.nas.persistence.repository.FileRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DownloadControllerTest {

    private static DownloadFileService downloadFileService;
    private static JwtService jwtService;
    private static FileRepository fileRepository;

    @BeforeAll
    static void beforeAll() {
        jwtService = Mockito.mock(JwtService.class);
        fileRepository = Mockito.mock(FileRepository.class);
        downloadFileService = new DownloadFileService();
        downloadFileService.setUploadPath("/");
        downloadFileService.setFileRepository(fileRepository);
    }

    @Test
    @SneakyThrows
    void getFileListTest() {
        DownloadController downloadController = new DownloadController(downloadFileService, jwtService);

        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(new UserView());

        assertNotNull(downloadController.getFileList("", ""));
    }

    @Test
    @SneakyThrows
    void zipDownloadTest() {
        DownloadController downloadController = new DownloadController(downloadFileService, jwtService);

        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(new UserView());
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.doNothing().when(response).setHeader(Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(response).setContentType(Mockito.anyString());
        ServletOutputStream servletOutputStream = Mockito.mock(ServletOutputStream.class);
        Mockito.when(response.getOutputStream()).thenReturn(servletOutputStream);

        List<String> files = new ArrayList<>();
        files.add("");
        Mockito.when(jwtService.extractClaim(Mockito.anyString(), Mockito.startsWith("pathFiles"))).thenReturn(files);
        Mockito.when(jwtService.extractClaim(Mockito.anyString(), Mockito.startsWith("parentFolder"))).thenReturn("/");

        OutputStream outputStream = Mockito.mock(OutputStream.class);
        downloadController.zipDownload(response, "").getBody().writeTo(outputStream);

        assertEquals(HttpStatus.OK, downloadController.zipDownload(response, "").getStatusCode());
    }

    @Test
    @SneakyThrows
    void downloadFileTest() {
        DownloadController downloadController = new DownloadController(downloadFileService, jwtService);
        HttpServletResponse response = Mockito.mock(HttpServletResponse .class);

        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(new UserView());

        assertThrows(FileNotFoundException.class, ()->
            downloadController.downloadFile(response, "", 0L)
        );

        File file = new File();
        file.setPath("");
        Mockito.when(fileRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(file));
        Mockito.doNothing().when(response).setHeader(Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(response).setContentType(Mockito.anyString());

        OutputStream outputStream = Mockito.mock(OutputStream.class);
        downloadController.downloadFile(response, "", 0L).getBody().writeTo(outputStream);
        assertEquals(HttpStatus.OK, downloadController.downloadFile(response, "", 0L).getStatusCode());
    }

    @Test
    @SneakyThrows
    void shareFilesTest() {
        DownloadController downloadController = new DownloadController(downloadFileService, jwtService);

        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(new UserView());
        Mockito.when(jwtService.generateToken(Mockito.any(), Mockito.any(), Mockito.anyString())).thenReturn("");

        assertNotNull(downloadController.shareFiles("", new String[0], ""));
    }

    @Test
    @SneakyThrows
    void deleteFilesTest() {
        DownloadController downloadController = new DownloadController(downloadFileService, jwtService);

        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(new UserView());
        List<String> files = new ArrayList<>();
        files.add("1");
        files.add("/");
        files.add("\\");
        Mockito.when(jwtService.extractClaim(Mockito.anyString(), Mockito.anyString())).thenReturn(files);
        Mockito.doNothing().when(fileRepository).deleteByUserAndPathContaining(Mockito.any(), Mockito.anyString());

        assertEquals(HttpStatus.OK, downloadController.deleteFiles("").getStatusCode());
    }

    @Test
    @SneakyThrows
    void changeNameTest() {
        DownloadController downloadController = new DownloadController(downloadFileService, jwtService);

        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(new UserView());

        assertTrue(!downloadController.changeName("", "", "", ""));

        Mockito.when(fileRepository.findByPathAndUser(Mockito.anyString(), Mockito.any())).thenReturn(null);
        File file = new File();
        file.setPath("");
        Mockito.when(fileRepository.findByPathAndUser(Mockito.startsWith("//0/"), Mockito.any())).thenReturn(file);
        Mockito.when(fileRepository.save(Mockito.any())).thenReturn(new File());
        List<File> files = new ArrayList<>();
        files.add(file);
        File file2 = new File();
        file2.setPath("//0g");
        files.add(file2);
        File file3 = new File();
        file3.setPath("//0g/");
        files.add(file3);
        Mockito.when(fileRepository.findAllByUserAndPathContaining(Mockito.any(), Mockito.anyString())).thenReturn(files);

        assertTrue(!downloadController.changeName("", "", "", "g"));

        Mockito.when(fileRepository.findByPathAndUser(Mockito.anyString(), Mockito.any())).thenReturn(file);
        assertTrue(!downloadController.changeName("", "", "", "g"));
    }
}
