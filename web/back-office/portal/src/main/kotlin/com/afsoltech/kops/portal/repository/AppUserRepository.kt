package com.nanobnk.epayment.portal.repository

import com.nanobnk.epayment.portal.entity.AppUser
import com.nanobnk.util.jpa.repository.BaseRepository

interface AppUserRepository : BaseRepository<AppUser, Long> {
    fun findByUsername(username: String): AppUser?
}

