package com.nanobnk.epayment.administration.config

import com.nanobnk.epayment.core.backoffice.config.ErrorHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableWebSecurity
class CustomWebSecurityConfigurerAdapter(val authenticationProvider: AuthenticationProvider) : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var accessDeniedHandler: ErrorHandler

    override fun configure(http: HttpSecurity) {

        http.authenticationProvider(authenticationProvider)
        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login").defaultSuccessUrl("/admin").permitAll()
                .failureUrl("/login?error=true")
        http.authorizeRequests().antMatchers("/login",  "/error", "/error/**", "/vendor/**", "/data/**", "/dist/**", "/css/**", "/static.css/**",
                "/images/**", "/img/**", "/js/**", "/vendor2/**", "/config/**").permitAll()
                .anyRequest().authenticated()
        http
                .logout()
                .logoutRequestMatcher( AntPathRequestMatcher("/logout")).invalidateHttpSession(true).deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)

//        csrf().disable().
    }

/*    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth
                .inMemoryAuthentication()
                .withUser("portal").password("portal").authorities(listOf(SimpleGrantedAuthority("USER")))
                .and()use
                .withUser("admin").password("admin").authorities(listOf(SimpleGrantedAuthority("ADMIN")))

    }*/

}