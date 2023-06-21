/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.skroutz.ninjastore.utils;

import gui.UpdateXMLDistributorGui;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author owner
 */
public class ExcelWriter {
    
    public static void writeExcel(List<Map<String,String>> data,String savePath,String[] pHeaders){
        
        UpdateXMLDistributorGui.loadingText.setText("Creating Excel");
        //Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook(); 
         
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Product List");
        
        //sheet header
        if(data.size()<=0){return;}
        
        List<String> headers =pHeaders!=null?Arrays.asList(pHeaders): data.get(0).keySet().stream().collect(Collectors.toList());
        
        
        int rowNum = 0;
        for(Map<String, String> jRow:data){
            
            UpdateXMLDistributorGui.loadingText.setText("Creating Excel, Adding Row "+rowNum);
            
            Row row = sheet.createRow(rowNum++);
            int cellnum = 0;
            
            for(String header:headers){
                
                
                
                //add header
                if(rowNum == 1){
                    for(String headerC:headers){
                       sheet.setColumnWidth(cellnum, 25 * 256);
                       
                       Cell cell = row.createCell(cellnum++);
                       String currentVal = headerC;
                
                       cell.setCellValue(currentVal);
                       
                    }
                    row = sheet.createRow(rowNum++);
                    cellnum = 0;
                }
                //add rows
                Cell cell = row.createCell(cellnum++);
                String currentVal = jRow.containsKey(header)?jRow.get(header):"-";
                
                cell.setCellValue(currentVal.trim());
                
            }
        }

        //save to file
        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(savePath));
            workbook.write(out);
            out.close();
            UpdateXMLDistributorGui.loadingText.setText("<html><h2>Exce Crated at</h2> <br/><h3>"+savePath+"</h3></html>");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    
    
}
