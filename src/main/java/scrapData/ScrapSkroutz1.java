/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scrapData;

import com.opencsv.CSVWriter;
import com.skroutz.ninjastore.utils.ExcelReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author owner
 */
public class ScrapSkroutz1 implements Runnable{
    

    
    private final String skroutzUrl = "https://www.skroutz.gr/search?keyphrase=";
    private final SeleniumHelper sHelper =new SeleniumHelper("chromeDriver");
    private String ean = null;
    
    private final String price;
    private final String productCategory2;
    private final String mpn;
    private final String productUrl;
    private final String stock;
    private final String titile2;
    private final String uid;
    
    public static BufferedWriter bWriter = null;
    public static CSVWriter writer = null;

    public ScrapSkroutz1(String ean, String uid, String titile2, String price, String productCategory2, String productUrl, String mpn, String stock) {
        this.ean = ean;
        this.uid = uid;
        this.titile2 = titile2;
        this.price = price;
        this.productCategory2 = productCategory2;
        this.productUrl = productUrl;
        this.mpn = mpn;
        this.stock = stock;
        
        //sHelper.LoadPage(skroutzUrl);
    }
    
    
    
    public static void  scrapTargetList(String productListUri){
       String[] sampleHeaders = {"ΕΑΝ","UID","MPN"};
       ExcelReader er = new ExcelReader(productListUri,sampleHeaders);
       List<Map<String, String>> rowList = er.readExcel();
       
       String saveUri = "/Users/abraham/Documents/scrappedData.csv";
       String eans = "/Users/abraham/Documents/eans2.txt";
       FileInputStream idIs = null;
       List<String> idList = new ArrayList<>();
       
       
       
       try {
           idIs = new FileInputStream(new File(eans));
           writer = new CSVWriter(new FileWriter(saveUri, true));
           BufferedReader reader = new BufferedReader(new InputStreamReader(idIs));
           bWriter = new  BufferedWriter(new FileWriter(eans, true));
           
           while(reader.ready()){
               idList.add(reader.readLine().trim());
           }
           reader.close();
          
            
       }catch (IOException ex) {
           Logger.getLogger(ScrapSkroutz1.class.getName()).log(Level.SEVERE, null, ex);
       }
        
       
       
       //threading initialization
       //limit the number of actual threads
       int poolSize = 10;
       ExecutorService service = Executors.newFixedThreadPool(10);
       List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();
       
       
       
       for(Map<String, String> row:rowList){
           
           String ean = row.get("ΕΑΝ").strip();
           String price = row.get("Τιμή");
           String mpn = row.get("MPN");
           String stock = row.get("Ποσότητα");
           String titile2 = row.get("Όνομα");
           String productUrl = row.get("URL προϊόντος");
           String productCategory2 = row.get("Κατηγορία προϊόντος");
           String uid = row.get("UID");
           
           
           
           String searchEan = ean;
           
         
           
           if(ean.indexOf("0") == 0){
               searchEan = searchEan.substring(1);
               
               System.out.println("index is "+ean+"\n"+searchEan);
           }

           if(idList.toString().contains(uid.strip())){
               System.out.println("already scrapped ean=>"+ean);
               continue;
           }else{
               try {
                   String[] dataScrapped = {titile2,"","","",productCategory2,"",uid,titile2,price,productCategory2,productUrl,ean,mpn,stock};
                   
                   //save current scrapped data
                   writer.writeNext(dataScrapped);
                   writer.flush();
                   continue;
               } catch (IOException ex) {
                   Logger.getLogger(ScrapSkroutz1.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
           
           
           
           Future f = null;
           
           f = service.submit(new ScrapSkroutz1(ean,uid,titile2,price,productCategory2,productUrl,mpn,stock));
           futures.add(f);
           
           poolSize--;
           
           if(poolSize == 0){
               poolSize = 10;
                 // wait for all tasks to complete before continuing
                for (Future<Runnable> ff : futures){
                     try {
                         ff.get();
                     } catch (InterruptedException ex) {
                         Logger.getLogger(ScrapSkroutz1.class.getName()).log(Level.SEVERE, null, ex);
                     } catch (ExecutionException ex) {
                         Logger.getLogger(ScrapSkroutz1.class.getName()).log(Level.SEVERE, null, ex);
                     }
                }
                
                //shut down the executor service so that this thread can exit
                service.shutdownNow();
                
                service = Executors.newFixedThreadPool(10);
                futures = new ArrayList<Future<Runnable>>();
                
                
           }
       }
       



       
    }
    
    public void parseData(){
        String searchUrl = skroutzUrl+this.ean;
        sHelper.driver.navigate().to(searchUrl);
        WebDriverWait wait = new WebDriverWait(sHelper.driver, Duration.ofSeconds(20));
        
        
       
        
        try{


            WebElement productMain = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("js-sku-link")));
            String productLink = productMain.getAttribute("href");
           
            
            //get manufacturer
            String manufacurer = "";
             try{
               WebElement manufacturerWe = sHelper.driver.findElement(By.className("manufacturer"));
               manufacurer = manufacturerWe.findElement(By.tagName("a")).getAttribute("title");
            }catch(NoSuchElementException e){
               System.out.println("error caught :"+e);
            }
            
            

            
            sHelper.driver.navigate().to(productLink+"?lang=en&store_lang=true#description");

            //WebElement productMainEn = sHelper.driver.findElement(By.className("js-lang-link"));

            //String enProductLink = productMainEn.getAttribute("href");

            //sHelper.driver.navigate().to(enProductLink+"#description");

            String productCategory = "";
            String productName  = "";
            String productDetails = "";
            String manufacturer = "";
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
               System.out.println("error caught :"+e);
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
            productImage =productImageWe != null? productImageWe.getAttribute("href"):"";

            System.out.println("\n"+productName + ",\n"+productDetails+ ",\n"+productImage+productImages.toString()+ ",\n"+productCategory+",\n"+manufacurer);

            
            String[] dataScrapped = {productName,productDetails,productImage,productImages.toString(),productCategory,manufacurer,this.uid,this.titile2,this.price,this.productCategory2,this.productUrl,this.ean,this.mpn,this.stock};
            
            //save current scrapped data
            writer.writeNext(dataScrapped);
            writer.flush();
            
            bWriter.write(ean);
            bWriter.newLine();
            bWriter.flush();
            
            
            
            
            
        }catch (Exception e) {
            System.out.println("Error occurs while parsing web : "+e + "\n"+ean);
            
            
        }
        
        this.sHelper.driver.close();
        
        
    }
    
    public void run(){
      this.parseData();
    }
}
