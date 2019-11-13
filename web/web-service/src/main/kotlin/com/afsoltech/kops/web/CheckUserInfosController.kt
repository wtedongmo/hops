package com.afsoltech.kops.web


import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.util.enforce
import com.afsoltech.kops.service.ws.CheckUserInfosService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.MessageSource
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("\${api.epayment.rest.checkUserInfos}")
class CheckUserInfosController (val checkUserInfosService: CheckUserInfosService) {

    companion object : KLogging()

    @Autowired
    @Qualifier(value = "errorMessageSource")
    lateinit var messageSource: MessageSource

    @PostMapping  //
    fun checkUserInfos(@RequestBody authRequestDto: AuthRequestDto, request: HttpServletRequest?) :AuthResponseDto{

        try {
            enforce(!authRequestDto.taxpayerNumber.isNullOrBlank())
            enforce(!authRequestDto.userEmail.isNullOrBlank())
            enforce(!authRequestDto.userCategory.isNullOrBlank())
            val boolNui = authRequestDto.taxpayerNumber!!.length<10 || authRequestDto.taxpayerNumber!!.length>20
            val boolEmail = authRequestDto.userEmail!!.length<10 || authRequestDto.userEmail!!.length>30
            val boolCategory = !authRequestDto.userCategory!!.equals("E") && !authRequestDto.userCategory!!.equals("R")
            if(boolNui || boolEmail  || boolCategory){
                throw BadRequestException("EPayment.Error.Parameter.CheckUser")
            }

            val result = checkUserInfosService.checkUserInfos(authRequestDto, request)
            logger.trace { result }
            return result
        }catch (ex: RestException){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return AuthResponseDto(
                    "E",
                    messageSource.getMessage(ex.message, ex.parameters.toTypedArray(), ex.message, ex.locale),
                    authRequestDto.taxpayerNumber?:"", authRequestDto.userEmail?:"", authRequestDto.userCategory?:"")
        }catch (ex: Exception){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return AuthResponseDto(
                    "E", ex.message?:"",
                    authRequestDto.taxpayerNumber?:"", authRequestDto.userEmail?:"", authRequestDto.userCategory?:"")
        }

    }
}

//enum internal class UserCategory{
//    E, R;
//    fun getValue() : String {
//        return "portal.privilege." + this.name
//    }
//}