import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter {

    private static List<String> CELL_HEADS; // 列头

    static {
        // 类装载时就载入指定好的列头信息，如有需要，可以考虑做成动态生成的列头
        CELL_HEADS = new ArrayList<>();
        CELL_HEADS.add("cont1");
        CELL_HEADS.add("cont2");
        CELL_HEADS.add("cont3");
        CELL_HEADS.add("cont4");
    }

    /**
     * 生成Excel并写入数据信息
     * 
     * @param dataList
     *            数据列表
     * @return 写入数据后的工作簿对象
     */
    public static Workbook exportData(List<ExcelDataVO> dataList) {
        // 生成xlsx的Excel
        Workbook workbook = new SXSSFWorkbook();

        // 如需生成xls的Excel，请使用下面的工作簿对象，注意后续输出时文件后缀名也需更改为xls
        // Workbook workbook = new HSSFWorkbook();

        // 生成Sheet表，写入第一行的列头
        Sheet sheet = buildDataSheet(workbook);
        // 构建每行的数据内容
        int rowNum = 1;
        for (Iterator<ExcelDataVO> it = dataList.iterator(); it.hasNext();) {
            ExcelDataVO data = it.next();
            if (data == null) {
                continue;
            }
            // 输出行数据
            Row row = sheet.createRow(rowNum++);
            convertDataToRow(workbook, data, row);
        }
        return workbook;
    }

    /**
     * 生成sheet表，并写入第一行数据（列头）
     * 
     * @param workbook
     *            工作簿对象
     * @return 已经写入列头的Sheet
     */
    private static Sheet buildDataSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet();
        // 设置列头宽度
        for (int i = 0; i < CELL_HEADS.size(); i++) {
            sheet.setColumnWidth(i, 4000);
        }
        // 设置默认行高
        sheet.setDefaultRowHeight((short) 400);
        // 构建头单元格样式
        CellStyle cellStyle = buildHeadCellStyle(sheet.getWorkbook());
        // 写入第一行各列的数据
        Row head = sheet.createRow(0);
        for (int i = 0; i < CELL_HEADS.size(); i++) {
            Cell cell = head.createCell(i);
            cell.setCellValue(CELL_HEADS.get(i));
            cell.setCellStyle(cellStyle);
        }
        return sheet;
    }

    /**
     * 设置第一行列头的样式
     * 
     * @param workbook
     *            工作簿对象
     * @return 单元格样式对象
     */
    private static CellStyle buildHeadCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 对齐方式设置
        style.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
        style.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
        style.setBorderTop(CellStyle.BORDER_THIN);// 上边框
        style.setBorderRight(CellStyle.BORDER_THIN);// 右边框

        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        // 粗体字设置
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    /**
     * 将数据转换成行
     * 
     * @param data
     *            源数据
     * @param row
     *            行对象
     * @return
     */
    private static void convertDataToRow(Workbook workbook, ExcelDataVO data, Row row) {
        int cellNum = 0;
        Cell cell;
        cell = row.createCell(cellNum++);

        if (data.getCont1() != "" && (data.getCont1().contains("test") || data.getCont1().contains("Test"))) {
            CellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            cell.setCellStyle(style);
        }

        cell.setCellValue(null == data.getCont1() ? "" : data.getCont1());

        cell = row.createCell(cellNum++);

        if (null != data.getCont2()) {
            cell.setCellValue(data.getCont2());
        } else {
            cell.setCellValue("");
        }

        if (data.getCont2() != "" && (data.getCont2().trim().split(":")[1].trim().startsWith("*")
                || data.getCont2().trim().split(":")[1].trim().startsWith("//")
                || data.getCont2().trim().split(":")[1].trim().startsWith("import")
                || data.getCont2().trim().split(":")[1].trim().startsWith("/**")
                || data.getCont2().trim().split(":")[1].trim().startsWith("}catch(")
                || data.getCont2().trim().split(":")[1].trim().startsWith("} catch(")
                || data.getCont2().trim().split(":")[1].trim().startsWith("}catch (")
                || data.getCont2().trim().split(":")[1].trim().startsWith("} catch (")
                || data.getCont2().trim().split(":")[1].trim().startsWith("@Test")
                || data.getCont2().trim().split(":")[1].trim().startsWith("catch ("))) {
            CellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            cell.setCellStyle(style);
        }

        cell = row.createCell(cellNum++);
        cell.setCellValue(null == data.getCont3() ? "" : data.getCont3());

        cell = row.createCell(cellNum++);
        cell.setCellValue(null == data.getCont4() ? "" : data.getCont4());
    }
}
