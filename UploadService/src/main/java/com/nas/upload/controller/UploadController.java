package com.nas.upload.controller;

import com.nas.communicationsecurity.service.JwtService;
import com.nas.communicationsecurity.service.exception.ExpiredTokenException;
import com.nas.communicationsecurity.service.exception.InvalidCredentialsException;
import com.nas.persistence.dto.FolderDTO;
import com.nas.persistence.model.User;
import com.nas.upload.service.UploadFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UploadController {
    @Autowired
    @NonNull
    private final UploadFileService uploadFileService;

    @Autowired
    @NonNull
    private final JwtService jwtService;

    @Operation(summary = "Upload one or more files in a specific path")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The files were uploaded succesfully")})
    @CrossOrigin(origins = "*") //NOSONAR
    @PostMapping(path = "/uploadMultipleFiles", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void uploadMultipleFiles(
            @Parameter(description = "The jwt json", required = true) @RequestPart(name = "jwt") String jwt,
            @Parameter(description = "The path to save the files", required = true) @RequestPart(name = "path") String path,
            @Parameter(description = "The files to be uploaded", required = true) @RequestPart(name = "files") MultipartFile[] files) throws ExpiredTokenException, InvalidCredentialsException {
        User user = new User(jwtService.validateToken(jwt));
        for (MultipartFile multipartFile : files) {
            uploadFileService.uploadFile(user, path, multipartFile);
        }
    }

    @Operation(summary = "Gets the folders inside a folder in order to expand the tree in the client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the folder list")})
    @CrossOrigin(origins = "*") //NOSONAR
    @PostMapping(path = "/getFolderList", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<FolderDTO> getFoldersList(
            @Parameter(description = "The jwt json", required = true) @RequestPart(name = "jwt") String jwt,
            @Parameter(description = "The path to save the files", required = true) @RequestPart(name = "path") String path) throws ExpiredTokenException, InvalidCredentialsException {
        return uploadFileService.getFoldersList(jwtService.validateToken(jwt).getId(), path);
    }

    @Operation(summary = "Creates a folder in the specific path")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Creates new folder")})
    @CrossOrigin(origins = "*") //NOSONAR
    @PostMapping(path = "/folder", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public boolean createNewFolder(
            @Parameter(description = "The jwt json", required = true) @RequestPart(name = "jwt") String jwt,
            @Parameter(description = "The path to save the files", required = true) @RequestPart(name = "path") String path) throws ExpiredTokenException, InvalidCredentialsException {
        return uploadFileService.createFolder(new User(jwtService.validateToken(jwt)), path);
    }

    @Operation(summary = "Deletes a specific path")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deletes a folder")})
    @CrossOrigin(origins = "*") //NOSONAR
    @DeleteMapping(path = "/folder", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public boolean deleteFolder(
            @Parameter(description = "The jwt json", required = true) @RequestPart(name = "jwt") String jwt,
            @Parameter(description = "The path to save the files", required = true) @RequestPart(name = "path") String path) throws IOException, ExpiredTokenException, InvalidCredentialsException {
        return uploadFileService.deleteFolder(new User(jwtService.validateToken(jwt)), path);
    }

}