/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scrapData;

import com.skroutz.ninjastore.utils.ExcelReader;
import com.skroutz.ninjastore.utils.ExcelWriter;
import gui.UpdateXMLDistributorGui;
import static gui.UpdateXMLDistributorGui.docFolder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import xml.CreteNinJaXMLFeed;

/**
 *
 * @author owner
 */
public class ScrapSkroutz implements Runnable{
    
    
    public static List<Map<String,String>> scrappedProductList = new ArrayList<>();
    public static List<Map<String,String>> skippedProductList = new ArrayList<>();
    public static int totalNumberOfProducts = 0;
   
    

    
    private final String skroutzUrl = "https://www.skroutz.gr/search?keyphrase=";
    private final SeleniumHelper sHelper;
    
    private String ean = null;
    private String price = null;
        

    private final String id;
    private final String productUrl;
    private final String stock;

    public ScrapSkroutz(String ean,String price,String id,String chromeDriver) {
        this.ean = ean;
        this.price = price;
        this.productUrl = "https://www.ninjaStore/"+ean;
        this.stock = 0+"";
        this.id = id;
        this.sHelper =new SeleniumHelper(chromeDriver);
    }
    
    
    
    public static void  scrapTargetList(String productListUri, int poolSizeCount, String chromeDriver) throws IOException{
        
      
       
       //open the list of ean and price to be scrapped
       String[] sampleHeaders = {"ean","price"};
       ExcelReader er = new ExcelReader(productListUri,sampleHeaders);
       List<Map<String, String>> rowList = er.readExcel();
       
       totalNumberOfProducts = rowList.size();
       
       String scrapFolder = docFolder+"/scrapper";
       Files.createDirectories(Paths.get(scrapFolder));
       
       String saveUri = scrapFolder+"/Scrapped Product List.xlsx";
       String saveUriSkipped = scrapFolder+"/Not Scrapped Product List.xlsx";
       List<String> eanList = new ArrayList<>();
       //threading initialization
       //limit the number of actual threads
       int poolSize = poolSizeCount;
       ExecutorService service = Executors.newFixedThreadPool(10);
       List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();
       
       
       int rowCount = 0;
       
       try{
            for(Map<String, String> row:rowList){

                rowCount++;

                String ean = row.get("ean");
                
                if(ean==null){
                    continue;
                }else{
                    ean = ean.strip();
                    
                    if(ean.isBlank())continue;
                }
                
                if(eanList.contains(ean)){
                    
                    System.out.println("ean found "+ean);
                    continue;
                }
                
                String price = row.get("price");
                
                if(eanList.contains(ean)) continue;


                String id = "EAN"+ean;
                Future f = null;

                f = service.submit(new ScrapSkroutz(ean,price,id,chromeDriver));
                futures.add(f);
                System.out.println("ean is "+ean);

                poolSize--;

                if(poolSize == 0 | (rowList.size() == rowCount)){
                    
                    poolSize = poolSizeCount;
                      // wait for all tasks to complete before continuing
                     for (Future<Runnable> ff : futures){
                          try {
                              ff.get();
                          } catch (InterruptedException ex) {
                              Logger.getLogger(ScrapSkroutz.class.getName()).log(Level.SEVERE, null, ex);
                          } catch (ExecutionException ex) {
                              Logger.getLogger(ScrapSkroutz.class.getName()).log(Level.SEVERE, null, ex);
                          }
                     }
                     
                           ExcelWriter.writeExcel(scrappedProductList, saveUri, CreteNinJaXMLFeed.productFeedKeys);
                           ExcelWriter.writeExcel(skippedProductList, saveUriSkipped, CreteNinJaXMLFeed.productFeedKeys); 
                            updateLabelText();

                     //shut down the executor service so that this thread can exit
                     service.shutdownNow();

                     service = Executors.newFixedThreadPool(10);
                     futures = new ArrayList<Future<Runnable>>();


                }
            }
       }catch(Exception e){
           System.out.println("stopped "+e);
       }
      ExcelWriter.writeExcel(scrappedProductList, saveUri, CreteNinJaXMLFeed.productFeedKeys);
      ExcelWriter.writeExcel(skippedProductList, saveUriSkipped, CreteNinJaXMLFeed.productFeedKeys); 
      updateLabelText();
    }
    
    public void parseData(){
        
        Map<String,String> currentScrappedProduct = new HashMap<>();
        
        String searchUrl = skroutzUrl+this.ean;
        sHelper.driver.navigate().to(searchUrl);
        WebDriverWait wait = new WebDriverWait(sHelper.driver, Duration.ofSeconds(20));
        
        
       
        
        try{


            WebElement productMain = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("js-sku-link")));
            String productLink = productMain.getAttribute("href");
           
            
            //get manufacturer
            String manufacturer = "";
             try{
               WebElement manufacturerWe = sHelper.driver.findElement(By.className("manufacturer"));
               manufacturer = manufacturerWe.findElement(By.tagName("a")).getAttribute("title");
            }catch(NoSuchElementException e){
               System.out.println("error caught :"+e);
            }
            
            

            
            sHelper.driver.navigate().to(productLink+"?lang=en&store_lang=true#description");

