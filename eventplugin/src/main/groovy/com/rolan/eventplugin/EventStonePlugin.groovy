package com.rolan.eventplugin;

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 插件里同时支持java grooovy混编
 */
class EventStonePlugin implements Plugin<Project> {
    void apply(Project project) {
//        note()
//        //create an extension object:Whyn,so others can config via Whyn
//        project.extensions.create("event", EventExtension)
//        project.task('event'){
//            group = "test"
//            description = "hello world EventStonePlugin"
//            doLast{
//                println '*****************Is me 1122*********************'
//                println '**************************************'
//            }
//
//        }
        project.android.registerTransform(new EventTransform(project))
    }

    private void note() {
        println '------------------------'
        println 'apply EventStonePlugin'
        println '------------------------'
    }
}
//
//class EventExtension {
//    String description = 'default description'
//}
