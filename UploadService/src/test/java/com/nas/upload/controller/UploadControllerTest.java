package com.nas.upload.controller;

import com.nas.communicationsecurity.service.JwtService;
import com.nas.persistence.model.UserView;
import com.nas.upload.service.UploadFileService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

class UploadControllerTest {

    private static UploadFileService uploadFileService;
    private static JwtService jwtService;

    @BeforeAll
    private static void beforeAll() {
        uploadFileService = Mockito.mock(UploadFileService.class);
        jwtService = Mockito.mock(JwtService.class);
    }

    @SneakyThrows
    @Test
    void testUploadMultipleFiles() {
        UploadController controller = new UploadController(uploadFileService, jwtService);

        MultipartFile[] files = new MultipartFile[2];
        files[0] = Mockito.mock(MultipartFile.class);
        files[1] = Mockito.mock(MultipartFile.class);

        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(new UserView());
        Mockito.doNothing().when(uploadFileService).uploadFile(Mockito.any(), Mockito.anyString(), Mockito.any());
        controller.uploadMultipleFiles("jwt", "", files);
    }

    @Test
    @SneakyThrows
    void testGetFoldersList() {
        UploadController controller = new UploadController(uploadFileService, jwtService);

        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(new UserView());
        Mockito.when(uploadFileService.getFoldersList(Mockito.any(), Mockito.anyString())).thenReturn(new ArrayList<>());
        assertNotNull(controller.getFoldersList("jwt", "path"));
    }

    @Test
    @SneakyThrows
    void testCreateNewFolder() {
        UploadController controller = new UploadController(uploadFileService, jwtService);

        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(new UserView());
        Mockito.when(uploadFileService.createFolder(Mockito.any(), Mockito.anyString())).thenReturn(true);
        Mockito.when(uploadFileService.createFolder(Mockito.any(), Mockito.anyString())).thenReturn(true);
        controller.createNewFolder("jwt", "path");
    }

    @Test
    @SneakyThrows
    void testDeleteFolder() {
        UploadController controller = new UploadController(uploadFileService, jwtService);

        Mockito.when(jwtService.validateToken(Mockito.anyString())).thenReturn(new UserView());
        Mockito.when(uploadFileService.deleteFolder(Mockito.any(), Mockito.anyString())).thenReturn(true);
        controller.deleteFolder("jwt", "path");
    }

}
