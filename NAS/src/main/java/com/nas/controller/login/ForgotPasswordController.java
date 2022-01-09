package com.nas.controller.login;

import com.nas.communicationsecurity.service.exception.ExpiredTokenException;
import com.nas.communicationsecurity.service.exception.InvalidCredentialsException;
import com.nas.persistence.dto.EmailDTO;
import com.nas.persistence.dto.ResetPasswordDTO;
import com.nas.service.login.ForgotPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@NoArgsConstructor
public class ForgotPasswordController {

    @NonNull
    @Autowired
    private ForgotPasswordService forgotPasswordService;

    private static final String ENGLISH = "english";
    private static final String LANGUAGE = "language";

    @Operation(summary = "Gets view to the form forgot password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the view successfully")})
    @GetMapping("/forgotPassword")
    public String showForgotPasswordPage(
            ModelMap model,
            @Parameter(description = "It can be \"only-fragment\" or different")
            @RequestParam(name = "operation", required = false) String operation) {
        model.put(LANGUAGE, LocaleContextHolder.getLocale().toString().equals("en") ? ENGLISH : LocaleContextHolder.getLocale().toString());
        if ("only-fragment".equals(operation)) {
            return "fragments/login/forgotPassword";
        }
        return "login/forgotPassword";
    }

    @Operation(summary = "Generates an email with a token to reset the password for a specific email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "An email was sent to reset password"),
            @ApiResponse(responseCode = "400", description = "Operation is not present"),
            @ApiResponse(responseCode = "404", description = "Email not found"),
            @ApiResponse(responseCode = "503", description = "Mail service unavailable")})
    @PostMapping("/forgotPassword")
    @ResponseBody
    public ResponseEntity<HttpStatus> forgotPassword(
            ModelMap model,
            @Parameter(description = "The email", required = true) @RequestBody EmailDTO emailDTO) {
        model.put(LANGUAGE, LocaleContextHolder.getLocale().toString().equals("en") ? ENGLISH : LocaleContextHolder.getLocale().toString());
        return forgotPasswordService.forgotPassword(emailDTO);
    }

    @Operation(summary = "Gets the view to reset the password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the view successfully")})
    @GetMapping("/resetPassword")
    public String showResetPasswordPage(
            Model model,
            @Parameter(description = "The jwt json", required = true) @RequestParam(name = "token") String token)
            throws Exception {
        model.addAttribute(LANGUAGE, LocaleContextHolder.getLocale().toString().equals("en") ? ENGLISH : LocaleContextHolder.getLocale().toString());
        return forgotPasswordService.showResetPasswordPage(model, token);
    }

    @Operation(summary = "Resets a user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Resets the password successfully"),
            @ApiResponse(responseCode = "400", description = "Jwt token is expired"),
            @ApiResponse(responseCode = "401", description = "Passwords are not equal")})
    @PostMapping("/resetPassword")
    @ResponseBody
    public ResponseEntity<HttpStatus> resetPassword(
            ModelMap model,
            @Parameter(description = "The 2 passwords and the jwt token", required = true) @RequestBody ResetPasswordDTO resetPasswordDTO)
            throws ExpiredTokenException, InvalidCredentialsException {
        model.put(LANGUAGE, LocaleContextHolder.getLocale().toString().equals("en") ? ENGLISH : LocaleContextHolder.getLocale().toString());
        return forgotPasswordService.resetUserpassword(resetPasswordDTO);
    }
}