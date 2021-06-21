package com.bitsvaley.micro.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * The CustomSuccessHandler provides a handle after a successful login
 *
 * @author  Fru Chifen
 * @version 1.0
 * @since   2021-06-20
 */
public class CustomSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request,
                                        final HttpServletResponse response, final Authentication authentication)
        throws IOException, ServletException {
        HttpSession session = request.getSession(true);

        System.out.println("------------LOGIN SUCCESS -----------------");
        System.out.println("------------              -----------------");
        System.out.println("------------LOGIN SUCCESS -----------------");
        System.out.println("------------              -----------------");
        System.out.println("------------LOGIN SUCCESS -----------------");
        System.out.println("------------              -----------------");
        System.out.println("------------LOGIN SUCCESS -----------------");
        System.out.println("------------              -----------------");
        System.out.println("------------LOGIN SUCCESS -----------------");

    }

}
