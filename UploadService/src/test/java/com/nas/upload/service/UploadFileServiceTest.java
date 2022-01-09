package com.nas.upload.service;

import com.nas.persistence.model.File;
import com.nas.persistence.model.User;
import com.nas.persistence.repository.FileRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.Assert.*;

class UploadFileServiceTest {

    private static FileRepository fileRepository;

    @BeforeAll
    private static void beforeAll() {
        fileRepository = Mockito.mock(FileRepository.class);
    }

    @Test
    void testUploadFileExistent() {
        UploadFileService controller = new UploadFileService(fileRepository);

        Mockito.when(fileRepository.findByPathAndUser(Mockito.anyString(), Mockito.any())).thenReturn(new File());
        Mockito.when(fileRepository.save(Mockito.any())).thenReturn(new File());
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(multipartFile.getOriginalFilename()).thenReturn("");

        controller.setUploadPath("");
        controller.uploadFile(new User(), "", multipartFile);
    }

    @Test
    void testUploadFileNotExistent() {
        UploadFileService controller = new UploadFileService(fileRepository);
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);

        Mockito.when(fileRepository.findByPathAndUser(Mockito.anyString(), Mockito.any())).thenReturn(null);
        Mockito.when(fileRepository.save(Mockito.any())).thenReturn(new File());
        Mockito.when(multipartFile.getOriginalFilename()).thenReturn("this/is/a/test");

        controller.uploadFile(new User(), "path", multipartFile);
    }

    @Test
    void testUploadFileNotExistentNullMultipartName() {
        UploadFileService controller = new UploadFileService(fileRepository);
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);

        Mockito.when(multipartFile.getOriginalFilename()).thenReturn(null);

        controller.uploadFile(new User(), "path", multipartFile);
    }

    @Test
    @SneakyThrows
    void testUploadFileNotExistentIOException() {
        UploadFileService controller = new UploadFileService(fileRepository);
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);

        Mockito.doThrow(new IOException()).when(multipartFile).transferTo((java.io.File) Mockito.any());

        controller.uploadFile(new User(), "path", multipartFile);
    }

    @Test
    void testGetFoldersList() {
        assertNotNull(new UploadFileService(fileRepository).getFoldersList(1L, "test"));
    }

    @Test
    void testHasSubFoldersTrue() {
        assertTrue(new UploadFileService(fileRepository).hasSubFolders("/"));
    }

    @Test
    void testHasSubFoldersFalse() {
        assertFalse(new UploadFileService(fileRepository).hasSubFolders("test"));
    }

    @Test
    void testCreateNewFolder() {
        assertTrue(!new UploadFileService(fileRepository).createFolder(new User(), "test"));
    }

    @SneakyThrows
    @Test
    void testDeleteDirectory() {
        new java.io.File("null/0this_is_test/test").mkdirs();
        assertTrue(new UploadFileService(fileRepository).deleteFolder(new User(), "this_is_test"));
    }
}
