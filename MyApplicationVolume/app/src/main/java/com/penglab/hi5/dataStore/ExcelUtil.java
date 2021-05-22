package com.penglab.hi5.dataStore;

import java.io.File;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelUtil {

    public ExcelUtil() {

    }

    public static void writeExcel(String filepath, String[] titles, ArrayList<String[]> arrayList) {

        try {

            WritableFont titleFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, true);
            WritableCellFormat titleformat = new WritableCellFormat(titleFont);
            WritableWorkbook workbook = Workbook.createWorkbook(new File(filepath));
            WritableSheet sheet = workbook.createSheet("sheet 0", 0);

            for (int i = 0; i < titles.length; i++) {
                sheet.addCell(new Label(i, 0, titles[i], titleformat));
            }

            for (int i = 1; i <= arrayList.size(); i++) {
                for (int j = 0; j < arrayList.get(i - 1).length; j++) {
                    sheet.addCell(new Label(j, i, arrayList.get(i - 1)[j]));
                }
            }

            workbook.write();
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

