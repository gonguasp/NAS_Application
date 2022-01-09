package com.nas.controller.login;

import com.nas.persistence.dto.RegisterDTO;
import com.nas.service.login.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
public class RegisterController {

    @NonNull
    @Autowired
    private RegisterService registerService;

    private static final String ENGLISH = "english";
    private static final String LANGUAGE = "language";

    @Operation(summary = "Gets view to register a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the view successfully")})
    @GetMapping("/register")
    public String showRegisterPage(
            ModelMap model,
            @Parameter(description = "It can be \"only-fragment\" or different")
            @RequestParam(name = "operation", required = false) String operation) {
        model.put(LANGUAGE, LocaleContextHolder.getLocale().toString().equals("en") ? ENGLISH : LocaleContextHolder.getLocale().toString());
        if ("only-fragment".equals(operation)) {
            return "fragments/login/register";
        }
        return "login/register";
    }

    @Operation(summary = "Register a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The email was sent"),
            @ApiResponse(responseCode = "400", description = "Operation is not present"),
            @ApiResponse(responseCode = "404", description = "Email not found"),
            @ApiResponse(responseCode = "409", description = "Passwords are not equal")})
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<HttpStatus> registerUser(
            ModelMap model,
            @Parameter(description = "User data to register", required = true) @RequestBody RegisterDTO registerDTO) {
        model.put(LANGUAGE, LocaleContextHolder.getLocale().toString().equals("en") ? ENGLISH : LocaleContextHolder.getLocale().toString());
        return registerService.registerUser(registerDTO);
    }

    @Operation(summary = "Final step to activate an account. It gets the view register confirmation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the view successfully")})
    @GetMapping("/registerConfirm")
    public String activateAccount(
            ModelMap model,
            @Parameter(description = "The jwt token", required = true) @RequestParam(name = "token") String token) throws Exception {
        model.put(LANGUAGE, LocaleContextHolder.getLocale().toString().equals("en") ? ENGLISH : LocaleContextHolder.getLocale().toString());
        return registerService.activateAccount(token);
    }
}