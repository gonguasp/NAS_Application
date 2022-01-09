package com.nas.controller;

import com.nas.service.TransferFilesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
@SecurityRequirement(name = "swagger")
public class UploadFilesController {

    @NonNull
    @Autowired
    private TransferFilesService transferFilesService;

    @Operation(summary = "Gets view to upload files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the view successfully")})
    @GetMapping("/upload")
    public String showUploadPage() {
        return "fragments/uploadFiles";
    }

}