            String productCategory = "";
            String productName  = "";
            String productDetails = "";
            String productImage = "";
            List<String> productImages = new ArrayList<>();
            //gather product details
            WebElement productCatNav = sHelper.driver.findElement(By.id("nav"));
            List<WebElement> categoryArray = productCatNav.findElements(By.tagName("a"));
            WebElement productNameWe = sHelper.driver.findElement(By.className("page-title"));
            WebElement productDiscriptionWe = null;
            WebElement productImageWe = null;

            WebElement productImagseWe = null;


             try{
               productImageWe = sHelper.driver.findElement(By.className("sku-image"));
            }catch(NoSuchElementException e){
                
                try{
                    productImageWe = sHelper.driver.findElement(By.className("slide")).findElement(By.tagName("img"));
                     productImage =productImageWe != null? productImageWe.getAttribute("src"):"";
                }catch(NoSuchElementException e2){

                   System.out.println("error caught :"+e2);
                }
                
               //System.out.println("error caught :"+e);
            }
             
             
            
            try{
               productImagseWe = sHelper.driver.findElement(By.className("thumbnails"));
            }catch(NoSuchElementException e){
               System.out.println("error caught :"+e);
            }
            
            try{
                productDiscriptionWe = sHelper.driver.findElement(By.className("smart-description"));
            }catch(NoSuchElementException e){
               System.out.println("error caught :"+e);
            }






            for(WebElement element:categoryArray){
                String subCategory = element.getText();

                if(!subCategory.contains("Home")){
                    if(productCategory.isBlank()){
                        productCategory = subCategory;
                    }else{
                        productCategory+=" > "+subCategory;
                    }
                }
            }
           

            //gather list of additional image if available
            if(productImagseWe != null){
                List<WebElement>  productImagseWeList = productImagseWe.findElements(By.tagName("li"));
                for(WebElement element:productImagseWeList){
                     productImages.add(element.getAttribute("href"));
                }
            }
            //extract product name
            productName = productNameWe.getText();
            //remove code
            if(productName.contains("Code")){
                productName = productName.substring(0,productName.indexOf("Code"));
            }
            //gather product description

            productDetails =productDiscriptionWe!=null? productDiscriptionWe.getText():"";
            //get product image
            if(productImage.isBlank()){
                productImage =productImageWe != null? productImageWe.getAttribute("href"):"";
            }

            
            String productImagesStr = "";
            
            for(String productImgStr:productImages){
                productImgStr = productImgStr==null?"":productImgStr;
                productImagesStr+=","+productImgStr.replace("//", "");
            }
            
            if(!this.price.contains(".")){
                this.price=this.price+".00";
            }
            
            String[]dataScrapped= {this.id,productName,productCategory,this.price,manufacturer,productImage,"-",ean,this.productUrl,"Available from 4 to 10 days",this.stock,productImagesStr,productDetails,"-","-","-"};

            for(int i = 0;i<CreteNinJaXMLFeed.productFeedKeys.length;i++){
                 currentScrappedProduct.put(CreteNinJaXMLFeed.productFeedKeys[i], dataScrapped[i]);
            }
            scrappedProductList.add(currentScrappedProduct);
            
       
            updateLabelText();
            
            
        }catch (Exception e) {

            System.out.println("Error occurs while parsing web : "+e.getMessage() + "\n"+ean);
            
            Map<String,String> currentSkippedProduct = new HashMap<>();
            
            currentSkippedProduct.put("ean", this.ean);
            currentSkippedProduct.put("price", this.price);
            
            skippedProductList.add(currentSkippedProduct);    
            
            updateLabelText();
        }
        
        try{
            this.sHelper.driver.close();
        }catch(Exception e){
            return;
        }
        
        
        
        
    }
    
    public static void updateLabelText(){
             UpdateXMLDistributorGui.loadingText.setText("<html><h4>Total Number of Products :"+totalNumberOfProducts+"</h4></br>"
                    + "<h4>Total Number of Scrapped Products :"+scrappedProductList.size()+"</h4></br>"
                            + "<h4>Total Number of skipped Products :"+skippedProductList.size()+"</h4></html>");    
    }
    
    /**
     *
     */
    @Override
    public void run(){
        try {
            this.parseData();
        } catch (Exception ex) {
            System.out.println("Error occurs while parsing web : "+ex.getMessage() + "\n"+ean);
            
            Map<String,String> currentSkippedProduct = new HashMap<>();
            
            currentSkippedProduct.put("ean", this.ean);
            currentSkippedProduct.put("price", this.price);
            
            skippedProductList.add(currentSkippedProduct);    
            
            updateLabelText();
            
             try{
                this.sHelper.driver.close();
            }catch(Exception e){
                
            }
            
            
        }
    }
}
