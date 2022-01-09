package com.nas.controller;

import com.nas.communicationsecurity.service.exception.ExpiredTokenException;
import com.nas.communicationsecurity.service.exception.InvalidCredentialsException;
import com.nas.service.TransferFilesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;

@Controller
@RequiredArgsConstructor
@SecurityRequirement(name = "swagger")
public class DownloadFilesController {
    @Autowired
    @NonNull
    private final TransferFilesService transferFilesService;

    @Operation(summary = "Gets view to download files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the view successfully")})
    @GetMapping("/download")
    public String showDownLoadPage(ModelMap model) throws ExpiredTokenException, InvalidCredentialsException {
        transferFilesService.loadVariablesToView(model, true, new HashMap<>());
        return "fragments/downloadFiles";
    }

    @Operation(summary = "Gets view to download files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the view successfully"),
            @ApiResponse(responseCode = "400", description = "Expired token"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")})
    @GetMapping("/download/{jwt}")
    public String showDownLoadPage(
            ModelMap model,
            @Parameter(description = "The jwt json", required = true) @PathVariable String jwt)
            throws ExpiredTokenException, InvalidCredentialsException {
        transferFilesService.loadVariablesToView(model, true, new HashMap<>(), jwt);
        return "fragments/downloadFiles";
    }

}