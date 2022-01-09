package com.nas.configuration.error;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class CustomErrorController implements ErrorController {

    @NonNull
    @Autowired
    private CustomizedResponseEntityExceptionHandler customizedResponseEntityExceptionHandler;

    @RequestMapping("/error") // NOSONAR
    public ModelAndView handleError(WebRequest webRequest, HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        Exception ex = new Exception("Internal server error.");

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                httpStatus = HttpStatus.NOT_FOUND;
                ex = new Exception("Not found.");
            }
        }

        return customizedResponseEntityExceptionHandler.generateModelAndViewErrorResponse(ex, webRequest, httpStatus);
    }
}