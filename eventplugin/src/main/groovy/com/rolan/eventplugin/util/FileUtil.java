package com.rolan.eventplugin.util;

import javassist.CtMethod;

/**
 * Created by wangyang on 2018/9/6.上午11:30
 */
public class FileUtil {

    public static int isSamePackage(String filePath,String packageName){
        if(filePath==null||filePath.length()<=0)return -1;
        if(packageName==null)return 0;
        int index = filePath.indexOf(packageName);
        return index;

    }

    public static String getSimpleName(CtMethod ctmethod) {
        String methodName = ctmethod.getName();
        return methodName.substring(
                methodName.lastIndexOf('.') + 1, methodName.length());
    }
}
