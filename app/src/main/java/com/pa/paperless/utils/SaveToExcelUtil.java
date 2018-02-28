package com.pa.paperless.utils;

import android.app.Activity;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by Administrator on 2017/11/25.
 */

public class SaveToExcelUtil {
    public static WritableFont arial14font = null;

    public static WritableCellFormat arial14format = null;
    public static WritableFont arial10font = null;
    public static WritableCellFormat arial10format = null;
    public static WritableFont arial12font = null;
    public static WritableCellFormat arial12format = null;

    public final static String UTF8_ENCODING = "UTF-8";
    public final static String GBK_ENCODING = "GBK";

    public static void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14,
                    WritableFont.BOLD);
            arial14font.setColour(jxl.format.Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW);
            arial10font = new WritableFont(WritableFont.ARIAL, 10,
                    WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(jxl.format.Colour.LIGHT_BLUE);
            arial12font = new WritableFont(WritableFont.ARIAL, 12);
            arial12format = new WritableCellFormat(arial12font);
            arial12format.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    /**
     * @param fileName  生成的文件名称
     * @param sheetName
     * @param lineName
     * @param colName
     * @param values
     */
    public static void createSmtrautExcel(String fileName, String sheetName, String[] lineName, String[] colName, List<List<Map<String, Object>>> values) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            boolean isWrite = file.canWrite();
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            int ran = 1;
            for (int j = 0; j < values.size(); j++) {
                sheet.setRowView(j * 6 + ran - 1, 600);
                for (int line = 0; line < lineName.length; line++) {
                    sheet.addCell(new Label(line + 1, j * 6 + ran, lineName[line], arial10format));
                }
                for (int col = 0; col < colName.length; col++) {
                    sheet.addCell(new Label(0, col + j * 6 + ran + 1, colName[col], arial10format));
                }

                for (int i = 0; i < values.get(j).size(); i++) {
                    Map<String, Object> m = values.get(j).get(i);
                    int col = (Integer) m.get("col");
                    int line = (Integer) m.get("line");
                    String value = (String) m.get("value");
                    if (col == 0 && line == 0) {
                        sheet.addCell(new Label(col, line + j * 6 + ran - 1, value, arial14format));
                        sheet.addCell(new Label(col, line + j * 6 + ran, "", arial10format));
                        sheet.mergeCells(col, line + j * 6 + ran - 1, col + lineName.length, line + j * 6 + ran - 1);
                    } else {
                        sheet.addCell(new Label(col, line + j * 6 + ran, value, arial10format));
                    }
                }
                ran++;
            }
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
//    private WritableWorkbook wwb;
//    private String excelPath;
//    private File excelFile;
//    private Activity activity;
//
//    public SaveToExcelUtil(Activity activity, String excelPath) {
//        this.excelPath = excelPath;
//        this.activity = activity;
//        excelFile = new File(excelPath);
//        createExcel(excelFile);
//
//    }
//
//    // 创建excel表.
//    public void createExcel(File file) {
//        WritableSheet ws = null;
//        try {
//            if (!file.exists()) {
//                wwb = Workbook.createWorkbook(file);
//
//                ws = wwb.createSheet("sheet1", 0);
//
//                // 在指定单元格插入数据
//                Label lbl1 = new Label(0, 0, "姓名");
//                Label lbl2 = new Label(1, 0, "性别");
//                Label lbl3 = new Label(2, 0, "电话");
//                Label lbl4 = new Label(3, 0, "地址");
//
//                ws.addCell(lbl1);
//                ws.addCell(lbl2);
//                ws.addCell(lbl3);
//                ws.addCell(lbl4);
//
//                // 从内存中写入文件中
//                wwb.write();
//                wwb.close();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    //将数据存入到Excel表中
//    public void writeToExcel(Object... args) {
//
//        try {
//            Workbook oldWwb = Workbook.getWorkbook(excelFile);
//            wwb = Workbook.createWorkbook(excelFile, oldWwb);
//            WritableSheet ws = wwb.getSheet(0);
//            // 当前行数
//            int row = ws.getRows();
//            Label lab1 = new Label(0, row, args[0] + "");
//            Label lab2 = new Label(1, row, args[1] + "");
//            Label lab3 = new Label(2, row, args[2] + "");
//            Label lab4 = new Label(3, row, args[3] + "");
//            ws.addCell(lab1);
//            ws.addCell(lab2);
//            ws.addCell(lab3);
//            ws.addCell(lab4);
//
//            // 从内存中写入文件中,只能刷一次.
//            wwb.write();
//            wwb.close();
//            Toast.makeText(activity, "保存成功", Toast.LENGTH_SHORT).show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}