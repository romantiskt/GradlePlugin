package com.rolan.eventplugin;

import com.rolan.eventplugin.util.FileUtil;

import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import javassist.ClassPool;
import sun.tools.jar.resources.jar;

/**
 * Created by wangyang on 2018/9/7.上午9:18
 */
public class JarInjectUtil {
    private ClassPool pool=null;
    private static JarInjectUtil INSTANCE;
    JarInjectUtil() {
        pool= ClassPool.getDefault();
    }

    public static JarInjectUtil instance(){
        if(INSTANCE==null){
            synchronized (JarInjectUtil.class){
                if(INSTANCE==null){
                    INSTANCE=new JarInjectUtil();
                }
            }
        }
        return INSTANCE;
    }

    public void inject(Project project,File jar){
        if(jar==null||!jar.exists())return;
        try {
            ZipFile zipFile = new ZipFile(jar);
            zipFile.close();
            zipFile = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        String jarName = jar.getName().substring(0, jar.getName().length() - "jar".length());
        String baseDir = new StringBuilder().append(project.getProjectDir().getAbsolutePath())
                .append(File.separator).append("inject")
                .append(File.separator).append(jarName).toString();

        File rootFile = new File(baseDir);
        FileUtil.clearFile(rootFile);
        rootFile.mkdirs();


    }
}
