package com.afsoltech.kops.administration.controller

import com.afsoltech.core.backoffice.controller.AbstractBasePagingController
import com.afsoltech.core.model.user.UserModel
import com.afsoltech.core.service.UserService
import com.afsoltech.kops.administration.utils.CheckAuth
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

//@RequestMapping(value = ["/admin/users", "/", "/admin"])
@RestController
class UserElementController(val userService: UserService) : AbstractBasePagingController() {

    companion object : KLogging()

    @Autowired
    lateinit var checkAuth: CheckAuth

    private val bCryptPasswordEncoder= BCryptPasswordEncoder()

    @PostMapping("/admin/users/reset-password")
    fun resetPassword(@ModelAttribute("user") userModel: UserModel,
                      @RequestParam(value = "pageNumber", required = false) pageNumber: Int?) : ModelAndView {

        if(!checkAuth.hasAuthorization()){
            return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&accessDenied=true")
        }

        if(userModel.login.equals(userModel.password, true))
            return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&showAddForm=true" +
                    "&errorMessage=admin.username.password.same")

        if(userModel.password!!.length<6)
            return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&showAddForm=true" +
                    "&errorMessage=admin.password.short")

        userService.resetPassword(checkNotNull(userModel.userId), bCryptPasswordEncoder.encode(userModel.login+userModel.password))
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