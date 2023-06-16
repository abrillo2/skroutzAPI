/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.skroutz.ninjastore.utils;

import static com.skroutz.ninjastore.utils.HelperClass.checkProductData;
import static com.skroutz.ninjastore.utils.HelperClass.parseProductXml;
import gui.UpdateXMLDistributorGui;
import static gui.UpdateXMLDistributorGui.docFolder;
import static gui.UpdateXMLDistributorGui.nsUrl;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import xml.SaxHandlerModel;

/**
 *
 * @author owner
 */
public class PrepareDistributorData {

    private final String xtgUrl;
    private final String wwtUrl;
    private final String mobiUrl;
    
    private final String[] nsProductDomKey = {"product"};
    private final String[] xtgProductDomKey = {"products","produkty"};
    private final String[] wwtproductDomKey = {"product"};
    
    List<String> currentDistributors;
    
    
    public static int updatdStockCount = 0;
    public static int totalProduct = 0;
    
   
    public List<Map<String, String>> ninjaStoreData = new ArrayList<>();
    private Map<String, List<Map<String, String>>> allData;
    
    
    public PrepareDistributorData(String xtgUrl,String wwtUrl, String mobiUrl){
        
        this.xtgUrl = xtgUrl;
        this.wwtUrl = wwtUrl;
        this.mobiUrl = mobiUrl;
        this.currentDistributors= new ArrayList<>();
        
    
    }
    
    public void setAllData() throws CustomException{
        this.allData = getDistributorData();
    }
    
    public void setNinjaStoreData(){
        
        this.ninjaStoreData = parseProductXml(nsProductDomKey,nsUrl);
    
    }

    public List<Map<String, String>> getNinjaStoreData() {
        return ninjaStoreData;
    }
    
    
    

    
    /**
     * 
     * Parse  distributor excel 
     * 
     * @param eanList
     * @param sampleHeaders
     * @param url
     * @param eanKey
     * @param stockKey
     */
    
    public List<Map<String, String>> parseProductExcel(List<String> eanList, String[] sampleHeaders, String url, String eanKey, String stockKey, String priceKey,String storeName,String mpnKey){
            
            ExcelReader er = new ExcelReader(url,sampleHeaders);
            List<Map<String, String>> dataProductList = er.readExcel();
            
            List<Map<String, String>> data = new ArrayList<>();
            
            
            int productCount = 0;
            for(Map<String, String> product:dataProductList ){
                  String dataEan = product.get(eanKey);
                  String dataStock = product.get(stockKey);
                  String price = product.get(priceKey);
                  String mpn = product.get(mpnKey);
                  
                  
                  if(eanList.contains(dataEan)){
                       productCount++;
                       UpdateXMLDistributorGui.loadingText.setText("Parsing Excel from "+storeName+" ,Total Located Products #"+productCount);
                      Map<String,String> tempData = new HashMap<>(); 
                      
                      int stockInt = HelperClass.convertToInt(dataStock.replace("+", ""));
                      String stockStr = stockInt!=-1?stockInt+"":"0";
                      
                      
                      tempData.put("ean", dataEan);
                      tempData.put("stock",stockStr);
                      tempData.put("price",price);
                      tempData.put("mpn",mpn);
                      
                      data.add(tempData);
                      
                      
                  }
            }
            
            return data;
        
    }
    
    
    
    /**
     * 
     * parse xml for distributor
     * 
     * @param nsProductDomKey
     * @param eanList
     * @param url
     * @param eanKey
     * @param stockKey
     * @param isUrl
     * @param storeName
     * @return 
     */
    
    public  List<Map<String, String>> parseProductXmlDistributor(String[] nsProductDomKey, List<String> eanList,String url, String eanKey, String stockKey, boolean isUrl, String storeName){
        SaxHandlerModel xm = new SaxHandlerModel(nsProductDomKey);
        xm.setEanKey(eanKey);
        xm.setEans(eanList);
        xm.setStockKey(stockKey);
        xm.setUrl(url);
        xm.setStoreName(storeName);
        xm.setSaxHandlerFilter();
        
        //extracted data 
        List<Map<String, String>> data = xm.getProductUpdateList();
        return data;
    
    }
    
