package com.afsoltech.config

import com.afsoltech.core.entity.security.Menu
import com.afsoltech.core.entity.security.Rubric
import com.afsoltech.core.model.attribute.BaseStatus
import com.afsoltech.core.repository.security.MenuRepository
import com.afsoltech.core.repository.security.RubricRepository
import mu.KLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@ConditionalOnProperty(name = ["spring.jpa.hibernate.ddl-auto"], havingValue = "create")
@Configuration
class CreateRubric(val requestMappingHandlerMapping: RequestMappingHandlerMapping, val rubricRepository: RubricRepository,
                   val menuRepository: MenuRepository)  {

    companion object : KLogging()

    @Bean
    fun getEndPointsInView() = CommandLineRunner {

//        val keys = requestMappingHandlerMapping.handlerMethods.keys
        val methods = requestMappingHandlerMapping.handlerMethods

        var menuOp = menuRepository.findById(1)
        var menu : Menu
        if(menuOp.isPresent)
            menu = menuOp.get()
        else menu = Menu(1, "Security")
        menu.status = BaseStatus.ACTIVE
        menu = menuRepository.save(menu)

        menuOp = menuRepository.findById(2)
        var menu2 : Menu
        if(menuOp.isPresent)
            menu2 = menuOp.get()
        else menu2 = Menu(2, "Admin")
        menu2.status = BaseStatus.ACTIVE
        menu2 = menuRepository.save(menu2)

        val rubricList = mutableListOf<Rubric>()
        var index :Long =1
        methods.forEach { key, value ->
            logger.trace { "$key ==== ${value.beanType.`package`.name}" }
            if(key.methodsCondition.methods.contains(RequestMethod.GET) && (value.beanType.`package`.name.contains("administration", true))){

                val boolMenu= value.beanType.`package`.name.contains("security", true)
                val rubric = Rubric(id = index++, code = null, label = key.name?:key.patternsCondition.patterns.elementAt(0),
                        uri = key.patternsCondition.patterns.elementAt(0), menu = if(boolMenu) menu else menu2)
                rubric.status = BaseStatus.ACTIVE
                rubricList.add(rubricRepository.save(rubric))
            }
        }

//        logger.trace { "\n Execution of config class" }
//        methods.forEach{ key, value ->
//            logger.trace { "$key ==== ${value.beanType.`package`.name}" }
//        }
//        requestMappingHandlerMapping.pathPrefixes
//        val paths = requestMappingHandlerMapping.pathPrefixes
//        paths.forEach{ key, value ->
//            logger.trace { "$key ==== $value" }
//        }
    }

}