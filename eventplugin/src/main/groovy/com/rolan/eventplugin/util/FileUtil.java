package com.rolan.eventplugin.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javassist.CtMethod;

/**
 * Created by wangyang on 2018/9/6.上午11:30
 */
public class FileUtil {
    private static String logPath = "";
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

    public static void unZipFiles(String zipFilePath, String fileSavePath, boolean isDelete) {
        boolean isUnZipSuccess = true;
        try {
            (new File(fileSavePath)).mkdirs();
            File f = new File(zipFilePath);
            if ((!f.exists()) && (f.length() <= 0)) {
                throw new RuntimeException("not find "+zipFilePath+"!");
            }
            //一定要加上编码，之前解压另外一个文件，没有加上编码导致不能解压
            JarFile zipFile = new JarFile(f);
            String gbkPath, strtemp;
            Enumeration<JarEntry> e = zipFile.entries();
            while (e.hasMoreElements()) {
                JarEntry zipEnt = e.nextElement();
                gbkPath = zipEnt.getName();
                strtemp = fileSavePath + File.separator + gbkPath;
                if (zipEnt.isDirectory()) { //目录
                    File dir = new File(strtemp);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    continue;
                } else {
                    // 读写文件
                    InputStream is = zipFile.getInputStream(zipEnt);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    // 建目录
                    String strsubdir = gbkPath;
                    for (int i = 0; i < strsubdir.length(); i++) {
                        if (strsubdir.substring(i, i + 1).equalsIgnoreCase("/")) {
                            String temp = fileSavePath + File.separator
                                    + strsubdir.substring(0, i);
                            File subdir = new File(temp);
                            if (!subdir.exists())
                                subdir.mkdir();
                        }
                    }
                    FileOutputStream fos = new FileOutputStream(strtemp);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    int len;
                    byte[] buff = new byte[5120];
                    while ((len = bis.read(buff)) != -1) {
                        bos.write(buff, 0, len);
                    }
                    bos.close();
                    fos.close();
                }
            }
            zipFile.close();
        } catch (Exception e) {
//            logger.error("解压文件出现异常：", e);
            isUnZipSuccess = false;
            System.out.println("extract file error: " + zipFilePath);
//            WriteStringToFile(logPath, "extract file error: " + zipFilePath);
        }
        /**
         * 文件不能删除的原因：
         * 1.看看是否被别的进程引用，手工删除试试(删除不了就是被别的进程占用)
         2.file是文件夹 并且不为空，有别的文件夹或文件，
         3.极有可能有可能自己前面没有关闭此文件的流(我遇到的情况)
         */
        if (isDelete && isUnZipSuccess) {
            boolean flag = new File(zipFilePath).delete();
//            logger.debug("删除源文件结果: " + flag);
//            WriteStringToFile(logPath, "delete " + zipFilePath + "result: " + flag);
        }
//        logger.debug("compress files success");
    }

    /**
     * 写内容到指定文件
     * @param filePath
     * @param content
     */
    public  static void WriteStringToFile(String filePath, String content) {
        try {
            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content + "\r\n");// 往已有的文件上添加字符串
            bw.close();
            fw.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void zipJarFile(File srcDir, File destFile) {
        if (null == srcDir||!srcDir.exists()||null == destFile) {
            return;
        }
        if (".DS_Store".equals(srcDir.getName())) {
            return;
        }

        if (!destFile.exists()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            try {
                destFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(destFile));
            if (srcDir.isDirectory()) {
                File[] files = srcDir.listFiles();
                if (null != files && files.length > 0) {
                    for (File file : files) {
                        zipInternal(zipOutputStream, file, file.getName() + File.separator);
                    }
                }
            }
            zipOutputStream.flush();
            closeQuietly(zipOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void zipInternal(ZipOutputStream out, File file, String baseDir) {
        if (".DS_Store".equals(file.getName())) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files && files.length > 0) {
                for (File f : files) {
                    zipInternal(out, f, baseDir + f.getName() + File.separator);
                }
            }
        } else {
            InputStream input = null;
            try {
                byte[] buffer = new byte[10240];
                input = new FileInputStream(file);
                out.putNextEntry(new ZipEntry(baseDir.substring(0, baseDir.indexOf(file.getName())) + file.getName()));

                int readCount = 0;
                while (-1 != (readCount = input.read(buffer))) {
                    out.write(buffer, 0, readCount);
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            closeQuietly(input);
        }
    }
    public static void closeQuietly(Closeable closeable) {
        try {
            if (null != closeable) {
                closeable.close();
                closeable = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasFiles(File rootFile) {
        if (null == rootFile || !rootFile.exists()) {
            return false;
        }
        return null == rootFile.listFiles() ? false : rootFile.listFiles().length > 0;
    }
}
