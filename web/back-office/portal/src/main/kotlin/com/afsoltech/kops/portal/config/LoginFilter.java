//package com.nanobnk.epayment.portal.config;
//
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//
//@Component
//@WebFilter
//public class LoginFilter implements Filter {
//
//    /**
//     * @see Filter#init(FilterConfig)
//     */
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {}
//
//    /**
//     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
//     */
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) res;
//
//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("loggedInUser") == null) {
//            response.sendRedirect(request.getContextPath() + "/login");
//        } else {
//            chain.doFilter(request, response);
//        }
//    }
//
//
//    /**
//     * @see Filter#destroy()
//     */
//    @Override
//    public void destroy() {}
//}