        /**
     * 
     * 
     * @return 
     * 
     * gather required infos from distributors
     */
     public  Map<String,List<Map<String, String>>> getDistributorData() throws CustomException {

            
            
            UpdateXMLDistributorGui.loadingText.setText("Getting NinJa Store Data...");
            //update ninja store data
            this.setNinjaStoreData();
            
            
            
            //get all eans and add to list
            List<String> eanList = HelperClass.getEanList(ninjaStoreData);
            
            checkProductData(ninjaStoreData,"ninJaStore",1000);
            
            /***
             * 
             * Parse XTG
             * 
             */
            
            UpdateXMLDistributorGui.loadingText.setText("Getting XTG Distributor Data...");
            
            String[] sampleHeadersXTG = {"Quantity (incoming)","Price EUR","Quantity","Model"};
            List<Map<String, String>> xtgData = new ArrayList<>();
            
            if(xtgUrl!=null){
                currentDistributors.add("XTG");
                xtgData=parseProductExcel(eanList,sampleHeadersXTG,xtgUrl,"EAN","Quantity","Price EUR","XTG","Model");
            }
            
            
          
            //extracted data 
           // List<Map<String, String>> xtgData = parseProductXmlDistributor(xtgProductDomKey,eanList,xtgUrl,"ean","coming",true,"XTG");
            
            System.out.println("xtg data len "+xtgData.size());
            
            /**
             * 
             * Parse WWT
             * 
             * 
             */
             //extracted data
             
            UpdateXMLDistributorGui.loadingText.setText("Getting WWT Distributor Data...");
            List<Map<String, String>> wwtData = new ArrayList<>();
            
            if(wwtUrl!=null){
                currentDistributors.add("WWT");
                wwtData=parseProductXmlDistributor(wwtproductDomKey,eanList,wwtUrl,"ean","stock",false,"WWT");

            }
            
            
           
            
            /**
             * 
             * Parse Mobi Parts
             * 
             * 
             */
            String[] sampleHeaders = {"EAN","On Stock","Part number","Price"};
            
            List<Map<String, String>> mobiData =new ArrayList<>();
            
            if(mobiUrl!=null){
                currentDistributors.add("MOBI");
                mobiData=parseProductExcel(eanList,sampleHeaders,mobiUrl,"EAN","On Stock","Price","MOBI","Part number");
            }
            
           Map<String,List<Map<String, String>>> allData = new HashMap<>();
           
           allData.put("XTG", xtgData);
           allData.put("MOBI", mobiData);
           allData.put("WWT", wwtData);
            

     
            return allData;

    }
     
    
    public void updateProductStock() throws IOException{
        Map<String,Map<String,String>> productStockList = new HashMap<>();
        
        //prepare distributors stock data by merginig the highest stock
        Set<String> dKeys = allData.keySet();
        
        UpdateXMLDistributorGui.loadingText.setText("Updating Stock...");
        for(String dKey:dKeys){
            List<Map<String, String>> dProducts = allData.get(dKey);
            
           
            for(Map<String, String> dProduct : dProducts){
                String dProductEan = dProduct.get("ean");
                String dProductStock = dProduct.get("stock");
                String distributorPrice = dProduct.get("price");
                String distributorMpn = dProduct.get("mpn");
                if(productStockList.containsKey(dProductEan)){
                    int currentStock = Integer.parseInt(dProductStock);
                    int prevStock = Integer.parseInt(productStockList.get(dProductEan).get("stock"));

                    if(currentStock > prevStock){
                        Map<String,String> tempdProducts = new HashMap<>();
                        tempdProducts.put("distributor", dKey);
                        tempdProducts.put("stock",dProductStock);
                        tempdProducts.put("price",distributorPrice);
                        tempdProducts.put("mpn",distributorMpn);
                        productStockList.put(dProductEan, tempdProducts);
                        
                        
                    }
                    continue;

                }
                Map<String,String> tempdProducts = new HashMap<>();
                tempdProducts.put("distributor", dKey);
                tempdProducts.put("stock",dProductStock);
                tempdProducts.put("price",distributorPrice);
                tempdProducts.put("mpn",distributorMpn);
                
                productStockList.put(dProductEan, tempdProducts);

            }
        }
        
        //update ninja store stock
        Set<String> dEanList = productStockList.keySet();
        
        updatdStockCount = 0;
        totalProduct =0;
        
        List<Map<String, String>> priceChangeList = new ArrayList<>();
        
        
        for(Map<String, String> ninjaData : ninjaStoreData){
            String nEan = ninjaData.get("ean");
            String nMpn = ninjaData.get("mpn");
            String nDistributor = ninjaData.get("distributor");
            nMpn = nMpn !=null?nMpn:"";
            nDistributor = nDistributor !=null?nDistributor:"";
            
            //dont update ns products
            if(nDistributor.trim().equalsIgnoreCase("ns")){
                continue;
            }
            
            totalProduct++;
            
            UpdateXMLDistributorGui.loadingText.setText("Updating Stock, product ean:"+nEan);
            if(dEanList.contains(nEan)){
                
                
                
                
                String dStock = productStockList.get(nEan).get("stock");
                String dis = productStockList.get(nEan).get("distributor");
                String disPrice = productStockList.get(nEan).get("price");
                
                disPrice =disPrice!=null?disPrice:"";
                
                String disMpn = productStockList.get(nEan).get("mpn");
                disPrice = disPrice.isBlank()?"":disPrice.replace("€", "");
                
                //check for price changes and capture
                if(ninjaData.containsKey("disPrice")){
                     String nDisPrice = ninjaData.get("disPrice");
                     
                     if(!(disPrice.strip().equalsIgnoreCase(nDisPrice.strip()))){
                          Map<String, String> tempChangeData = new HashMap<>();
                          tempChangeData.put("current price", disPrice);
                          tempChangeData.put("previous price", nDisPrice);
                          tempChangeData.put("ean", nEan);
                          tempChangeData.put("name", ninjaData.get("name"));
                          tempChangeData.put("distributor", ninjaData.get("distributor"));
                          priceChangeList.add(tempChangeData);
                          
                          
                     }
                }
                
                //update ninja store xml data
                ninjaData.put("quantity", dStock);
                ninjaData.put("distributor", dis);
                ninjaData.put("disPrice", disPrice);
                
                if(nMpn.isBlank()){
                    ninjaData.put("mpn", disMpn);
                }
                
                
                updatdStockCount++;
                

                
            }else if(currentDistributors.contains(nDistributor.trim())){
                ninjaData.put("quantity", "0");
                ninjaData.put("distributor", nDistributor);
            }
        }
        
        System.out.println(priceChangeList.size() + " price change size ");
        
        String priceChangesUri = docFolder+"/price_changes/";
        Files.createDirectories(Paths.get(priceChangesUri));
        
        ExcelWriter.writeExcel(priceChangeList, priceChangesUri+"/price_changes.xlsx", null);
    }
}

