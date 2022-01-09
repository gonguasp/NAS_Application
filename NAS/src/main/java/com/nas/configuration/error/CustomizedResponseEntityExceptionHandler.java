package com.nas.configuration.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nas.communicationsecurity.service.JwtService;
import com.nas.communicationsecurity.service.exception.ExpiredTokenException;
import com.nas.communicationsecurity.service.exception.InvalidCredentialsException;
import com.nas.communicationsecurity.service.exception.NotValidTokenException;
import com.nas.configuration.SecurityConfiguration;
import com.nas.persistence.dto.UserDetailsDTO;
import com.nas.persistence.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
@Controller
@RequiredArgsConstructor
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @NonNull
    @Autowired
    private UserRepository userRepository;

    @NonNull
    @Autowired
    private SecurityConfiguration securityConfiguration;

    @ExceptionHandler(Exception.class)
    public final ModelAndView handleGenericException(Exception ex, WebRequest request) {
        return generateModelAndViewErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {JsonProcessingException.class, ExpiredTokenException.class})
    public final ModelAndView handleBadRequestException(Exception ex, WebRequest request) {
        return generateModelAndViewErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidCredentialsException.class, NotValidTokenException.class, UsernameNotFoundException.class})
    public final ModelAndView handleUnauthorizedException(Exception ex, WebRequest request) {
        return generateModelAndViewErrorResponse(ex, request, HttpStatus.UNAUTHORIZED);
    }

    public ModelAndView generateModelAndViewErrorResponse(Exception ex, WebRequest request, HttpStatus httpStatus) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                ex.getMessage(),
                request.getDescription(false),
                httpStatus);
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("exception", exceptionResponse);
        if (securityConfiguration.isLogged()) {
            UserDetails userDetails = securityConfiguration.getLoggedUser();
            mav.addObject("jwt", new JwtService().generateToken((UserDetailsDTO) userDetails));
            mav.addObject("isDarkTheme", userRepository.findByEmail(userDetails.getUsername()).isDarkTheme());
        }
        return mav;
    }
}
