package com.rolan.eventplugin;

import com.rolan.eventplugin.util.FileUtil;

import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import javax.inject.Inject;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

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

    public File inject(Project project,File jar){
        if(jar==null||!jar.exists())return null;
        String jarPath = jar.getAbsolutePath();
        if(!jarPath.endsWith("classes.jar")) {
            return null;
        }
        if(jarPath.endsWith(".jar")){
            File jarFile = new File(jarPath);

            // jar包解压后的保存路径
            String jarZipDir = jarFile.getParent() + "/" + jarFile.getName().replace(".jar", "");
            FileUtil.unZipFiles(jarPath,jarZipDir,false);
            // 删除原来的jar包
            jarFile.delete();
            try {
                pool.insertClassPath(jarZipDir);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            injectCodeByDir(jarZipDir,jarZipDir);
            File sourceFile = new File(jarPath);
            FileUtil.zipJarFile(new File(jarZipDir), sourceFile);
            return sourceFile;
        }
        return  null;
    }


    void injectCodeByDir(String path, String rootPath){
        try {
            pool.insertClassPath(path);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        File dir = new File(path);
        listFiles(dir,rootPath);
    }

    void listFiles(File dir,String rootPath) {
        if (dir.isDirectory()) {
            for(File file:dir.listFiles()){
                listFiles(file, rootPath);
            }
        } else {
            injectCode(dir,rootPath);
        }
    }

    void injectCode(File file,String rootPath) {
        String filePath = file.getAbsolutePath();
        if (filePath.endsWith(".class")
                && !filePath.contains("R$")
                && !filePath.contains("R.class")
                && !filePath.contains("BuildConfig.class")) {

            int index = FileUtil.isSamePackage(filePath, "com/yanzhenjie/permission");
            if(index!=-1){//未设置packageName默认不检查返回true
                String className = filePath.substring(index, filePath.length() - 6)
                        .replace('\\', '.').replace('/', '.');
                addConstructorMethod(rootPath, className);
                println("jar className file---->"+className);
            }
        }

    }

    private void addConstructorMethod(String rootPath, String className) {
        CtClass ctClass = null ;
        try {
            ctClass = pool.getCtClass(className);

            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            String body = "System.out.println(\"构造方法\" ); ";
            CtConstructor[] cts = ctClass.getDeclaredConstructors();
            if (cts == null || cts.length == 0) {
                //手动创建一个构造函数
                CtConstructor constructor = new CtConstructor(new CtClass[0], ctClass);
                constructor.insertBeforeBody(body);
                ctClass.addConstructor(constructor);
            } else {
                cts[0].insertBeforeBody(body);
            }
            ctClass.writeFile(rootPath);
            ctClass.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void println(String msg){
        System.out.println(msg);
    }

}
