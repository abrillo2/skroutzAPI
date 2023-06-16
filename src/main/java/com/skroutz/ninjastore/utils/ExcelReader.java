/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.skroutz.ninjastore.utils;

import gui.UpdateXMLDistributorGui;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author owner
 */


public class ExcelReader {
    private String fileUri;
    private String[] sampleHeaders;
    
    public ExcelReader(String fileUri,String[] sampleHeaders){
        this.fileUri = fileUri;
        this.sampleHeaders = sampleHeaders;
    }
    
    public String getCellValue(Cell cell) {
        if (cell != null) {
            switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();

            case BOOLEAN:
                Object val =  cell.getBooleanCellValue();
                
                return val.toString();

            case NUMERIC:
                Object o =  cell.getNumericCellValue();
                String result = new BigDecimal(o.toString()).toPlainString();
                return result;
            }
        }
        return null;
    }
    
    public List<Map<String, String>> readExcel(){
        UpdateXMLDistributorGui.loadingText.setText("Reading Excel...");
        try{
            FileInputStream file = new FileInputStream(new File(this.fileUri));
 
            //Create Workbook instance holding reference to .xlsx file
            //XSSFWorkbook workbook = new XSSFWorkbook(file);
            Workbook workbook = new XSSFWorkbook(file);
 
            //Get first/desired sheet from the workbook
            //XSSFSheet sheet = workbook.getSheetAt(0);
            Sheet sheet = workbook.getSheetAt(0);
            
            //to hold list of sheet header
            List<String> headers;
            headers = new ArrayList<>();
            
            
            List<Map<String,String>> rows;
            rows = new ArrayList<>();
            
            
            //Iterate through each rows one by one
            for(Row row : sheet) {
                
                if(!headers.isEmpty())UpdateXMLDistributorGui.loadingText.setText("Reading Excel, row#"+row.getRowNum());
                
                
                //to hold list of sheet header
                List<String> cells;
                cells = new ArrayList<>();
                
                //to hold list of values for a row
                Map<String,String> rowVals = new HashMap<>();
                for(int cn=0; cn<row.getLastCellNum(); cn++) {
                    // If the cell is missing from the file, generate a blank one
                    // (Works by specifying a MissingCellPolicy)
                    Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                        
                    DataFormatter dataFormatter = new DataFormatter();
                    String formattedCellStr = dataFormatter.formatCellValue(cell);
                    
                  
                    if(cell.getCellType()== NUMERIC){
                        formattedCellStr = getCellValue(cell);
                    }
                    
                    if(headers.isEmpty()){
                        cells.add(formattedCellStr.trim());
                    }else if((headers.size() > cn)){
                        
                        rowVals.put(headers.get(cn), formattedCellStr);
                    }
                    
                    
                }
                //check if header has ben determined yet or start capturing rows to map
                if(headers.isEmpty()){
                    headers = cells.containsAll(Arrays.asList(this.sampleHeaders))?cells:headers;
                }else{
                    
                    for(String headerVal:headers){
                        //make sure empty row cell is captured as null
                        if(!rowVals.containsKey(headerVal)){
                            rowVals.put(headerVal, null);
                        }
                    }
                    //capture the current row cells
                    rows.add(rowVals);
                }
            }
            
            
            if(headers.isEmpty()){
                
                UpdateXMLDistributorGui.loadingText
                    .setText("<html><h2>Excel File is  Invalid or Required Headers Are Missing</h2> <br/><h3>File :"+
                            this.fileUri+"</h3><br><h4>Excel Header Must Contain All Of The follwoing Value:</h4></br><h5>"+Arrays.toString(sampleHeaders)+"</h5></html>");
            }
            file.close();
            return rows;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
