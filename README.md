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