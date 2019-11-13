package com.nanobnk.epayment.portal.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StringDateFormaterUtils {

    object DateToString {
        val formatterString = DateTimeFormatter. ofPattern("dd-MM-yyyy")
        val formatter = DateTimeFormatter. ofPattern("yyyyMMdd")
        val formatterDateTime = DateTimeFormatter. ofPattern("yyyyMMddHHmmss")

        fun format(localDate : LocalDate?) : String?{
            return localDate?.let{
                localDate.format(formatter)
            }
        }
    }

    object DateTimeToString {
        fun format(localDateTime : LocalDateTime?) : String?{
            return localDateTime?.let {
                localDateTime.format(DateToString.formatterDateTime)
            }
        }
    }

    object StringToDate{
        fun parse(stringDate : String?) : LocalDate? {
            return stringDate?.let {
                LocalDate.parse(stringDate, DateToString.formatter)
            }
        }
    }

    object StringDateToDateFormat{
        fun format(stringDate : String?) : String? {
            return stringDate?.let {
                val date = LocalDate.parse(stringDate, DateToString.formatter)
                date.format(DateToString.formatterString)
            }
        }
    }

    object StringToDateTime{
        fun parse(stringDateTime : String) : LocalDateTime {
            return stringDateTime?.let {
                LocalDateTime.parse(stringDateTime, DateToString.formatterDateTime)
            }
        }
    }
}