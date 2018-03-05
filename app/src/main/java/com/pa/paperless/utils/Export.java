package com.pa.paperless.utils;

import android.util.Log;

import com.pa.paperless.bean.SigninBean;
import com.pa.paperless.bean.VoteBean;
import com.pa.paperless.bean.VoteInfo;
import com.pa.paperless.bean.VoteOptionsInfo;

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
import jxl.write.WriteException;

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
     * @param datas     签到数据
     */
    public static boolean ToSigninExcel(String fileName, String sheetName, String[] titles, List<SigninBean> datas) {
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
            //7.导入数据 i 从1开始
            for (int i = 1; i <= datas.size(); i++) {
                //获取正确的数据 i-1  不然会索引越界
                SigninBean bean = datas.get(i - 1);
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
                            if (bean.getSignin_date() != null && !(bean.getSignin_date().isEmpty())) {
                                str = "已签到";
                            } else {
                                str = " ";
                            }
                            break;
                    }
                    //8.添加数据
                    label = new Label(j, i, str);
                    sheet.addCell(label);
                }
            }
            //9.写入数据，一定记得写入数据，不然你都开始怀疑世界了，excel里面啥都没有
            workbook.write();
            //10.最后一步，关闭工作簿
            workbook.close();
            Log.e("MyLog", "Export.ToExcel 112行:  导出Excel成功 --->>> ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean ToVoteResultExcel(String content, String fileName, String SheetName, String[] titles, List<VoteBean> datas) throws IOException, WriteException {
        // 1.创建Excel文件
        File file = new File("/sdcard/" + fileName + ".xls");
        file.createNewFile();
        // 2.创建工作薄
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        // 3.创建Sheet
        WritableSheet sheet = workbook.createSheet(SheetName, 0);
        // 4.创建单元格
        Label label = null;
        // 第一行展示投票内容
        Label titLabel = new Label(0, 0, content);
        sheet.addCell(titLabel);
        for (int i = 0; i < titles.length; i++) {
            // 5.定位 列 行 内容
            label = new Label(i, 1, titles[i]);
            // 6.添加单元格
            sheet.addCell(label);
        }
        // 7.导入数据 第一行是标题  所以从第二行开始
        for (int i = 2; i < datas.size() + 2; i++) {
            VoteBean voteBean = datas.get(i - 2);
            for (int j = 0; j < titles.length; j++) {
                String str = "";
                switch (j) {
                    case 0:
                        str = i - 1 + "";
                        break;
                    case 1:
                        str = voteBean.getName();
                        break;
                    case 2:
                        str = voteBean.getChoose();
                        break;
                }
                //8.添加数据  列 行 数据
                label = new Label(j, i, str);
                sheet.addCell(label);
            }
        }
        //9.写入数据，一定记得写入数据，不然你都开始怀疑世界了，excel里面啥都没有
        workbook.write();
        //10.最后一步，关闭工作簿
        workbook.close();
        return true;
    }

    /**
     * 以Excel表格的形式导出投票结果信息
     *
     * @param meetName  当前参加的会议名称
     * @param fileName  文件名
     * @param SheetName 工作表名称
     * @param titles    每一列的标题
     * @param datas     投票数据
     * @return
     * @throws IOException
     * @throws WriteException
     */
    public static boolean ToVoteExcel(String meetName, String fileName, String SheetName, String[] titles, List<VoteInfo> datas) throws IOException, WriteException {
        //1.创建Excel文件
        File file = new File("/sdcard/" + fileName + ".xls");
        file.createNewFile();
        //2.创建工作簿
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        //3.创建Sheet
        WritableSheet sheet = workbook.createSheet(SheetName, 0);
        //4.创建单元格
        Label label = null;
        //首先在第一行的位置添加 会议名作为主题
        Label titLable = new Label(0, 0, meetName);
        sheet.addCell(titLable);
        for (int i = 0; i < titles.length; i++) {
            //5.定为 列 行 文本内容
            label = new Label(i, 1, titles[i]);
            //6.添加单元格
            sheet.addCell(label);
        }
        //7.导入数据 因为第一行是自定义的标题文本  所以 i从1（第二行）开始
        for (int i = 2; i < datas.size() + 2; i++) {
            VoteInfo voteInfo = datas.get(i - 2);
            List<VoteOptionsInfo> optionInfo = voteInfo.getOptionInfo();
            for (int j = 0; j < titles.length; j++) {
                //j表示的是列数
                String str = "";
                switch (j) {
                    case 0:
                        str = voteInfo.getVoteid() + "";
                        break;
                    case 1:
                        str = voteInfo.getContent();
                        break;
                    case 2:
                        int type = voteInfo.getType();
                        if (type == 0) {
                            str = "多选";
                        } else if (type == 1) {
                            str = "单选";
                        } else if (type == 2) {
                            str = "5选4";
                        } else if (type == 3) {
                            str = "5选3";
                        } else if (type == 4) {
                            str = "5选2";
                        } else if (type == 5) {
                            str = "3选2";
                        }
                        break;
                    case 3:
                        if (voteInfo.getMode() == 0) {
                            str = "匿名";
                        } else {
                            str = "记名";
                        }
                        break;
                    case 4:
                        int votestate = voteInfo.getVotestate();
                        if (votestate == 0) {
                            str = "未发起";
                        }
                        if (votestate == 1) {
                            str = "正在投票";
                        }
                        if (votestate == 2) {
                            str = "已经结束";
                        }
                        break;
                    case 5: //选项一
                        if (optionInfo.size() >= 1) {
                            VoteOptionsInfo voteOptionsInfo = optionInfo.get(0);
                            str = voteOptionsInfo.getText();
                        }
                        break;
                    case 6: //投票数
                        if (optionInfo.size() >= 1) {
                            VoteOptionsInfo voteOptionsInfo = optionInfo.get(0);
                            int selcnt = voteOptionsInfo.getSelcnt();
                            str = selcnt + "";
                        }
                        break;
                    case 7://选项二
                        if (optionInfo.size() >= 2) {
                            VoteOptionsInfo voteOptionsInfo = optionInfo.get(1);
                            str = voteOptionsInfo.getText();
                        }
                        break;
                    case 8://投票数
                        if (optionInfo.size() >= 2) {
                            VoteOptionsInfo voteOptionsInfo = optionInfo.get(1);
                            int selcnt = voteOptionsInfo.getSelcnt();
                            str = selcnt + "";
                        }
                        break;
                    case 9://选项三
                        if (optionInfo.size() >= 3) {
                            VoteOptionsInfo voteOptionsInfo = optionInfo.get(2);
                            str = voteOptionsInfo.getText();
                        }
                        break;
                    case 10://投票数
                        if (optionInfo.size() >= 3) {
                            VoteOptionsInfo voteOptionsInfo = optionInfo.get(2);
                            int selcnt = voteOptionsInfo.getSelcnt();
                            str = selcnt + "";
                        }
                        break;
                    case 11://选项四
                        if (optionInfo.size() >= 4) {
                            VoteOptionsInfo voteOptionsInfo = optionInfo.get(3);
                            str = voteOptionsInfo.getText();
                        }
                        break;
                    case 12://投票数
                        if (optionInfo.size() >= 4) {
                            VoteOptionsInfo voteOptionsInfo = optionInfo.get(3);
                            int selcnt = voteOptionsInfo.getSelcnt();
                            str = selcnt + "";
                        }
                        break;
                    case 13://选项五
                        if (optionInfo.size() >= 5) {
                            VoteOptionsInfo voteOptionsInfo = optionInfo.get(4);
                            str = voteOptionsInfo.getText();
                        }
                        break;
                    case 14://投票数
                        if (optionInfo.size() >= 5) {
                            VoteOptionsInfo voteOptionsInfo = optionInfo.get(4);
                            int selcnt = voteOptionsInfo.getSelcnt();
                            str = selcnt + "";
                        }
                        break;
                }
                //8.添加数据  列 行 数据
                label = new Label(j, i, str);
                sheet.addCell(label);
            }
        }
        //9.写入数据，一定记得写入数据，不然你都开始怀疑世界了，excel里面啥都没有
        workbook.write();
        //10.最后一步，关闭工作簿
        workbook.close();
        return true;
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
