package com.nas.controller.login;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Operation(summary = "Gets view to log in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the view successfully")})
    @GetMapping("/login")
    public String showLoginPage(
            ModelMap model,
            @Parameter(description = "It can be \"only-fragment\" or different")
            @RequestParam(name = "operation", required = false) String operation) {
        model.put("language", LocaleContextHolder.getLocale().toString().equals("en") ? "english" : LocaleContextHolder.getLocale().toString());
        if ("only-fragment".equals(operation)) {
            return "fragments/login/login";
        }
        return "login/login";
    }
}