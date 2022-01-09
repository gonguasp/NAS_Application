package com.nas.controller;

import com.nas.service.TransferFilesService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ModelMap;

import static org.junit.Assert.assertEquals;

class DownloadFilesControllerTest {

    @SneakyThrows
    @Test
    void testShowForgotPasswordPageOnlyFragment() {
        TransferFilesService uploadFilesService = Mockito.mock(TransferFilesService.class);
        DownloadFilesController controller = new DownloadFilesController(uploadFilesService);

        Mockito.doNothing().when(uploadFilesService).loadVariablesToView(Mockito.any(), Mockito.any(), Mockito.any());

        assertEquals("fragments/downloadFiles", controller.showDownLoadPage(Mockito.mock(ModelMap.class)));
    }

    @SneakyThrows
    @Test
    void testShowForgotPasswordPageOnlyFragmentOverload() {
        TransferFilesService uploadFilesService = Mockito.mock(TransferFilesService.class);
        DownloadFilesController controller = new DownloadFilesController(uploadFilesService);

        Mockito.doNothing().when(uploadFilesService).loadVariablesToView(Mockito.any(), Mockito.any(), Mockito.any());

        assertEquals("fragments/downloadFiles", controller.showDownLoadPage(Mockito.mock(ModelMap.class), ""));
    }
}
