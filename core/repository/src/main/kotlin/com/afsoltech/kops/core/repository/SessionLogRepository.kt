package com.afsoltech.core.repository

import com.afsoltech.core.repository.base.BaseRepository

interface SessionLogRepository : BaseRepository<SessionLog, Long> {

    fun findByUsername(username: String) : List<SessionLog>
}



