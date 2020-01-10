package com.afsoltech.config

import com.afsoltech.core.entity.security.Profile
import com.afsoltech.core.entity.security.UserProfile
import com.afsoltech.core.entity.user.UserApp
import com.afsoltech.core.model.attribute.BaseStatus
import com.afsoltech.core.model.user.attribute.UserPrivilege
import com.afsoltech.core.model.user.attribute.UserType
import com.afsoltech.core.repository.security.ProfileRepository
import com.afsoltech.core.repository.security.UserProfileRepository
import com.afsoltech.core.repository.user.UserAppRepository
import mu.KLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@ConditionalOnProperty(name = ["spring.jpa.hibernate.ddl-auto"], havingValue = "create")
@Configuration
class TempConfig(val userRepository: UserAppRepository, val userProfileRepository: UserProfileRepository, val profileRepository: ProfileRepository) {

    companion object : KLogging()

    @Bean
    fun initUsers() = CommandLineRunner {
        logger.trace { "init users" }
        val bCryptPasswordEncoder= BCryptPasswordEncoder()
        var userAdmin = UserApp(id=1, login = "admin", password = bCryptPasswordEncoder.encode("admin0"),
                privilege = UserPrivilege.ADMIN, type = UserType.ADMIN,  email="wtedongmo@gmail.com")
        userAdmin.status = BaseStatus.ACTIVE

        userAdmin = userRepository.save(userAdmin)
//        val userAdmin = userRepository.getOne(102)

        logger.trace { "init Profile" }
        var profile= Profile(1, "ADMIN")
        profile.status = BaseStatus.ACTIVE
        profile = profileRepository.save(profile)

        logger.trace { "init Profile" }
        val userProfile= UserProfile(id = 1, userP = userAdmin, profileUser = profile, type = UserType.ADMIN, privilege = UserPrivilege.ADMIN)
        userProfile.status = BaseStatus.ACTIVE
        userProfileRepository.save(userProfile)
    }



}