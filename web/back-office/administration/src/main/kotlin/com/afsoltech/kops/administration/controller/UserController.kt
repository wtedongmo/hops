package com.nanobnk.epayment.administration.controller

import com.nanobnk.epayment.administration.utils.CheckAuth
import com.nanobnk.epayment.core.administration.controller.AbstractBasePagingController
import com.nanobnk.epayment.core.controller.UserLoginView
import com.nanobnk.epayment.entity.UserEntity
import com.nanobnk.epayment.model.UserModel
import com.nanobnk.epayment.repository.SessionLogRepository
import com.nanobnk.epayment.service.UserService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@RequestMapping(value = ["/admin/users", "/", "/admin"])
@RestController
class UserController(val userService: UserService, val bCryptPasswordEncoder: BCryptPasswordEncoder) : AbstractBasePagingController() {

    companion object : KLogging()

    @Autowired
    lateinit var sessionLogRepository: SessionLogRepository

    @Autowired
    lateinit var checkAuth: CheckAuth

    private val showAddUserFormField = "showAddUserForm"
    private val showResetUserPasswordFormField = "showResetUserPasswordForm"
    private val showUserAssociationFormField = "showAUserAssociationForm"

    @GetMapping
    fun loadUsersMainPage(@RequestParam(value = "pageNumber", required = false) pageNumber: Int?,
                          @RequestParam(value = "showAddForm", required = false) showAddForm: Boolean?,
                          @RequestParam(value = "editId", required = false) editId: Long?,
                          @RequestParam(value = "deleteId", required = false) deleteId: Long?,
                          @RequestParam(value = "associationId", required = false) associationId: Long?,
                          @RequestParam(value = "resetId", required = false) resetId: Long?,
                          @RequestParam(value = "errorMessage", required = false) errorMessage: String?,
                          @RequestParam(value = "accessDenied", required = false) accessDenied: Boolean?
    ): ModelAndView {

        val auth = SecurityContextHolder.getContext().authentication
        if(auth==null){
            val mav = ModelAndView("login")
            val LOGIN_FORM = "loginForm"
            mav.addObject(LOGIN_FORM, UserLoginView())
            return mav
        }
        val mav = ModelAndView("user/users")

        mav.addObject(showAddUserFormField, showAddForm ?: false)
        mav.addObject("user", UserEntity())

        val hasAuth = checkAuth.hasAuthorization()
        var deniedOp: Boolean=false
        editId?.let {
            if(!hasAuth) deniedOp=true
            else {
                mav.addObject(showAddUserFormField, true)
                addUserToModel(mav, it)
            }
        }

        resetId?.let {
            if(!hasAuth) deniedOp=true
            else {
                mav.addObject(showResetUserPasswordFormField, true)
                addUserToModel(mav, it)
            }
        }

        associationId?.let {
            if(!hasAuth) deniedOp=true
            else {
                mav.addObject(showUserAssociationFormField, true)
                addUserToModelWithAssociations(mav, it, true)
            }
        }

        deleteId?.let {
            if(!hasAuth) deniedOp=true
            else {
                val result = userService.delete(deleteId)
                if (result.equals(-1L))
                    mav.addObject("DeleteMessage", "unable.to.delete")
                else
                    mav.addObject("DeleteMessage", "success.delete")
            }
        }

        if(deniedOp) {
            mav.addObject("accessDenied", "Access.Denied")
        }
        accessDenied?.let {
            if(accessDenied)
                mav.addObject("accessDenied", "Access.Denied")
        }
        errorMessage?.let {
            mav.addObject("errorMessage", errorMessage)
        }

        val users = userService.findAll(PageRequest(calculateCurrentPage(pageNumber), DEFAULT_PAGE_SIZE, Sort.Direction.ASC, "userId"))
        users?.let {
            presetPagingValues(users, mav)
        }

        mav.addObject("users", users ?: ArrayList<UserEntity>())
        return mav
    }

    private fun addUserToModelWithAssociations(mav: ModelAndView, userId: Long, addAssociations: Boolean) {

        val user = userService.findUserById(userId)
        mav.addObject("user", user)

        logger.trace { "user is ${user}" }

        if (addAssociations) {
            mav.addObject("userParticipants", userService.findParticipantAssociationForUser(userId))
            mav.addObject("unlinkedParticipants", userService.findUnlinkedParticipantsForUser(userId))
        }

    }

