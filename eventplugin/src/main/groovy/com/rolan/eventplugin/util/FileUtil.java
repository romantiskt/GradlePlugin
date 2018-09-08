package com.rolan.eventplugin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

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

    public static void clearFile(File rootFile) {
        if (null == rootFile) {
            return;
        }
        if (!rootFile.exists()) {
            return;
        }
        if (rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            if (null != files && files.length > 0) {
                for (File file:files){
                    clearFile(file);
                }
            }
        }
        rootFile.delete();
    }

    /**
     * 将该jar包解压到指定目录
     * @param jarPath jar包的绝对路径
     * @param destDirPath jar包解压后的保存路径
     * @return 返回该jar包中包含的所有class的完整类名类名集合，其中一条数据如：com.aitski.hotpatch.Xxxx.class
     */
//    public static List unzipJar(String jarPath, String destDirPath) {
//
//        List list = new ArrayList();
//        try {
//            if (jarPath.endsWith(".jar")) {
//
//                JarFile jarFile = new JarFile(jarPath);
//                Enumeration<JarEntry> jarEntrys = jarFile.entries();
//                while (jarEntrys.hasMoreElements()) {
//                    JarEntry jarEntry = jarEntrys.nextElement();
//                    if (jarEntry.isDirectory()) {
//                        continue;
//                    }
//                    String entryName = jarEntry.getName();
//                    if (entryName.endsWith(".class")) {
//                        String className = entryName.replace('\\', '.').replace('/', '.')
//                        list.add(className);
//                    }
//                    String outFileName = destDirPath + "/" + entryName;
//                    File outFile = new File(outFileName);
//                    outFile.getParentFile().mkdirs();
//                    InputStream inputStream = jarFile.getInputStream(jarEntry);
//                    FileOutputStream fileOutputStream = new FileOutputStream(outFile);
//                    fileOutputStream.write(inputStream);
//                    fileOutputStream << inputStream;
//                    fileOutputStream.close();
//                    inputStream.close();
//                };
//                jarFile.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return list;https://blog.csdn.net/u010386612/article/details/51131642
//    }
//
//    /**
//     * 重新打包jar
//     * @param packagePath 将这个目录下的所有文件打包成jar
//     * @param destPath 打包好的jar包的绝对路径
//     */
//    public static void zipJar(String packagePath, String destPath) {
//
//        File file = new File(packagePath);
//        JarOutputStream outputStream = new JarOutputStream(new FileOutputStream(destPath));
//        file.eachFileRecurse { File f ->
//            String entryName = f.getAbsolutePath().substring(packagePath.length() + 1)
//            outputStream.putNextEntry(new ZipEntry(entryName))
//            if(!f.directory) {
//                InputStream inputStream = new FileInputStream(f)
//                outputStream << inputStream
//                inputStream.close()
//            }
//        }
//        outputStream.close()
//    }
}
