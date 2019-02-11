package com.botmasterzzz.auth.listener;

import com.botmasterzzz.auth.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(final HttpServletRequest request,
                       final HttpServletResponse response,
                       final AccessDeniedException accessDeniedException) throws IOException, ServletException {
        final CustomException customException = new CustomException(HttpServletResponse.SC_FORBIDDEN, "ERROR", accessDeniedException.getMessage()) ;
        customException.setMessage(accessDeniedException.getMessage()) ;
        customException.setPath(request.getServletPath()) ;

        response.setContentType(MediaType.APPLICATION_JSON_VALUE) ;
        response.setStatus(HttpServletResponse.SC_FORBIDDEN) ;

        final ObjectMapper mapper = new ObjectMapper() ;
        mapper.writeValue(response.getOutputStream(), customException.getMessage()) ;
    }
}
