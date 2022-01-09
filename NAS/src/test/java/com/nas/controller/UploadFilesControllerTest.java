package com.nas.controller;

import com.nas.service.TransferFilesService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

class UploadFilesControllerTest {

    @SneakyThrows
    @Test
    void testShowForgotPasswordPageOnlyFragment() {
        TransferFilesService uploadFilesService = Mockito.mock(TransferFilesService.class);
        UploadFilesController controller = new UploadFilesController(uploadFilesService);

        Mockito.doNothing().when(uploadFilesService).loadVariablesToView(Mockito.any(), Mockito.any(), Mockito.any());

        assertEquals("fragments/uploadFiles", controller.showUploadPage());
    }
}
