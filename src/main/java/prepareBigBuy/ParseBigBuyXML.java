/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prepareBigBuy;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.skroutz.ninjastore.utils.ExcelWriter;
import static com.skroutz.ninjastore.utils.HelperClass.parseProductXml;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import xml.CreteNinJaXMLFeed;

/**
 *
 * @author owner
 */
public class ParseBigBuyXML {
    private final String bigBuyRootFolder = "/Users/abraham/Downloads/download";
    private final String[] bBProductDomKey = {"product"};
    private Map<String,String> manufacturerList = new HashMap<>();
    
    public void parseBigBuyProducts() throws IOException{
         File bigBuyRootFile = new File(bigBuyRootFolder);
         File[] listOfFiles = bigBuyRootFile.listFiles();
         List<Map<String, String>> bigBuyData = new ArrayList<>();
         
         //prepare manufacturer id and name key pair
         setManufacturer();
         
         for (int i = 0; i < listOfFiles.length; i++) {
            

             //check if file is not directory
            if (listOfFiles[i].isFile()) {
              
              String fileName = listOfFiles[i].getName();
              
              //only walk through product xml
              if(fileName.contains("products-xml")){
                    String filePath = listOfFiles[i].getAbsolutePath();
                    String str="sdfvsdf68fsdfsf8999fsdf09";
                    String numberOnly= fileName.replaceAll("[^0-9]", "");
                    
                    
                  //read current product xml file into list
                  List<Map<String, String>> tempData = parseProductXml(bBProductDomKey,filePath);
                  String categoryFilePath = bigBuyRootFolder + "/categories-csv-"+numberOnly+"-en.csv";
                  //parse each product in the xml
                  for(Map<String, String> data:tempData){
                       //get category string
                       String[] categoryIdList = data.get("category").split(",");
                       String manufacturerId = data.get("brand");
                       String name = data.get("name");
                       String mpn = data.get("id");
                       String description = data.get("description");
                       
                       //get attributes
                       String attribute1 = data.get("attribute1");
                       String attribute2 = data.get("attribute2");
                       //init size
                       String size = "";
                       
                       if(attribute1.strip().equalsIgnoreCase("Size")){
                            size = data.get("value1");
                       }else if(attribute2.strip().equalsIgnoreCase("Size")){
                            size = data.get("value2");
                       }
                      
                       
                       //check if descrition is more than character limit
                       if(description.length() >= 32767){
                            description = description.substring(0, 32766);
                       }
                       
                       //adjust name so that it is unique using mpn
                       data.put("name", name+" "+mpn);
                       
                       String image = "";
                       String additionalImageList = "";
                       
                       //parse product category levels using category id
                       String parsedCategory = getCategory(categoryFilePath,categoryIdList);
                       data.put("category", parsedCategory);
                       //price mpn ean
                       data.put("price_with_vat","0.000");
                       data.put("mpn", mpn);
                       data.put("ean",data.get("ean13"));
                       data.put("availability","Available from 4 to 10 days");
                       data.put("quantity",data.get("stock"));
                       data.put("link","https://www.ninjaStore/"+mpn);
                       
                       
                       
                       data.put("description",description);
                       data.put("size",size);
                       data.put("distributor","BIGBUY");
                       data.put("disPrice", data.get("price"));
                       //get manufacturer from manufacturerList
                       if(manufacturerList.containsKey(manufacturerId)){
                           data.put("manufacturer", manufacturerList.get(manufacturerId));
                       }else{
                          data.put("manufacturer","");
                       }
                       //parse product image and additional image
                       for(int j=1;j<=8;j++){
                            if(j==1){
                                image = data.get("image"+j);
                            }else{
                                
                                if(data.get("image"+j) != null){
                                
                                  if(!data.get("image"+j).isBlank()){
                                   additionalImageList+=","+data.get("image"+j);
                                  }
                                }
                            
                            }
                       }
                       //add image and additional image
                       data.put("image", image);
                       data.put("additional_imageurl",additionalImageList);
                  }
                  
                  ExcelWriter.writeExcel(tempData,bigBuyRootFolder+"/products-excel-"+numberOnly+"-en.xlsx",CreteNinJaXMLFeed.productFeedKeys);
                  //merge current modified product list to main product list
                  //bigBuyData.addAll(tempData);
                    
                    
                  
              }
                
             
            } else if (listOfFiles[i].isDirectory()) {
              System.out.println("Directory " + listOfFiles[i].getName());
            }
         }
         
         //create excel from the main product list;
         //ExcelWriter.writeExcel(bigBuyData,bigBuyRootFolder+"/bigBuyData.xlsx",null);
        /* System.out.println("size "+bigBuyData.size());
         
        Set<String> headers = bigBuyData.get(0).keySet();
        String saveUri;
        CSVWriter writer = new CSVWriter(new FileWriter(bigBuyRootFolder+"/bigBuyData.csv", true));
        
        writer.writeNext((String[]) headers.toArray(String[]::new));
        
        for(Map<String, String> bigBuyTempData:bigBuyData){
              List<String> tempRow = new ArrayList<>();
              for(String header:headers){
                   tempRow.add(bigBuyTempData.get(header));
              }
              
              writer.writeNext((String[]) tempRow.toArray(String[]::new));
              writer.flush();
        }*/
         
    }
    
    public String getCategory(String categoryFilePath, String[] categoryID){
        
        
        List<String> catIdList = Arrays.asList(categoryID);
        Map<Integer, String> catMapList = new HashMap<>();
        
        String category = "";
        try {
            FileReader filereader = new FileReader(categoryFilePath);
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            
            while ((nextRecord = csvReader.readNext()) != null) {
                for (String cell : nextRecord) {
                    String[] cellVal = cell.split(";");
                    
                    String id = cellVal[0].strip().replace("\"","") ;
                    String cCategory = cellVal[2].replace("\"","") ;
                    String pCategory = cellVal[3].replace("\"","") ;
                    
                    if(catIdList.contains(id)){
                         int idIndex = catIdList.indexOf(id);
                         if(idIndex==0){
                             catMapList.put(idIndex,  pCategory+ " > "+ cCategory);
                         }else{
                             catMapList.put(idIndex, " > "+cCategory);
                         }
                    }
                    
                }  
            }
            
            for(int i=0;i<catMapList.size();i++){
                if(catMapList.containsKey(i)){
                    category = category+catMapList.get(i);
                }
                
            }
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParseBigBuyXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParseBigBuyXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvValidationException ex) {
            Logger.getLogger(ParseBigBuyXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return category;
    
    }
    
    public void setManufacturer(){
        
        try {
            FileReader filereader = new FileReader(bigBuyRootFolder+"/manufacturer-csv-en.csv");
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            
            while ((nextRecord = csvReader.readNext()) != null) {
                for (String cell : nextRecord) {
                    String[] cellVal = cell.split(";");
                    
                    String id = cellVal[0].strip().replace("\"","") ;
                    String manuFacturer = cellVal[2].replace("\"","") ;
                    
                    manufacturerList.put(id, manuFacturer);
                    
                    
                    
                }  
            }
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParseBigBuyXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParseBigBuyXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvValidationException ex) {
            Logger.getLogger(ParseBigBuyXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    
    
    }
}
