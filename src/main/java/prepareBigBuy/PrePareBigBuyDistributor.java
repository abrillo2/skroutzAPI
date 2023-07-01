/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prepareBigBuy;

import com.skroutz.ninjastore.utils.HelperClass;
import static com.skroutz.ninjastore.utils.HelperClass.parseProductXml;
import static gui.UpdateXMLDistributorGui.nsUrl;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import xml.SaxHandlerModel;

/**
 *
 * @author owner
 */
public class PrePareBigBuyDistributor {
     private List<Map<String,String>> bigBuyData;
     private final String[] bigBuyProductKey= {"product"};
     private final String bigBuyStockKey = "stock";
     private final String bigBuyEanKey = "ean13";
     private final String priceKeys[] = {"pvd"};
     private final String mpnKeys[] = {"id"};
     
     private final String bigBuyRootFolder;
     private final List<String> NSeanList;
     
     public PrePareBigBuyDistributor(String bigBuyRootFolder,List<String> NSeanKeys){
         this.bigBuyRootFolder=bigBuyRootFolder;
         this.NSeanList = NSeanKeys;
         this.bigBuyData = new ArrayList<>();
     }
     
     public List<Map<String,String>> getBigBuyData (){
         
         return this.bigBuyData;

    }
     
     /***
      * 
      * extract price and mpn from bigbuy folder
      * 
      */
     
     public void extractDataBigBuy(){
         //read list of files from bigBuy fodler
         File bigBuyRootFile = new File(this.bigBuyRootFolder);
         File[] listOfFiles = bigBuyRootFile.listFiles();
         //loop through each files
         for (int i = 0; i < listOfFiles.length; i++) {
             //check if file is not directory
            if (listOfFiles[i].isFile()) {
              
              String fileName = listOfFiles[i].getName();
              
              //only walk through product xml
              if(fileName.contains("products-xml")){
                    String filePath = listOfFiles[i].getAbsolutePath();
                    List<Map<String, String>> tempBigBuyData =new ArrayList( parseProductXmlDistributor(filePath,"BigBuy"));
                    
                    this.bigBuyData.addAll(tempBigBuyData);
              }
            }
         }
     
     }
     
     /**
      * 
      * Need to have a common method for this in the future
      * 
     * @param url
     * @param storeName
     * @return 
      */
    public  List<Map<String, String>> parseProductXmlDistributor(String url,String storeName){
        SaxHandlerModel xm = new SaxHandlerModel(this.bigBuyProductKey);
        xm.setEanKey(this.bigBuyEanKey);
        xm.setEans(this.NSeanList);
        xm.setStockKey(this.bigBuyStockKey);
        xm.setUrl(url);
        xm.setStoreName(storeName);
        xm.setSaxHandlerFilter();
        xm.saxHandler.setPriceandMpnKeys(this.priceKeys, this.mpnKeys);
        
        //extracted data 
        List<Map<String, String>> data = xm.getProductUpdateList();
        
        return data==null?new ArrayList<>():data;
    }
    
    /**
     * test method
     * 
     */
    
    public static void testBigBuy(){
         List<Map<String, String>> ninjaStoreData = parseProductXml(new String[]{"product"},"/Users/abraham/Downloads/productList.xml");
         //get all eans and add to list
         List<String> eanList = HelperClass.getEanList(ninjaStoreData);
         
         PrePareBigBuyDistributor pbgd = new PrePareBigBuyDistributor("/Users/abraham/Downloads/download",eanList);
         pbgd.extractDataBigBuy();
         
         System.out.println(pbgd.bigBuyData);
    }
}
