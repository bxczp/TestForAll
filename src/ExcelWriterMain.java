
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelWriterMain {
    // 当前文件已经存在
    private static String excelPath = "C:\\Users\\chengzp2018.jy\\Desktop\\test.xlsx";

    public static void main(String[] args) throws Exception {

        // 创建需要写入的数据列表
        List<ExcelDataVO> dataVOList = Test17.getList();

        // 写入数据到工作簿对象内
        Workbook workbook = ExcelWriter.exportData(dataVOList);
        ZipSecureFile.setMinInflateRatio(-1.0d);

        // 以文件的形式输出工作簿对象
        FileOutputStream fileOut = null;
        try {
            String exportFilePath = excelPath;
            File exportFile = new File(exportFilePath);
            if (!exportFile.exists()) {
                exportFile.createNewFile();
            }

            
            fileOut = new FileOutputStream(exportFilePath);
            workbook.write(fileOut);
            fileOut.flush();
        } catch (Exception e) {
            System.out.println("输出Excel时发生错误，错误原因：" + e.getMessage());
        } finally {
            try {
                if (null != fileOut) {
                    fileOut.close();
                }
                if (null != workbook) {
                    workbook.close();
                }
            } catch (IOException e) {
                System.out.println("关闭输出流时发生错误，错误原因：" + e.getMessage());
            }
        }

    }

}
