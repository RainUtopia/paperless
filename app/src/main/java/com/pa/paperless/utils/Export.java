package com.pa.paperless.utils;

import android.util.Log;

import com.mogujie.tt.protobuf.InterfaceMacro;
import com.pa.paperless.bean.SigninBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Created by Administrator on 2018/2/28.
 */

public class Export {
    /**
     * 以xls表格的形式导出签到信息
     *
     * @param fileName  创建的表格文件名
     * @param sheetName 工作表名称
     * @param titles    每一列的标题
     * @param datas     数据
     */
    public static void ToExcel(String fileName, String sheetName, String[] titles, List<SigninBean> datas) {
        try {
            //1.创建Excel文件
            File file = new File("/sdcard/" + fileName + ".xls");
            file.createNewFile();
            //2.创建工作簿
            WritableWorkbook workbook = Workbook.createWorkbook(file);
            //3.创建sheet  int型参数 ：代表sheet号，0是第一页，1是第二页以此类推
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            //4.单元格
            Label label = null;
            //5.给第一行设置列名
            for (int i = 0; i < titles.length; i++) {
                // 三个参数分别表示： 列 行 文本内容
                label = new Label(i, 0, titles[i]);
                //6.添加单元格
                sheet.addCell(label);
            }
            //7.导入数据
            for (int i = 0; i < datas.size(); i++) {
                SigninBean bean = datas.get(i);
                for (int j = 0; j < titles.length; j++) {
                    // j 表示的是列数
                    String str = "";
                    switch (j) {
                        case 0:
                            str = bean.getSignin_num();
                            break;
                        case 1:
                            str = bean.getSignin_name();
                            break;
                        case 2:
                            str = bean.getSignin_date();
                            break;
                        case 3:
                            if (
                                    bean.getSign_in() == InterfaceMacro.Pb_MeetSignType.Pb_signin_direct.getNumber() ||
                                            bean.getSign_in() == InterfaceMacro.Pb_MeetSignType.Pb_signin_psw.getNumber() ||
                                            bean.getSign_in() == InterfaceMacro.Pb_MeetSignType.Pb_signin_photo.getNumber() ||
                                            bean.getSign_in() == InterfaceMacro.Pb_MeetSignType.Pb_signin_onepsw.getNumber() ||
                                            bean.getSign_in() == InterfaceMacro.Pb_MeetSignType.Pb_signin_onepsw_photo.getNumber() ||
                                            bean.getSign_in() == InterfaceMacro.Pb_MeetSignType.Pb_signin_psw_photo.getNumber()
                                    ) {
                                str = "已签到";
                            } else {
                                str = "###";
                            }
                            break;
                    }
                    //8.添加数据
                    label = new Label(j, i, str);
                    sheet.addCell(label);
                }
//                //8.添加序号
//                label = new Label(0, i, bean.getSignin_num());
//                sheet.addCell(label);
//
//                //9.添加姓名
//                label = new Label(1, i, bean.getSignin_name());
//                sheet.addCell(label);
//
//                //10.添加签到时间
//                label = new Label(2, i, bean.getSignin_date());
//                sheet.addCell(label);
//
//                //11.添加签到状态
//                label = new Label(3, i, bean.getSign_in() + "");
//                sheet.addCell(label);
            }
            //9.写入数据，一定记得写入数据，不然你都开始怀疑世界了，excel里面啥都没有
            workbook.write();
            //10.最后一步，关闭工作簿
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 读取Excel表格
     *
     * @param file
     */
    public static void ReadExcel(File file) {
        //1:创建workbook
        // new File("test.xls")
        Workbook workbook = null;
        try {
            workbook = Workbook.getWorkbook(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        //2:获取第一个工作表sheet
        Sheet sheet = workbook.getSheet(0);
        //3:获取数据
        System.out.println("共 " + sheet.getRows() + " 行");
        System.out.println("共 " + sheet.getColumns() + " 列");

        List<String> line = new ArrayList<>();
        for (int i = 0; i < sheet.getRows(); i++) {
            for (int j = 0; j < sheet.getColumns(); j++) {
                //4.获取定位的单元格
                Cell cell = sheet.getCell(j, i);
                //5.获取该单元格的文本内容
                String contents = cell.getContents();
                line.add(contents);
                System.out.print(contents + " ");
            }
            System.out.println();
        }
        Log.e("MyLog", "Export.ReadExcel 146行:  一共多少个数据 --->>> " + line.size());
        //最后一步：关闭资源
        workbook.close();
    }
}
