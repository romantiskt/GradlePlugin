package com.rolan

import org.gradle.api.Plugin
import org.gradle.api.Project
class ProjectBuild implements Plugin<Project> {
    void apply(Project project) {
        project.task('build-project') {//tasks名称
            group = "android"//tasks所在的组
            description = "gradle build script demo,shares only in this build.gradle"//说明
            doLast {
                println "Hello from the BuildScriptPlugin"
            }
        }
    }
}