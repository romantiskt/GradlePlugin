# GradlePlugin
插件可分为三种方式编写，每种方式各有利弊，根据业务逻辑选择
* Build Script
* BuildSrc Project
* Standalone project

### Build Script
在模块的build.gradle直接编写脚本代码，仅限于当前moudle,不利于复用

* 在模块的build.gradle直接创建一个plugin
```
/**
 * 只可在module的build.gradle定义的plugin
 */
class helloWorldPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('build-hello') {//tasks名称
            group = "android"//tasks所在的组
            description = "gradle build script demo,shares only in this build.gradle"
            doLast {
                println "Hello from the BuildScriptPlugin"
            }
        }
    }
}
```
* 当前module下build.gradle apply
```
apply plugin: helloWorldPlugin
```
### BuildSrc Project
在项目建立一个gradle默认会编译的路径，整个项目的moudled都可通用

* 创建'rootProjectDir/buildSrc/src/main/groovy'
* 编写源码
* apply
```
apply plugin: com.rolan.ProjectBuild//需要写包名+类名
```

### Standalone project
* 创建一个Android Module
```
1.删除除src/main build.gradle文件外所有内容
2.清空build.gradle
3.创建 src/main/groovy/包名 目录
```
* 添加groovy语言支持
```
apply plugin: 'groovy'

dependencies {
    //gradle sdk
    compile gradleApi()
    //groovy sdk
    compile localGroovy()
}
```
* 编写插件代码
* 发布
1.build.gradle添加如下代码
```
apply plugin: 'maven-publish'

publishing {
    publications {
        mavenJava(MavenPublication) {
            groupId 'com.rolan.eventplugin'
            artifactId 'eventplugin'
            version '1.0.0'
            from components.java
        }
    }
}
publishing {
    repositories {
        maven {
            // change to point to your repo, e.g. http://my.org/repo
            url uri('../maven')//发布到本地目录中
        }
    }
}

```
2.gradlew publish 发布
 ```
   注意：记得发布或者升级版本之前不要配置了项目的 classpath,不然会发布错误
 ```
  
  
3.依赖
```
【project/build.gradle】

 classpath 'com.rolan.eventplugin:eventplugin:1.0.0'
 
 
【app/build.gradle】

apply plugin: com.rolan.eventplugin.EventStonePlugin//需要写包名+类名

```