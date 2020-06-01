package com.afsoltech.config

import com.afsoltech.core.entity.role.Menu
import com.afsoltech.core.entity.role.Rubric
import com.afsoltech.core.model.attribute.BaseStatus
import com.afsoltech.core.repository.role.MenuRepository
import com.afsoltech.core.repository.role.RubricRepository
import mu.KLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
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
        else menu2 = Menu(2, "Application")
        menu2.status = BaseStatus.ACTIVE
        menu2 = menuRepository.save(menu2)

        val baseEntitiesMap = mutableMapOf<String, String>()
        baseEntitiesMap.put("1", "Declaration Type")
        baseEntitiesMap.put("2", "Notice Type")
        baseEntitiesMap.put("3", "Payment Category")
//        baseEntitiesMap.put("3", "Payment Category")
        baseEntitiesMap.put("4", "Payment Method")
        baseEntitiesMap.put("3", "Organism")
//        baseEntitiesMap.put("6", "Office")

        val rubricList = mutableListOf<Rubric>()
        var index :Long =1
        methods.forEach { key, value ->
            logger.trace { "$key ==== ${value.beanType.`package`.name}" }
            if(key.methodsCondition.methods.contains(RequestMethod.GET) && (value.beanType.`package`.name.contains("administration", true))){
                key.name?.let{name ->
                    val boolMenu= value.beanType.`package`.name.contains("security", true)
                    val code: String
                    if(name.contains(' ') || name.contains('_')){
                        val tabs = if(name.contains(' ')) name.split(' ') else name.split('_')
                        val index=0
                        val stBuild = StringBuilder()
                        while(index<tabs.size){
                            stBuild.append(tabs.get(index).substring(0,2))
                            if(stBuild.length>4)
                                break
                        }
                        code = stBuild.toString().toUpperCase()
                    } else if(name.length<5)
                        code = name.toUpperCase()
                    else
                        code = name.substring(0,4).toUpperCase()

                    val uri = key.patternsCondition.patterns.elementAt(0)
                    if(name.contains("entities", true)){
                        val rubricToSaves = mutableListOf<Rubric>()
                        baseEntitiesMap.forEach { t, u ->
                            val rubric = Rubric(id = index++, code = code, label = u,
                                    uri = uri.replace("{id}", t), menu = if (boolMenu) menu else menu2)
                            rubric.status = BaseStatus.ACTIVE
                            rubricToSaves.add(rubric)
                        }
                        rubricList.addAll(rubricRepository.saveAll(rubricToSaves))
                    }else {
                        val rubric = Rubric(id = index++, code = code, label = name,
                                uri = uri, menu = if (boolMenu) menu else menu2)
                        rubric.status = BaseStatus.ACTIVE
                        rubricList.add(rubricRepository.save(rubric))
                    }
                }
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