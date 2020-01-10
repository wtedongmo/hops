package com.afsoltech.config

import com.afsoltech.core.repository.security.MenuRepository
import com.afsoltech.core.repository.security.RubricRepository
import mu.KLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

//@RequestMapping(value = ["/users", "/"])
@ConditionalOnProperty(name = ["spring.jpa.hibernate.ddl-auto"], havingValue = "update")
@Configuration
@ComponentScan("com.afsoltech.kops")
class CreateRubric(val requestMappingHandlerMapping: RequestMappingHandlerMapping, val rubricRepository: RubricRepository,
                   val menuRepository: MenuRepository)  {

    companion object : KLogging()

    @Bean
    fun getEndPointsInView() = CommandLineRunner {

        val keys = requestMappingHandlerMapping.handlerMethods.keys
        val methods = requestMappingHandlerMapping.handlerMethods

//        val menuOp = menuRepository.findById(1)
//        var menu : Menu
//        if(menuOp.isPresent)
//            menu = menuOp.get()
//        else menu = Menu(1, "Initial Menu")
//        menu.status = BaseStatus.ACTIVE
//        menu = menuRepository.save(menu)
//
//        val rubricList = mutableListOf<Rubric>()
//        var index :Long =1
//        keys.forEach { key ->
//            if(key.methodsCondition.methods.contains(RequestMethod.GET)){
//                val rubric = Rubric(id = index++, code = null, label = key.name?:key.patternsCondition.patterns.elementAt(0),
//                        uri = key.patternsCondition.patterns.elementAt(0), menu = menu)
//                rubric.status = BaseStatus.ACTIVE
//                rubricList.add(rubricRepository.save(rubric))
//            }
//        }

        logger.trace { "\n Execution of config class" }
        methods.forEach{ key, value ->
            logger.trace { "$key ==== ${value.beanType.`package`}" }
        }
//        requestMappingHandlerMapping.pathPrefixes
//        val paths = requestMappingHandlerMapping.pathPrefixes
//        paths.forEach{ key, value ->
//            logger.trace { "$key ==== $value" }
//        }
    }

}