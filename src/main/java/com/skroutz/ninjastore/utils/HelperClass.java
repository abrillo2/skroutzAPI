/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.skroutz.ninjastore.utils;

import gui.UpdateXMLDistributorGui;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.transform.TransformerConfigurationException;
import xml.CreteNinJaXMLFeed;
import xml.SaxHandlerModel;

/**
 *
 * @author owner
 */
public class HelperClass {
    /**
     * 
     * 
     * Convert String to int
     * @param str
     * @return 
     */
    public static int convertToInt(String str){
        int result = -1; 
        try{
            result = Integer.parseInt(str);
        }catch(NumberFormatException e){
                          
            try{
                double strDouble = Double.parseDouble(str);
                result = (int)strDouble;
                                
                
                }catch(NumberFormatException e2){
                    System.out.println("Cannot Convert '"+str+"' to integer");
                }
        }  
        return result;
    }
    
    /****
     * 
     * Get ean from product list 
     * 
     */
    public static List<String> getEanList(List<Map<String, String>> data){
        List<String> eanList = new ArrayList<>();
        for(Map<String, String> product:data){
                String ean = product.get("ean");
                if(!ean.isBlank()){
                    eanList.add(ean);
                }
                
        }
        
        return eanList;
    }
    
   public static void testConnection() throws IOException {
      
         URL url = new URL("http://www.google.com");
         URLConnection connection = url.openConnection();
         connection.connect();

   }
   
   public static void  updateXmlFromExcel(String excelUrl,String xmlUrl) throws TransformerConfigurationException, InterruptedException, CustomException, IOException{
        
         //required xml keys
         String[] productFeedKeys = {"id","name","category",
                                             "price_with_vat","manufacturer",
                                              "image","mpn","ean","link","availability",
                                              "quantity","additional_imageurl","description","size"};
         
         ExcelReader er = new ExcelReader(excelUrl,productFeedKeys);
         List<Map<String, String>> dataProductList = er.readExcel();
         
         checkProductData(dataProductList,"Excel File",1000); 
         
         if(dataProductList.isEmpty()){
             System.out.println("Required Headers could not be located on the spreadsheet");
         }else{
                    CreteNinJaXMLFeed cnxf = new CreteNinJaXMLFeed(dataProductList);
                    cnxf.feedXML(xmlUrl);
         }
        
    }
   
       /**
     * parse xml for ninjaStroe
     * 
     * @param nsProductDomKey
     * @param url
     * @return 
     */
    
    public static List<Map<String, String>> parseProductXml(String[] nsProductDomKey, String url){
        
        UpdateXMLDistributorGui.loadingText.setText("Parsing Ninaja Store URL");
        SaxHandlerModel xm = new SaxHandlerModel(nsProductDomKey);
        
        xm.setStoreName("NinJaStore");
        xm.setUrl(url);
        xm.setSaxHandlerAll();
        
        
        //extracted data 
        List<Map<String, String>> data = xm.getProductUpdateList();
        
        return data;
    
    }
    
    public static void checkProductData(List<Map<String,String>> data,String Distributor,int length) throws CustomException{
           if(data == null){
                 // throw an object of user defined exception  
                throw new CustomException("Scrapped Data From "+Distributor+" is below "+length+", Process Stopped");  
            }else if(data.size() < length){
                 // throw an object of user defined exception  
                throw new CustomException("Scrapped Data From "+Distributor+" is below "+length+", Process Stopped");  
            }
    }
    
}
