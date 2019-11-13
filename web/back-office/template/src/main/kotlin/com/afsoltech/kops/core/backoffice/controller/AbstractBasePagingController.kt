package com.nanobnk.epayment.core.administration.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import java.util.ArrayList
import org.springframework.web.servlet.ModelAndView



abstract class AbstractBasePagingController {

    val DEFAULT_PAGE_NUMBER = 0
    @Value("\${api.epayment.page.number.line.toshow:10}")
    var DEFAULT_PAGE_SIZE = 10

    fun calculateCurrentPage(pageNumber: Int?): Int {

        var pageNumberToCall = DEFAULT_PAGE_NUMBER

        if (pageNumber != null && pageNumber > 0) {
            pageNumberToCall = pageNumber - 1
        }

        return pageNumberToCall
    }

    fun presetPagingValues(entities: Page<*>, modelAndView: ModelAndView) {

        presetPagingValuesForAttributes(entities, "currentPage", "pages", "totalNumberElements", modelAndView)

    }

    fun presetPagingValuesForAttributes(entities: Page<*>, currentPageAttribute: String,
                                        pagesAttribute: String, totalNumberElementsAttribute: String,
                                        modelAndView: ModelAndView) {

        val totalPages = entities.getTotalPages()
        val currentPage = entities.getNumber()
        val totalNumberElements = entities.getTotalElements()

        val pages = ArrayList<Int>()

        if (totalPages > 0) {
            for (i in 1..totalPages) {
                pages.add(i)
            }
        }

        modelAndView.addObject(currentPageAttribute, currentPage + 1)
        modelAndView.addObject(pagesAttribute, pages)
        modelAndView.addObject(totalNumberElementsAttribute, totalNumberElements)
    }
}