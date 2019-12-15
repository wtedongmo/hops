package com.afsoltech.kops.agent.config

import com.afsoltech.core.backoffice.config.ErrorHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableWebSecurity
@ComponentScan("com.afsoltech")
class AgentBankWebSecurityConfigurerAdapter() : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var accessDeniedHandler: ErrorHandler

    @Autowired
    lateinit var authenticationProvider: BackOfficeAgenBankingAuthenticationProvider

//    @Autowired
//    lateinit var myUserDetailsService: CustomUserDetailsService

    override fun configure(http: HttpSecurity) {

        //http.csrf().disable()
        http.authenticationProvider(authenticationProvider)
        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login").defaultSuccessUrl("/agent-banking/").permitAll()
                .failureUrl("/login?error=true")
        http.authorizeRequests().antMatchers("/login", "/error/**", "/vendor/**", "/data/**", "/dist/**", "/css/**", "/static.css/**",
                "/images/**", "/img/**", "/js/**", "/registration", "/registration/*" , "/password-reset", "/password-reset/*").permitAll()
                .anyRequest().authenticated()
        http
                .logout()
                .logoutRequestMatcher( AntPathRequestMatcher("/logout"))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)

    }

//    @Autowired
//    @Throws(Exception::class)
//    fun configureGlobal(auth: AuthenticationManagerBuilder) {
//
//        val passwordEncoder = BCryptPasswordEncoder()
//        auth.userDetailsService<CustomUserDetailsService>(myUserDetailsService).passwordEncoder(passwordEncoder)
//    }
}