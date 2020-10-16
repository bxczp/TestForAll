
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 遍历文件夹工具类
 * 
 */
public class DirTraversal {

    public static void main(String[] args) throws FileNotFoundException {
        // LinkedList<File> files= noRecFiles("D:\\glassfish-5.1.0");
        ArrayList<File> files = listFiles("E:\\glassfish41不能使用\\glassfish4\\glassfish\\modules");
        for (File f : files) {
            System.out.println(f.getAbsolutePath());
        }
    }

    public static void copy(File file, File toFile) throws Exception {
        byte[] b = new byte[1024];
        int a;
        FileInputStream fis;
        FileOutputStream fos;
        if (file.isDirectory()) {
            String filepath = file.getAbsolutePath();
            filepath = filepath.replaceAll("\\\\", "/");
            String toFilepath = toFile.getAbsolutePath();
            toFilepath = toFilepath.replaceAll("\\\\", "/");
            int lastIndexOf = filepath.lastIndexOf("/");
            toFilepath = toFilepath + filepath.substring(lastIndexOf, filepath.length());
            File copy = new File(toFilepath);
            // 复制文件夹
            if (!copy.exists()) {
                copy.mkdir();
            }
            // 遍历文件夹
            for (File f : file.listFiles()) {
                copy(f, copy);
            }
        } else {
            if (toFile.isDirectory()) {
                String filepath = file.getAbsolutePath();
                filepath = filepath.replaceAll("\\\\", "/");
                String toFilepath = toFile.getAbsolutePath();
                toFilepath = toFilepath.replaceAll("\\\\", "/");
                int lastIndexOf = filepath.lastIndexOf("/");
                toFilepath = toFilepath + filepath.substring(lastIndexOf, filepath.length());

                // 写文件
                File newFile = new File(toFilepath);
                fis = new FileInputStream(file);
                fos = new FileOutputStream(newFile);
                while ((a = fis.read(b)) != -1) {
                    fos.write(b, 0, a);
                }
            } else {
                // 写文件
                fis = new FileInputStream(file);
                fos = new FileOutputStream(toFile);
                while ((a = fis.read(b)) != -1) {
                    fos.write(b, 0, a);
                }
            }

        }
    }

    /**
     * 不进行递归，遍历当前路径下的文件
     * 
     * @param strPath
     *            路径
     * @return 文件夹列表
     */
    public static LinkedList<File> noRecFiles(String strPath) {
        // 创建文件夹列表
        LinkedList<File> list = new LinkedList<File>();
        // 创建文件列表
        LinkedList<File> fileList = new LinkedList<File>();
        // 创建文件
        File dir = new File(strPath);
        // 获取当前路径下的所有文件
        File[] files = dir.listFiles();
        // 对文件进行循环
        for (int i = 0, size = files.length; i < size; i++) {
            // 判断是否为文件夹
            if (files[i].isDirectory()) {
                // 添加到文件夹列表
                list.add(files[i]);
            } else {
                // 添加到文件列表
                fileList.add(files[i]);
                // System.out.println(files[i].getAbsolutePath());
            }
        }
        // 创建一个临时文件
        File tmp;
        // 判断文件夹列表是否为空
        while (!list.isEmpty()) {
            // 从列表中移除第一个文件夹，并赋值为临时文件
            tmp = list.removeFirst();
            // 判断临时文件是否是文件夹
            if (tmp.isDirectory()) {
                // 获取文件夹下的文件
                files = tmp.listFiles();
                // 如果文件为空，则跳出本次循环
                if (null == files) {
                    continue;
                }
                // 循环当前文件
                for (int i = 0, size = files.length; i < size; i++) {
                    // 判断是否为文件夹
                    if (files[i].isDirectory()) {
                        // 添加到文件夹列表
                        list.add(files[i]);
                    } else {
                        // 添加到文件列表
                        fileList.add(files[i]);
                        // System.out.println(files[i].getAbsolutePath());
                    }
                }
            } else {
                // System.out.println(tmp.getAbsolutePath());
            }
        }
        return fileList;
    }

    /**
     * 递归调用数组列表
     * 
     * @param strPath
     *            路径
     * @return 文件列表
     */
    public static ArrayList<File> listFiles(String strPath) {
        return refreshFileList(strPath);
    }

    /**
     * 获取当前路径文件列表
     * 
     * @param strPath
     *            当前路径
     * @return 文件列表
     */
    private static ArrayList<File> refreshFileList(String strPath) {
        // 存储文件列表
        ArrayList<File> fileList = new ArrayList<File>();
        // 创建文件
        File dir = new File(strPath);
        // 获取当前路径下的所有文件
        File[] files = dir.listFiles();
        // 判断文件数组是否为空
        if (null == files) {
            return null;
        }
        // 循环遍历所有文件
        for (int i = 0, size = files.length; i < size; i++) {
            // 如果是文件夹
            if (files[i].isDirectory()) {
                // 遍历此路径，执行此方法
                ArrayList<File> refreshFileList = refreshFileList(files[i].getAbsolutePath());
                if (null != refreshFileList) {
                    fileList.addAll(refreshFileList);
                }
            } else {
                // 添加到文件列表
                fileList.add(files[i]);
            }
        }
        return fileList;
    }
}
