///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package com.nanobnk.epayment.portal.controller;
//
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.NoHandlerFoundException;
//import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
//
///**
// *
// * @author tchipi
// */
//
//public class CustomHandlerExceptionResolver   extends   DefaultHandlerExceptionResolver{
//
//      private static final Logger logger = Logger.getLogger(CustomHandlerExceptionResolver.class.getName());
//    @Override
//    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
//			Object handler, Exception ex) {
//      ModelAndView mav=  new ModelAndView();  //super.doResolveException(request, response, handler, ex);
// logger.log(Level.SEVERE, "L'exception "+ex.getMessage()+" a été levée", ex);
//      if ((ex instanceof NotFoundException)  ||  (ex instanceof NoHandlerFoundException))
//        {
//            mav.addObject("codeerrror", 404);
//            mav.addObject("errormessage", " <h3><i class=\"fa fa-warning text-yellow\"></i> Oops! Page not found.</h3>\n" +
//"                            <p>\n" +
//"                                We could not find the page you were looking for. \n" +
//"                               The page you are looking for might have been removed, had its name changed, or unavailable.\n" +
//"                            </p>");
//        }
//        else
//             if (ex instanceof  AccessDeniedException)
//             {
//             mav.addObject("codeerrror", 403);
//            mav.addObject("errormessage", "<h3><i class=\"fa fa-warning text-yellow\"></i> Oops! Access Denied.</h3>\n" +
//"                            \n" +
//"             <p>Please contact administrator on ....</p>\n" +
//"             ");
//             }
//             else
//             if(ex instanceof  BusinessException){
//
//               mav.addObject("codeerrror", 503);
//            mav.addObject("errormessage", "<h3><i class=\"fa fa-warning text-yellow\"></i> Oops! Something went wrong.</h3>\n" +
//"                            \n" +
//"             <p>"+ex.getMessage()+"</p>\n" +
//"             ");
//
//
//             }
//             else
//             {
//             mav.addObject("codeerrror", 500);
//            mav.addObject("errormessage", "<h3><i class=\"fa fa-warning text-yellow\"></i> Oops! Something went wrong.</h3>\n" +
//"                            \n" +
//"                             <p>\n" +
//"                                 Application has encountered an error.</p> \n" +
//"                                  \n" +
//"             <p>Please contact support on ...Support may ask you to right click to view page source.</p>\n" +
//"             ");
//             }
//        mav.addObject("ex", ex);
//        mav.addObject("categorie", "ERROR");
//        mav.setViewName("error");
//      return mav;
//
//    }
//
//}
