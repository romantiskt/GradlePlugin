package com.rolan

import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjectBuild implements Plugin<Project> {

    void apply(Project project) {

        project.extensions.create('project_config', ProjectExtension)//这里会关联到build.gradle中配置的参数project_config{}
        project.extensions.create('module_config', ModuleConfig)
        println "start project build...."
        project.task('build-project') {//tasks名称
            group = "android"//tasks所在的组
            description = "project build plugin starting"//说明

            doLast {
                def project_config=project['project_config']
                def module_config=project['module_config']

                println "project_config name:"+project_config.name
                println "project_config version:"+project_config.version


                println "module_config name:"+module_config.name

                println "Hello from the BuildScriptPlugin"
            }
        }
    }
}

class ProjectExtension {//这里首字母一定要大写
    String name = null
    String version = null
}

class ModuleConfig {
    String name = null
}