package com.afsoltech.kops.portal.config

import com.afsoltech.core.backoffice.config.ErrorHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableWebSecurity
class CustomWebSecurityConfigurerAdapter(val authenticationProvider: AuthenticationProvider) : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var accessDeniedHandler: ErrorHandler

//    @Autowired
//    lateinit var authenticationProvider: DaoAuthenticationProvider

    override fun configure(http: HttpSecurity) {

        //http.csrf().disable()
        http.authenticationProvider(authenticationProvider)
        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login").defaultSuccessUrl("/").permitAll()
                .failureUrl("/login?error=true")
        http.authorizeRequests().antMatchers("/login", "/error/**", "/vendor/**", "/data/**", "/dist/**", "/css/**", "/static.css/**",
                "/images/**", "/img/**", "/js/**", "/vendor2/**").permitAll()
                .anyRequest().authenticated()
        http
                .logout()
                .logoutRequestMatcher( AntPathRequestMatcher("/logout"))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)

    }

}