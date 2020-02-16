//package com.afsoltech.hops.portal.config
//
//import ccom.afsoltech.hops.portal.model.attribute.UserPrivilege
//import com.afsoltech.hops.portal.service.CustomUserDetailsService
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.context.annotation.Configuration
//import org.springframework.role.config.annotation.authentication.builders.AuthenticationManagerBuilder
//import org.springframework.role.config.annotation.web.builders.HttpSecurity
//import org.springframework.role.config.annotation.web.configuration.EnableWebSecurity
//import org.springframework.role.config.annotation.web.configuration.WebSecurityConfigurerAdapter
//import org.springframework.role.crypto.bcrypt.BCryptPasswordEncoder
//
//
//@Configuration
//@EnableWebSecurity
//class PortalSecurityConfig () : WebSecurityConfigurerAdapter() {
//
//    @Autowired
//    lateinit var accessDeniedHandler: ErrorHandler
//
//    @Autowired
//    lateinit var myUserDetailsService: CustomUserDetailsService
//
//    @Throws(Exception::class)
//    override fun configure(http: HttpSecurity) {
//
////        http.headers().contentTypeOptions()
//        http.authorizeRequests().antMatchers("/vendor/**", "/data/**", "/dist/**", "/css/**", "/static.css/**",
//                "/images/**", "/img/**", "/js/**", "/vendor2/**", "/config/**").permitAll()
//        http.authorizeRequests()
//                .antMatchers("/", "/aboutus", "/login", "/international", "/checkinfos", "/error/**" ).permitAll()  //dashboard , Aboutus page will be permit to all portal
//                .antMatchers("/generateOtp", "/otp/otppage").hasAuthority(UserPrivilege.PRE_AUTH.name) //Only admin portal can login
//                .antMatchers("/admin/**", "/portal/**").hasAuthority(UserPrivilege.ROLE_ADMIN.name) //Only admin portal can login
//                .antMatchers("/portal/**", "/portal/homepage").authenticated() //Only normal portal can login
//                .anyRequest().authenticated() //Rest of all request need authentication
//                .and()
////                .csrf().disable()
//                .formLogin()
//                .loginPage("/login")  //Loginform all can access ..
//                //			.defaultSuccessUrl("/dashboard")
//                .defaultSuccessUrl("/generateOtp", true)
//                .failureUrl("/login?fail=true")
//                .permitAll()
//                .and()
//                .logout()
//                .permitAll()
//                .and()
//                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
//
//
//    }
//
//    @Autowired
//    @Throws(Exception::class)
//    fun configureGlobal(auth: AuthenticationManagerBuilder) {
//
//        val passwordEncoder = BCryptPasswordEncoder()
//        auth.userDetailsService<CustomUserDetailsService>(myUserDetailsService).passwordEncoder(passwordEncoder)
//    }
//
////    @Throws(Exception::class)
////    override fun configure(web: WebSecurity?) {
////        web!!
////                .ignoring()
////                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "/img/**", "/vendor/**")
////    }
//
//}
