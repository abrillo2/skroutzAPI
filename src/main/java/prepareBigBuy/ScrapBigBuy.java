/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prepareBigBuy;

import com.skroutz.ninjastore.utils.ExcelReader;
import com.skroutz.ninjastore.utils.ExcelWriter;
import com.skroutz.ninjastore.utils.HelperClass;
import gui.UpdateXMLDistributorGui;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import scrapData.ScrapSkroutz;
import static scrapData.ScrapSkroutz.totalNumberOfProducts;
import scrapData.SeleniumHelper;
import xml.CreteNinJaXMLFeed;

/**
 *
 * @author owner
 */
public class ScrapBigBuy implements Runnable{
    private Map<String,String> tempRow;
     private final String skroutzUrl = "https://www.skroutz.gr/search?keyphrase=";
     private final SeleniumHelper sHelper;
     //list of found product on skroutz , 
     public static List<Map<String,String>> foundList;
     public static List<Map<String,String>>  currentList;
     
     public ScrapBigBuy(String chromeDriver,Map<String,String> row){
         sHelper = new SeleniumHelper(chromeDriver);
         tempRow = row;
         
     }
     //parse folder containing excel file
     public static void scrapAllTargetList(String rootFolder,String chromeDriver) throws IOException{
         File bigBuyRootFile = new File(rootFolder);
         File[] listOfFiles = bigBuyRootFile.listFiles();
         List<Map<String, String>> bigBuyData = new ArrayList<>();
         
          for (int i = 0; i < listOfFiles.length; i++) {
            

             //check if file is not directory
            if (listOfFiles[i].isFile()) {
              
              String fileName = listOfFiles[i].getName();
              
              //only walk through product xml
              if(fileName.contains("products-excel")){
                    String filePath = listOfFiles[i].getAbsolutePath();
                    String str="sdfvsdf68fsdfsf8999fsdf09";
                    String numberOnly= fileName.replaceAll("[^0-9]", "");
                    
                    scrapTargetList(filePath,rootFolder+"/found",fileName,10,chromeDriver);
              }
            }
          }
     
     }
     
     
     //scrap specific file
     public static void  scrapTargetList(String productListUri,String scrapFolder,String fileName, int poolSizeCount, String chromeDriver) throws IOException{
        
        
         
       //re initialize found list
       foundList = Collections.synchronizedList(new ArrayList<>());
       
       //open the list of ean and price to be scrapped
       ExcelReader er = new ExcelReader(productListUri,CreteNinJaXMLFeed.productFeedKeys);
       currentList = Collections.synchronizedList(er.readExcel());
       
       List<Map<String, String>> iterList = new ArrayList<>(currentList);
       
       
       totalNumberOfProducts = currentList.size();
       
     
       Files.createDirectories(Paths.get(scrapFolder));
       
       String saveUri = scrapFolder+"/"+fileName;
       
       try{
       
       }catch(Exception e){
             ExcelReader er2 = new ExcelReader(saveUri,CreteNinJaXMLFeed.productFeedKeys);
             foundList = er2.readExcel();
       }
       
       
       //threading initialization
       //limit the number of actual threads
       int poolSize = poolSizeCount;
       ExecutorService service = Executors.newFixedThreadPool(10);
       List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();
       
       
       int rowCount = 0;
       
       try{
            UpdateXMLDistributorGui.loadingText.setText("Processing file "+productListUri);
            for(Map<String, String> row:iterList){

                rowCount++;
                Future f = null;

                f = service.submit(new ScrapBigBuy(chromeDriver,row));
                futures.add(f);
               

                poolSize--;

                if(poolSize == 0 | (currentList.size() == rowCount)){
                    
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
                     
                           HelperClass.testConnection();
                           ExcelWriter.writeExcel(foundList, saveUri, CreteNinJaXMLFeed.productFeedKeys);
                           ExcelWriter.writeExcel(currentList, productListUri, CreteNinJaXMLFeed.productFeedKeys);
                           
                           UpdateXMLDistributorGui.loadingText.setText("Processing file "+productListUri);
                           

                     //shut down the executor service so that this thread can exit
                     service.shutdownNow();

                     service = Executors.newFixedThreadPool(10);
                     futures = new ArrayList<Future<Runnable>>();


                }
            }
       }catch(Exception e){
           System.out.println("stopped "+e);
       }
      HelperClass.testConnection();
      ExcelWriter.writeExcel(foundList, saveUri, CreteNinJaXMLFeed.productFeedKeys);
      ExcelWriter.writeExcel(currentList, productListUri, CreteNinJaXMLFeed.productFeedKeys); 
      UpdateXMLDistributorGui.loadingText.setText("Finished Processing");
     
    }
    
    //check if current product is found in skroutz
    public void parseData(){
        
        
        String searchUrl = skroutzUrl+tempRow.get("ean");
        sHelper.driver.navigate().to(searchUrl);
        WebDriverWait wait = new WebDriverWait(sHelper.driver, Duration.ofSeconds(5));

        try{
            
            

            WebElement productMain = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("js-sku-link")));
            foundList.add(tempRow);
            currentList.removeIf(product -> product.get("ean").equalsIgnoreCase(tempRow.get("ean")));
        }catch(Exception e){
            currentList.removeIf(product -> product.get("ean").equalsIgnoreCase(tempRow.get("ean")));   
        }
    }
    
        /**
     *
     */
    @Override
    public void run(){
        try {
            this.parseData();
            this.sHelper.driver.close();
        } catch (Exception ex) {
           
            
            
             try{
                this.sHelper.driver.close();
            }catch(Exception e){
                
            }
            
            
        }
    }
}
