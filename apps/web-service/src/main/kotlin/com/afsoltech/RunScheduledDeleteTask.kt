package com.afsoltech

import com.afsoltech.hops.service.ws.DeleteTemporaryNoticeService
import mu.KLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RunScheduledDeleteTask (val deleteNoticeService: DeleteTemporaryNoticeService) {

    companion object : KLogging()

    @Scheduled(cron = "\${app.schedule.delete.checked.customs}")
    fun deleteTemporaryCheckedNotice(){

        deleteNoticeService.deleteTemporarySelectedNotice()
    }


//    @Scheduled(cron = "\${app.schedule.delete.unpaid.customs}")
//    fun deleteTemporaryUnpaidNotice(){
//
//        deleteNoticeService.deleteTemporaryUnpaidNotice()
//    }


    @Scheduled(cron = "\${app.schedule.reset.customs.temporary.table}")
    fun resetTemporaryUnpaidNoticeTable(){

        deleteNoticeService.resetTemporaryUnpaidNoticeTable()
    }
}