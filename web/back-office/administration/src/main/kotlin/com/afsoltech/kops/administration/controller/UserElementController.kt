package com.nanobnk.epayment.administration.controller

import com.nanobnk.epayment.administration.utils.CheckAuth
import com.nanobnk.epayment.core.administration.controller.AbstractBasePagingController
import com.nanobnk.epayment.model.UserModel
import com.nanobnk.epayment.service.UserService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

//@RequestMapping(value = ["/admin/users", "/", "/admin"])
@RestController
class UserElementController(val userService: UserService, val bCryptPasswordEncoder: BCryptPasswordEncoder) : AbstractBasePagingController() {

    companion object : KLogging()

    @Autowired
    lateinit var checkAuth: CheckAuth

    @PostMapping("/admin/users/reset-password")
    fun resetPassword(@ModelAttribute("user") userModel: UserModel,
                      @RequestParam(value = "pageNumber", required = false) pageNumber: Int?) : ModelAndView {

        if(!checkAuth.hasAuthorization()){
            return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&accessDenied=true")
        }

        if(userModel.username.equals(userModel.password, true))
            return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&showAddForm=true" +
                    "&errorMessage=admin.username.password.same")

        if(userModel.password!!.length<6)
            return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&showAddForm=true" +
                    "&errorMessage=admin.password.short")

        userService.resetPassword(checkNotNull(userModel.userId), bCryptPasswordEncoder.encode(userModel.username+userModel.password))
//        userService.resetPassword(checkNotNull(userModel.userId), bCryptPasswordEncoder.encode(userModel.password))

        return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber")
    }

    @GetMapping("/admin/users/{userId}/unlock")
    fun unlockUser(@PathVariable userId: Long,
                   @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {

        if(!checkAuth.hasAuthorization()){
           return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&accessDenied=true")
        }
        userService.unlockUser(userId)
        return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber")
    }

    @GetMapping("/admin/users/{userId}/linkParticipant/{participantId}")
    fun linkParticipant(@PathVariable userId: Long,
                        @PathVariable participantId: Long,
                        @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {

        if(!checkAuth.hasAuthorization()){
            return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&accessDenied=true")
        }

        userService.linkParticipantToUser(userId, participantId)

        return ModelAndView("redirect:/admin/users?associationId=$userId&pageNumber=$pageNumber")

    }

    @GetMapping("/admin/users/{userId}/unlinkAssociation/{associationId}")
    fun unlinkAssociation(@PathVariable userId: Long,
                          @PathVariable associationId: Long,
                          @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {

        if(!checkAuth.hasAuthorization()){
            return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&accessDenied=true")
        }
        userService.unlinkAssociationFromUser(userId, associationId)
        return ModelAndView("redirect:/admin/users?associationId=$userId&pageNumber=$pageNumber")

    }

}