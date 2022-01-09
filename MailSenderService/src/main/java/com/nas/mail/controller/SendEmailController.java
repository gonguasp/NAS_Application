package com.nas.mail.controller;

import com.nas.communicationsecurity.service.JwtService;
import com.nas.communicationsecurity.service.exception.ExpiredTokenException;
import com.nas.communicationsecurity.service.exception.InvalidCredentialsException;
import com.nas.mail.event.PreAuthenticationEvent;
import com.nas.persistence.dto.OperationDTO;
import com.nas.persistence.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequiredArgsConstructor
public class SendEmailController {

    @NonNull
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    @NonNull
    private final JwtService jwtService;

    @Operation(summary = "Sends email to reset password or to finish registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The email was sent"),
            @ApiResponse(responseCode = "400", description = "Operation is not present")})
    @PostMapping("/sendEmail")
    @ResponseBody
    public ResponseEntity<HttpStatus> sendEmail(
            WebRequest request,
            @Parameter(description = "The jwt json", required = true,
                    schema = @Schema(implementation = String.class)) @RequestBody String jwt,
            @Parameter(description = "The possible operations can be REGISTER or RESET_PASSWORD") @RequestParam String operation) throws ExpiredTokenException, InvalidCredentialsException {
        if (!new OperationDTO().isPresent(operation)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        eventPublisher.publishEvent(new PreAuthenticationEvent(new User(jwtService.validateToken(jwt)), OperationDTO.types.valueOf(operation).toString(), request.getLocale()));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}