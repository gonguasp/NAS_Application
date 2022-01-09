package com.nas.controller;

import com.nas.communicationsecurity.service.exception.ExpiredTokenException;
import com.nas.communicationsecurity.service.exception.InvalidCredentialsException;
import com.nas.configuration.SecurityConfiguration;
import com.nas.service.WelcomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.nas.service.TransferFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@SecurityRequirement(name = "swagger")
public class WelcomeController {

    @NonNull
    @Autowired
    private TransferFilesService transferFilesService;

    @NonNull
    @Autowired
    private WelcomeService welcomeService;

    @NonNull
    @Autowired
    private SecurityConfiguration securityConfiguration;

    @Operation(summary = "Gets view welcome after log in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets the view successfully")})
    @GetMapping("/welcome")
    public String showWelcomePage(
            ModelMap model,
            @Parameter(description = "It can be \"only-fragment\" or different")
            @RequestParam(name = "operation", required = false) String operation) throws ExpiredTokenException, InvalidCredentialsException {
        transferFilesService.loadVariablesToView(model, true, welcomeService.getWelcomeData(securityConfiguration.getLoggedUser()));
        if ("only-fragment".equals(operation)) {
            return "fragments/welcome";
        }
        return "welcome";
    }

}