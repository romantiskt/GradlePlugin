package com.rolan.eventplugin;

import com.rolan.eventplugin.util.FileUtil;

import org.gradle.api.Project;

import java.io.File;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * Created by wangyang on 2018/9/6.上午10:59
 */
public class InjectUtil {
    private ClassPool pool=null;
    private static InjectUtil INSTANCE;
    private String packageName;

    InjectUtil() {
        pool=ClassPool.getDefault();
    }

    public static InjectUtil instance(){
        if(INSTANCE==null){
            synchronized (InjectUtil.class){
                if(INSTANCE==null){
                    INSTANCE=new InjectUtil();
                }
            }
        }
        return INSTANCE;
    }

    InjectUtil setPackageName(String packageName){
        this.packageName=packageName;
        return this;
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
            println("class file---->"+filePath);
            int index = FileUtil.isSamePackage(filePath, packageName);
            if(index!=-1){//未设置packageName默认不检查返回true
                String className = filePath.substring(index, filePath.length() - 6)
                        .replace('\\', '.').replace('/', '.');
//                addConstructorMethod(rootPath, className);
                injectMethod(rootPath,className);
                println("dir className file---->"+className);
            }
        }

    }

    private void injectMethod(String rootPath,String className) {
        try {
            CtClass ctClass = pool.get(className);
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            for (CtMethod ctmethod : ctClass.getDeclaredMethods()) {
                String methodName = FileUtil.getSimpleName(ctmethod);
                println("method---->"+methodName);
                if("onClick".equals(methodName)){
                    String body = "System.out.println(\"加入的方法\" ); ";
                    CtMethod method = ctClass.getDeclaredMethod("onClick",new CtClass[]{pool.get("android.view.View")});
                    method.insertAfter(body);

                }
            }
            ctClass.writeFile(rootPath);
            ctClass.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 统一添加构造方法
     * @param rootPath
     * @param className
     */
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
