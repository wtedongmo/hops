package com.afsoltech.config

import com.afsoltech.core.entity.user.UserApp
import com.afsoltech.core.model.attribute.BaseStatus
import com.afsoltech.core.model.user.attribute.UserPrivilege
import com.afsoltech.core.model.user.attribute.UserType
import com.afsoltech.core.repository.user.UserAppRepository
import mu.KLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@ConditionalOnProperty(name = ["spring.jpa.hibernate.ddl-auto"], havingValue = "create")
@Configuration
class TempConfig(val userRepository: UserAppRepository) {

    companion object : KLogging()

    @Bean
    fun initUsers() = CommandLineRunner {
        logger.trace { "init users" }
        val bCryptPasswordEncoder= BCryptPasswordEncoder()
        val userAdmin = UserApp(id=1, login = "admin", password = bCryptPasswordEncoder.encode("admin0"),
                privilege = UserPrivilege.ADMIN, type = UserType.ADMIN,  email="wtedongmo@gmail.com")
        userAdmin.status = BaseStatus.ACTIVE

        userRepository.save(userAdmin)

    }



}