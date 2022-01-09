package com.nas.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nas.communicationsecurity.service.exception.ExpiredTokenException;
import com.nas.communicationsecurity.service.exception.InvalidCredentialsException;
import com.nas.persistence.model.UserView;
import com.nas.service.SettingsService;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


@Controller
@RequiredArgsConstructor
@SecurityRequirement(name = "swagger")
public class SettingsController {

    @NonNull
    @Autowired
    private SettingsService settingsService;

    @NonNull
    @Autowired
    private TransferFilesService transferFilesService;

    @Operation(summary = "Gets settings view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the view successfully")})
    @GetMapping("/settings")
    public String showUploadPage(ModelMap model) throws ExpiredTokenException, InvalidCredentialsException {
        transferFilesService.loadVariablesToView(model, true, new HashMap<>());
        return "fragments/settings";
    }

    @Operation(summary = "Gets the users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the users successfully"),
            @ApiResponse(responseCode = "400", description = "Token expired"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")})
    @ResponseBody
    @GetMapping("/settings/users/{jwt}")
    public List<UserView> showUploadPage(@Parameter(description = "The jwt json", required = true) @PathVariable String jwt)
            throws ExpiredTokenException, InvalidCredentialsException {
        return settingsService.showUploadPage(jwt);
    }

    @Operation(summary = "Get user files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get the user files"),
            @ApiResponse(responseCode = "400", description = "Token expired"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")})
    @ResponseBody
    @PostMapping("/settings/users/{jwt}/{email}")
    public String getUserJwt(
            @Parameter(description = "The jwt json", required = true) @PathVariable String jwt,
            @Parameter(description = "The user email", required = true) @PathVariable String email)
            throws ExpiredTokenException, InvalidCredentialsException {
        return settingsService.generateToken(jwt, email);
    }

    @Operation(summary = "Delete the users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the users successfully"),
            @ApiResponse(responseCode = "400", description = "Token expired or users field malformed"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")})
    @ResponseBody
    @DeleteMapping("/settings/users")
    public void deleteUsers(
            @Parameter(description = "The jwt json", required = true) @RequestPart String jwt,
            @Parameter(description = "The users to be deleted", required = true) @RequestPart String users)
            throws JsonProcessingException, ExpiredTokenException, InvalidCredentialsException {
        settingsService.deleteUsers(jwt, users);
    }

    @Operation(summary = "Reset users password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the users successfully"),
            @ApiResponse(responseCode = "400", description = "Token expired or users field malformed"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")})
    @ResponseBody
    @PatchMapping("/settings/users")
    public void resetUsersPassword(
            @Parameter(description = "The jwt json", required = true) @RequestPart String jwt,
            @Parameter(description = "The users to be deleted", required = true) @RequestPart String users)
            throws JsonProcessingException, ExpiredTokenException, InvalidCredentialsException {
        settingsService.resetUsersPassword(jwt, users);
    }

    @Operation(summary = "Update the user theme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the user theme successfully")})
    @ResponseBody
    @PostMapping("/changeTheme")
    public void changeUserTheme(
            @Parameter(description = "The jwt json", required = true) @RequestPart String jwt,
            @Parameter(description = "The theme", required = true) @RequestPart String isDarkTheme)
            throws ExpiredTokenException, InvalidCredentialsException {
        settingsService.updateTheme(jwt, Boolean.valueOf(isDarkTheme));
    }

}