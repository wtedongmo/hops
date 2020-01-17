//package com.afsoltech.hops.portal.config
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//
//import com.afsoltech.core.entity.SessionLog
//import com.afsoltech.core.repository.SessionLogRepository
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Component
//import java.io.IOException
//import javax.servlet.FilterChain
//import javax.servlet.ServletException
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
//import org.springframework.web.filter.OncePerRequestFilter
//import org.springframework.web.util.ContentCachingRequestWrapper
//import org.springframework.web.util.ContentCachingResponseWrapper
//import java.nio.charset.Charset
//import java.time.LocalDateTime
//import java.time.LocalTime
//
//@Component
//class PortalInterceptorLogger(val sessionLogRepository: SessionLogRepository) : OncePerRequestFilter() {
//
//    private val includeResponsePayload = true
//    private val maxPayloadLength = 1000
//
////    @Autowired
////    lateinit
//
//    private fun getContentAsString(buf: ByteArray?, maxLength: Int, charsetName: String): String {
//        if (buf == null || buf.size == 0) return ""
//        val length = Math.min(buf.size, this.maxPayloadLength)
//        try {
//            return java.lang.String(buf, 0, length, charsetName).toString()
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//            return "Unsupported Encoding"
//        }
//
//    }
//
//    /**
//     * Log each request and respponse with full Request URI, content payload and duration of the request in ms.
//     * @param request the request
//     * @param response the response
//     * @param filterChain chain of filters
//     * @throws ServletException
//     * @throws IOException
//     */
//    @Throws(ServletException::class, IOException::class)
//    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
//
//        val start = LocalDateTime.now()
//        val startTime = System.currentTimeMillis()
//        val sessionLog = SessionLog()
//        sessionLog.startTime = LocalTime.now()
//        sessionLog.method = request.method
//        sessionLog.requestUrl = request.requestURL.toString()
//        sessionLog.host = request.getRemoteHost()
//
////        val reqInfo = StringBuffer()
////                .append("[")
////                .append(startTime % 10000)  // request ID
////                .append("] ")
////                .append(request.method)
////                .append(" ")
////                .append(request.requestURL)
//
//        val queryString = request.queryString
//        if (queryString != null) {
////            reqInfo.append("?").append(queryString)
//            sessionLog.query = queryString
//        }
//
//        if (request.authType != null) {
////            reqInfo.append(", authType=")
////                    .append(request.authType)
//            sessionLog.authType = request.authType
//        }
//        if (request.userPrincipal != null) {
////            reqInfo.append(", principalName=")
////                    .append(request.userPrincipal.name)
//            sessionLog.username = request.userPrincipal.name
//        }
//
////        this.logger.debug("=> $reqInfo")
//
//        // ========= Log request and response payload ("body") ========
//        // We CANNOT simply read the request payload here, because then the InputStream would be consumed and cannot be read again by the actual processing/server.
//        //    String reqBody = DoogiesUtil._stream2String(request.getInputStream());   // THIS WOULD NOT WORK!
//        // So we need to apply some stronger magic here :-)
//        val wrappedRequest = ContentCachingRequestWrapper(request)
//        val wrappedResponse = ContentCachingResponseWrapper(response)
//
//        filterChain.doFilter(wrappedRequest, wrappedResponse)     // ======== This performs the actual request!
//        val duration = System.currentTimeMillis() - startTime
//        sessionLog.endTime = LocalTime.now()
//        sessionLog.duration = duration
//
//        // I can only log the request's body AFTER the request has been made and ContentCachingRequestWrapper did its work.
//        val requestBody = this.getContentAsString(wrappedRequest.contentAsByteArray, this.maxPayloadLength, request.characterEncoding)
//        if (requestBody.length > 0) {
////            this.logger.info(" \n  Request body filter:\n$requestBody")
//            sessionLog.request = requestBody
//        }
//
//        sessionLog.responseStatus = response.status.toString()
////        this.logger.info("<= " + reqInfo + ": returned status=" + response.status + " in " + duration + "ms")
//        if (includeResponsePayload) {
//            val buf = wrappedResponse.contentAsByteArray
//            val result = getContentAsString(buf, this.maxPayloadLength, response.characterEncoding)
//            sessionLog.response= result
//            //this.logger.info(" \n  Response body filter:\n" + result)
//        }
//        sessionLogRepository.save(sessionLog)
//        wrappedResponse.copyBodyToResponse()  // IMPORTANT: copy content of response back into original response
//
//    }
//
//    //    @Override
//    //    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//    //        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    //    }
//
//}
