package com.afsoltech.kops.administration.controller

import com.afsoltech.core.backoffice.controller.AbstractBasePagingController
import com.afsoltech.core.backoffice.controller.UserLoginView
import com.afsoltech.core.model.SearchModel
import com.afsoltech.core.model.user.UserModel
import com.afsoltech.core.repository.SessionLogRepository
import com.afsoltech.core.service.UserService
import com.afsoltech.kops.administration.utils.CheckAuth
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@RequestMapping(value = ["/admin/users", "/", "/admin"])
@RestController
class UserController(val userService: UserService) : AbstractBasePagingController() {

    companion object : KLogging()

    @Autowired
    lateinit var sessionLogRepository: SessionLogRepository

    @Autowired
    lateinit var checkAuth: CheckAuth

    private val bCryptPasswordEncoder =BCryptPasswordEncoder()

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
                          @RequestParam(value = "accessDenied", required = false) accessDenied: Boolean?,
                          @RequestParam(value = "success", required = false) success: Boolean?,
                          @ModelAttribute("searchForm") searchForm: SearchModel?,
                          request: HttpServletRequest
    ): ModelAndView {

        val auth = SecurityContextHolder.getContext().authentication
        if(auth==null){
            val mav = ModelAndView("login")
            val LOGIN_FORM = "loginForm"
            mav.addObject(LOGIN_FORM, UserLoginView())
            return mav
        }
        val mav = ModelAndView("user/users")
        mav.addObject("username", auth.name)
        request.session.setAttribute("username", auth.name)

        mav.addObject(showAddUserFormField, showAddForm ?: false)
        mav.addObject("user", UserModel())

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
        success?.let {
            if(success)
                mav.addObject("SuccessMessage", "operation.success")
        }

        var users : Page<UserModel>? = null
        if(editId!=null || resetId!=null || deleteId!=null || associationId!=null){
            val usersSession = request.session.getAttribute(auth.name+"_user")
            if(usersSession!= null)
                users = usersSession as Page<UserModel>
        }

        if(users==null && searchForm != null && !searchForm.column.isNullOrEmpty() && !searchForm.value.isNullOrEmpty()
                && !searchForm.column.equals("NONE", true) ){
            try {
                users = userService.search(PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, Sort.Direction.ASC, "id"),
                        searchForm.column!!, searchForm.value!!)
                request.session.setAttribute(auth.name+"_user", users)
            }catch(ex: Exception){
                logger.error { ex.message +"\n"+ex.printStackTrace() }
                mav.addObject("errorMessage", "app.search.error")
                users = null
            }
        }else if(users==null)
            users = userService.findAll(PageRequest.of(calculateCurrentPage(pageNumber), DEFAULT_PAGE_SIZE, Sort.Direction.ASC, "id"))

        users?.let {
            presetPagingValues(users, mav)
        }

        mav.addObject("searchMap", getSearchFieldMap("app.search.User", "app.search.User"))
        mav.addObject("searchForm", SearchModel())
        mav.addObject("users", users ?: ArrayList<UserModel>())
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

        if(userModel.fullName.isNullOrBlank() || userModel.login.isNullOrBlank() || userModel.type?.name.isNullOrBlank() || userModel.privilege?.name.isNullOrBlank() ||
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

            if(!userModel.login.equals(user.login, true)){
                val list = sessionLogRepository.findByUsername(user.login!!)
                if(list.isNotEmpty()){
                    return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&editId=${userModel.userId}&showAddForm=true" +
                                "&errorMessage=username.edit.prohibed")
                }
            }
        }

        val userOp = userService.findByUsername(userModel.login!!)
        if(userOp.isPresent){
            val user = userOp.get()
            if(userModel.userId==null || (userModel.userId!=null && userModel.userId!=user.id)) {
                userModel.userId?.let {
                    return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&editId=${userModel.userId}&showAddForm=true" +
                            "&errorMessage=username.exists")
                }
                return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&showAddForm=true" +
                        "&errorMessage=username.exists")
            }
        }
        

        if (encryptPassword) {
            if(userModel.login.equals(userModel.password, true))
                return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&showAddForm=true" +
                        "&errorMessage=admin.username.password.same")
            if(userModel.password!!.length<6)
                return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&showAddForm=true" +
                        "&errorMessage=admin.password.short")
            userModel.password = bCryptPasswordEncoder.encode(userModel.login+userModel.password)
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

        return ModelAndView("redirect:/admin/users?pageNumber=$pageNumber&success=true")
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