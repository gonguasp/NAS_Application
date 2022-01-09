package com.nas.download.controller;

import com.nas.communicationsecurity.service.JwtService;
import com.nas.communicationsecurity.service.exception.ExpiredTokenException;
import com.nas.communicationsecurity.service.exception.InvalidCredentialsException;
import com.nas.download.service.DownloadFileService;
import com.nas.persistence.dto.FileDTO;
import com.nas.persistence.dto.UserDetailsDTO;
import com.nas.persistence.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DownloadController {

    @Autowired
    private final DownloadFileService downloadFileService;

    @Autowired
    private final JwtService jwtService;

    @Operation(summary = "Gets the folders inside a folder in order to expand the tree in the client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the folder list")})
    @CrossOrigin(origins = "*") //NOSONAR
    @PostMapping(path = "/getFileList", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<FileDTO> getFileList(
            @Parameter(description = "The jwt json", required = true) @RequestPart(name = "jwt") String jwt,
            @Parameter(description = "The path to get the files", required = true) @RequestPart(name = "path", required = false) String path) throws ExpiredTokenException, InvalidCredentialsException {
        return downloadFileService.getFileList(jwtService.validateToken(jwt).getId(), path);
    }

    @Operation(summary = "Zip all files and download it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Downloads the zip file")})
    @CrossOrigin(origins = "*") //NOSONAR
    @GetMapping(value = "/download/{jwt}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StreamingResponseBody> zipDownload(
            final HttpServletResponse response,
            @Parameter(description = "The jwt json", required = true) @PathVariable String jwt) throws ExpiredTokenException, InvalidCredentialsException {
        return new ResponseEntity<>(
                downloadFileService.downloadZipFile(
                        response, jwtService.validateToken(jwt).getId(),
                        (List<String>) (jwtService.extractClaim(jwt, "pathFiles")),
                        (String) (jwtService.extractClaim(jwt, "parentFolder"))),
                HttpStatus.OK);
    }

    @Operation(summary = "Downloads a single file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Downloads the file")})
    @CrossOrigin(origins = "*") //NOSONAR
    @GetMapping(value = "/download/{jwt}/{fileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StreamingResponseBody> downloadFile(
            final HttpServletResponse response,
            @Parameter(description = "The jwt json", required = true) @PathVariable String jwt,
            @Parameter(description = "File id", required = true) @PathVariable Long fileId) throws ExpiredTokenException, InvalidCredentialsException, FileNotFoundException {
        jwtService.validateToken(jwt);
        return new ResponseEntity<>(downloadFileService.downloadFile(response, fileId), HttpStatus.OK);
    }

    @Operation(summary = "Gets the jwt to share specific files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a jwt which contains the files to download")})
    @CrossOrigin(origins = "*") //NOSONAR
    @PostMapping(value = "/shareFiles")
    public String shareFiles(
            @Parameter(description = "The jwt json", required = true) @RequestPart(name = "jwt") String jwt,
            @Parameter(description = "Files id", required = true) @RequestPart(name = "files") String[] files,
            @Parameter(description = "Parent folder", required = true) @RequestPart(name = "parentFolder") String parentFolder) throws ExpiredTokenException, InvalidCredentialsException {
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setId(jwtService.validateToken(jwt).getId());
        return jwtService.generateToken(userDetailsDTO, files, parentFolder);
    }

    @Operation(summary = "Delete files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete files")})
    @CrossOrigin(origins = "*") //NOSONAR
    @DeleteMapping(value = "/deleteFiles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StreamingResponseBody> deleteFiles(
            @Parameter(description = "The jwt json", required = true) @RequestPart String jwt) throws IOException, ExpiredTokenException, InvalidCredentialsException {
        downloadFileService.deleteFiles(new User(jwtService.validateToken(jwt)), (List<String>) (jwtService.extractClaim(jwt, "pathFiles")));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Modifies folder/file name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder/file name modified")})
    @CrossOrigin(origins = "*") //NOSONAR
    @PutMapping(value = "/changeName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean changeName(
            @Parameter(description = "The jwt json", required = true) @RequestPart String jwt,
            @Parameter(description = "The path where the folder/file is", required = true) @RequestPart String path,
            @Parameter(description = "The old name", required = true) @RequestPart String oldName,
            @Parameter(description = "The new name", required = true) @RequestPart String newName) throws ExpiredTokenException, InvalidCredentialsException {
        return downloadFileService.changeName(new User(jwtService.validateToken(jwt)), path, oldName, newName);
    }

}
