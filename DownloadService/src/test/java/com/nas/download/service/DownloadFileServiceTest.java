package com.nas.download.service;

import com.nas.persistence.repository.FileRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.zip.ZipOutputStream;



class DownloadFileServiceTest {

    private static DownloadFileService downloadFileService;
    private static FileRepository fileRepository;

    @BeforeAll
    static void beforeAll() {
        fileRepository = Mockito.mock(FileRepository.class);
        downloadFileService = new DownloadFileService();
        downloadFileService.setUploadPath("/");
        downloadFileService.setFileRepository(fileRepository);
    }

    @Test
    void composeFileListTest() {
        Mockito.when(fileRepository.findByPath(Mockito.anyString())).thenReturn(new com.nas.persistence.model.File());

        File file = Mockito.mock(File.class);
        Mockito.when(file.isDirectory()).thenReturn(true);
        Mockito.when(file.toPath()).thenReturn(Path.of(""));
        downloadFileService.composeFileList(file, "", new ArrayList<>());

        Mockito.when(file.isDirectory()).thenReturn(false);
        downloadFileService.composeFileList(file, "", new ArrayList<>());
    }

    @Test
    @SneakyThrows
    void zipFileTest() {
        Mockito.when(fileRepository.findByPath(Mockito.anyString())).thenReturn(new com.nas.persistence.model.File());

        File file = Mockito.mock(File.class);
        ZipOutputStream zipOutputStream = Mockito.mock(ZipOutputStream.class);
        Mockito.when(file.isHidden()).thenReturn(true);

        downloadFileService.zipFile(file, "", zipOutputStream);

        Mockito.when(file.isDirectory()).thenReturn(true);
        File[] files = new File[1];
        files[0] = new File("");
        Mockito.when(file.listFiles()).thenReturn(files);
        Mockito.when(file.isHidden()).thenReturn(false);

        downloadFileService.zipFile(file, "/", zipOutputStream);
        downloadFileService.zipFile(file, "", zipOutputStream);
    }
}