    private fun addUserToModel(mav: ModelAndView, userId: Long) {
        addUserToModelWithAssociations(mav, userId, false)
    }

//    @PostMapping("/admin/reset-password")
//    fun resetPassword(@ModelAttribute("user") userModel: UserModel,
//                      @RequestParam(value = "pageNumber", required = false) pageNumber: Int?) : ModelAndView {
//
//        userService.resetPassword(checkNotNull(userModel.userId), bCryptPasswordEncoder.encode(userModel.password))
//
//        return ModelAndView("redirect:/users?pageNumber=$pageNumber")
//    }

//    @GetMapping("/admin/{userId}/unlock")
//    fun unlockUser(@PathVariable userId: Long,
//                   @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {
//        userService.unlockUser(userId)
//
//        return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber")
//    }

    @PostMapping
    fun saveUser(@ModelAttribute("user") userModel: UserModel,
                 @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {

        if(!checkAuth.hasAuthorization()){
            userModel.userId?.let {
                return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&editId=${userModel.userId}&showAddForm=true" +
                        "&errorMessage=admin.user.not.authorized")
            }
            return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&showAddForm=true" +
                    "&errorMessage=admin.user.not.authorized")
        }
        logger.trace { "saving user: $userModel" }

        if(userModel.fullName.isNullOrBlank() || userModel.username.isNullOrBlank() || userModel.type?.name.isNullOrBlank() || userModel.privilege?.name.isNullOrBlank() ||
                (userModel.userId==null && userModel.password.isNullOrBlank() ) || userModel.status?.name.isNullOrBlank()){
            userModel.userId?.let {
                return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&editId=${userModel.userId}&showAddForm=true" +
                    "&errorMessage=admin.parameter.not.found")
            }
            return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&showAddForm=true" +
                "&errorMessage=admin.parameter.not.found")
        }

        var encryptPassword = true

        userModel.userId?.let {
            val user = userService.findUserById(it)

            if(userModel.password.isNullOrEmpty()){
                userModel.password = user.password
                encryptPassword = false
            } else if (user.password.equals(userModel.password)) {
                encryptPassword = false
            }

            if(!userModel.username.equals(user.username, true)){
                val list = sessionLogRepository.findByUsername(user.username!!)
                if(list.isNotEmpty()){
                    return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&editId=${userModel.userId}&showAddForm=true" +
                                "&errorMessage=username.edit.prohibed")
                }
            }
        }

        val user = userService.findByUsername(userModel.username!!)
        if(user!=null){
            if(userModel.userId==null || (userModel.userId!=null && userModel.userId!=user.userId)) {
                userModel.userId?.let {
                    return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&editId=${userModel.userId}&showAddForm=true" +
                            "&errorMessage=username.exists")
                }
                return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&showAddForm=true" +
                        "&errorMessage=username.exists")
            }
        }
        

        if (encryptPassword) {
            if(userModel.username.equals(userModel.password, true))
                return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&showAddForm=true" +
                        "&errorMessage=admin.username.password.same")
            if(userModel.password!!.length<6)
                return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&showAddForm=true" +
                        "&errorMessage=admin.password.short")
            userModel.password = bCryptPasswordEncoder.encode(userModel.username+userModel.password)
//            userModel.password = bCryptPasswordEncoder.encode(userModel.password)
        }

        try {
            userService.save(userModel)
        }catch (ex: Exception){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            userModel.userId?.let {
                return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&editId=${userModel.userId}&showAddForm=true" +
                    "&errorMessage=admin.system.error")
            }
            return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&showAddForm=true" +
                    "&errorMessage=admin.system.error")
        }

        return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber")
    }

//    @PutMapping()
//    fun updateUser(@ModelAttribute("user") userModel: UserModel,
//                 @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {
//
//        logger.trace { "saving user: $userModel" }
//
//        var encryptPassword = true
//
//        userModel.userId?.let {
//            val user = userService.findUserById(it)
//
//            if (user.password.equals(userModel.password)) {
//                encryptPassword = false
//            }
//        }
//
//        if (encryptPassword) {
//            userModel.password = bCryptPasswordEncoder.encode(userModel.password)
//        }
//
//        userService.save(userModel)
//
//        return ModelAndView("redirect:/users?pageNumber=$pageNumber")
//    }

//    @GetMapping("/admin/{userId}/linkParticipant/{participantId}")
//    fun linkParticipant(@PathVariable userId: Long,
//                        @PathVariable participantId: Long,
//                        @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {
//
//        userService.linkParticipantToUser(userId, participantId)
//
//        return ModelAndView("redirect:/admin/users?associationId=$userId&pageNumber=$pageNumber")
//
//    }
//
//    @GetMapping("/admin/{userId}/unlinkAssociation/{associationId}")
//    fun unlinkAssociation(@PathVariable userId: Long,
//                          @PathVariable associationId: Long,
//                          @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {
//
//        userService.unlinkAssociationFromUser(userId, associationId)
//        return ModelAndView("redirect:/admin/users?associationId=$userId&pageNumber=$pageNumber")
//
//    }

}