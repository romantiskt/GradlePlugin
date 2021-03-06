package com.rolan.eventplugin

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import org.gradle.api.Project
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils

class EventTransform extends Transform {

    private Project project
    def pool=ClassPool.default
    EventTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {//tasks 名称
        return "EventStonePlugin"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {//处理的文件类型
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {//指定task 作用范围
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        project.android.bootClasspath.each {//这里会将android sdk插入进去，因为如果下面需要get android里的类
            pool.appendClassPath(it.absolutePath)
        }
        transformInvocation.inputs.each {
            it.jarInputs.each {//对类型为jar文件的input进行遍历[第三方依赖]
                def jarName = it.name
                def md5Name = DigestUtils.md5Hex(it.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                File injectedJarFile = JarInjectUtil.instance().inject(project,it.file)
                // 获取output目录
                def dest = transformInvocation.outputProvider.getContentLocation(
                        jarName + md5Name, it.contentTypes, it.scopes, Format.JAR)
                if(injectedJarFile!=null){
                    FileUtils.copyFile(injectedJarFile, dest)
                }else {
                    FileUtils.copyFile(it.file, dest)
                }
            }
            it.directoryInputs.each {
                //对类型为“文件夹”的input进行遍历[包含书写的类以及R.class、BuildConfig.class以及R$XXX.class等]

                InjectUtil.instance()
                        .setPackageName("com/rolan/gradleplugin")
                        .injectCodeByDir(it.file.absolutePath,it.file.absolutePath)
                // 获取output目录
                def dest = transformInvocation.outputProvider.getContentLocation(
                        it.name,
                        it.contentTypes,
                        it.scopes,
                        Format.DIRECTORY)
                FileUtils.copyDirectory(it.file, dest)
            }
        }
    }
}