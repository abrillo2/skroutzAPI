/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xml;

import com.skroutz.ninjastore.utils.ExcelReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import scrapData.ScrapSkroutz;

/**
 *
 * @author owner
 */
public class skroutzXMLFeed {
    public void feedXML(){
        
        //prepare today's date
        Date date  = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm");
        String today=sdf.format(date);
        //xml save location
        String xmlLocation = "/Users/abraham/Documents/productList.xml";
        
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("ninjaStore");
            
            doc.appendChild(rootElement);

            //date created
            Element createdAt = doc.createElement("created_at");
            rootElement.appendChild(createdAt);
            createdAt.setTextContent(today);
            
            //products root element
            Element products = doc.createElement("products");
            rootElement.appendChild(products);
            
            
            appendProduct(doc,products);
            
            
            try {
                createXML(doc,xmlLocation);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(skroutzXMLFeed.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(skroutzXMLFeed.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerException ex) {
                Logger.getLogger(skroutzXMLFeed.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(skroutzXMLFeed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //create xml file
    public void createXML(Node document, String destUri) throws TransformerConfigurationException, UnsupportedEncodingException, FileNotFoundException, TransformerException{
			// create the xml file
			//transform the DOM Object to an XML File
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			DOMSource domSource = new DOMSource(document);
			
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(destUri),"UTF-8");

			StreamResult streamResult = new StreamResult(writer);
			
			
			transformer.transform(domSource, streamResult);
    }
    
    //append products to the xml
    public void appendProduct( Document doc,Element products){
       String[] sampleHeaders = {"ean","category","mpn"};
       String[] sampleHeaders2 = {"UID","Size"};
       String productListUri = "/Users/abraham/Downloads/scrapped data main.xlsx";
       String ringsListUri = "/Users/abraham/Downloads/items with size.xlsx";
       
       ExcelReader er = new ExcelReader(productListUri,sampleHeaders);
       List<Map<String, String>> rowList = er.readExcel();
       
       ExcelReader er2 = new ExcelReader(ringsListUri,sampleHeaders2);
       List<Map<String, String>> ringList = er2.readExcel();
       
       
       
       //product fieldlist array
       String [] productKeys = {"id","name","category","price_with_vat","manufacturer","image","mpn","ean","link","availability","quantity","additional_imageurl","description","size"};
       String nonCD = " mpn ean quantity id price_with_vat manufacturer,availability";
       
       
       
       
      
       
      
       String eans = "/Users/abraham/Documents/eans.txt";
       String rings = "/Users/abraham/Documents/rings.txt";
       String glass = "/Users/abraham/Documents/glass.txt";
       String all = "/Users/abraham/Documents/allListed.txt";
       
       
       FileInputStream eanIS = null;
       
       
       List<String> eanList = new ArrayList<>();
       List<String> eanAll = new ArrayList<>();
       List<String> glassListed = new ArrayList<>();
       List<String> ringsListed = new ArrayList<>();
       
       
       
       try {
           eanIS = new FileInputStream(new File(eans));
          
           BufferedReader reader = new BufferedReader(new InputStreamReader(eanIS));
           while(reader.ready()){
               eanList.add(reader.readLine().trim());
           }
           reader.close();
          
            
       }catch (IOException ex) {
           Logger.getLogger(ScrapSkroutz.class.getName()).log(Level.SEVERE, null, ex);
       }
       
       try {
           eanIS = new FileInputStream(new File(rings));
          
           BufferedReader reader = new BufferedReader(new InputStreamReader(eanIS));
           while(reader.ready()){
               ringsListed.add(reader.readLine().trim());
           }
           reader.close();
          
            
       }catch (IOException ex) {
           Logger.getLogger(ScrapSkroutz.class.getName()).log(Level.SEVERE, null, ex);
       }
              
        
        try {
           eanIS = new FileInputStream(new File(glass));
          
           BufferedReader reader = new BufferedReader(new InputStreamReader(eanIS));
           while(reader.ready()){
               glassListed.add(reader.readLine().trim());
           }
           reader.close();
          
            
       }catch (IOException ex) {
           Logger.getLogger(ScrapSkroutz.class.getName()).log(Level.SEVERE, null, ex);
       }
               
      try {
           eanIS = new FileInputStream(new File(all));
          
           BufferedReader reader = new BufferedReader(new InputStreamReader(eanIS));
           while(reader.ready()){
               eanAll.add(reader.readLine().trim());
           }
           reader.close();
          
            
       }catch (IOException ex) {
           Logger.getLogger(ScrapSkroutz.class.getName()).log(Level.SEVERE, null, ex);
       }
      
      
      List<String> itemNames= new ArrayList<>();
      List<String> eanDuplicate= new ArrayList<>();

      
      
       for(Map<String, String> row:rowList){
              
              
           
             
              
           
              
              String size = null;
              String price_with_vat = row.get("price_with_vat");
              String additional_imageurl = row.get("additional_imageurl");
              String id = row.get("id");
              String ean = row.get("ean");
              String mpn = row.get("mpn");
              String image = row.get("image");
              String itemName= row.get("name");
              String title2 = row.get("tittle 2");
              
              System.out.println("ID is: "+id);
          
              if(itemNames.contains(itemName)){
                    row.put("name", title2);
              }
              
              itemNames.add(itemName);
              
              if(image.isEmpty()){
                 image = row.get("link");
                 row.put("image",image);
              }
              
             
              
              if(ean.isBlank()){
                  continue;
                  
              }else if(eanDuplicate.contains(ean)){
                   continue;
              }
              
              eanDuplicate.add(ean);
              
             
              row.put("mpn", mpn);
              
              if(eanList.contains("0"+ean)| eanList.contains("00"+ean)){
                   ean =eanList.contains("00"+ean)? "00"+ean:"0"+ean;
                   
                   row.put("ean", ean);
              }
              
              
              
              if(id.isEmpty()) {continue;}
              
              if(!eanAll.contains(id)){
                  System.out.println("moving on");
                  continue;
              }else if(glassListed.contains(id)){
                  size = "47-63";
              }else if(ringsListed.contains((id))){
                  
                  for(Map<String, String> row2:ringList){
                         if(id.equals(row2.get("UID"))){
                            size = row2.get("Size");
                         }
                  }
                  
                  System.out.println("Ring Size is "+size);
              
              }
              
              Element product = doc.createElement("product");
              products.appendChild(product);
              
              additional_imageurl = additional_imageurl.replace("[", "");
              additional_imageurl = additional_imageurl.replace("]", "");
              additional_imageurl = additional_imageurl.replace("//", "");
              row.put("additional_imageurl", additional_imageurl);
              
              
              
              DecimalFormat df = new DecimalFormat("0.00");
              String formatedPrice =df.format(Double.parseDouble(price_with_vat.replace("€", "").replace(".", "").replace(",", ".")));
              row.put("price_with_vat", formatedPrice);
              
              
              
              for(String key:productKeys){
                  
                  if(key.equals("size")){
                     
                      if(size!=null){
                           Element currentElement = doc.createElement(key);
                           currentElement.setTextContent(size);
                           product.appendChild(currentElement);
                           
                           Element variations = doc.createElement("variations");
                           product.appendChild(variations);
                           
                           Element variation = doc.createElement("variation");
                           variations.appendChild(variation);
                           
                           Element variationid = doc.createElement("variationid");
                           variationid.setTextContent(row.get("ean")+"."+size);
                           variation.appendChild(variationid);
                           
                           Element link = doc.createElement("link");
                           Node cdata1 = doc.createCDATASection(row.get("link"));
                           link.appendChild(cdata1);
                           variation.appendChild(link);
                           
                           Element availability = doc.createElement("availability");
                           availability.setTextContent(row.get("availability"));
                           variation.appendChild(availability);
                           
                           Element manufacturersku = doc.createElement("manufacturersku");
                           manufacturersku.setTextContent(row.get("mpn"));
                           variation.appendChild(manufacturersku);
                           
                           Element eanI = doc.createElement("ean");
                           eanI.setTextContent(row.get("ean"));
                           variation.appendChild(eanI);
                           
                           Element price_with_vatI = doc.createElement("price_with_vat");
                           price_with_vatI.setTextContent(row.get("price_with_vat"));
                           variation.appendChild(price_with_vatI);
                           
                           Element sizeI = doc.createElement("size");
                           sizeI.setTextContent(size);
                           variation.appendChild(sizeI);
                           
                           Element quantityI = doc.createElement("quantity");
                           quantityI.setTextContent(row.get("quantity"));
                           variation.appendChild(quantityI);
                           
                           
                           
                      }
                      continue;
                  
                  }
                  
                  
                  if(key.equalsIgnoreCase("additional_imageurl")){
                      String[] additionalImages = additional_imageurl.split(",");
                      for(String url:additionalImages){
                           if(url.isBlank()) continue;
                           Element currentElement = doc.createElement(key);
                           Element currentElementI = doc.createElement(key);
                           currentElementI.setTextContent(url);
                           product.appendChild(currentElementI);
                           
                      }
                      
                  }else if(nonCD.contains(key)){
                      Element currentElement = doc.createElement(key);
                      currentElement.setTextContent(row.get(key));
                      product.appendChild(currentElement);
                  }else{
                      Element currentElement = doc.createElement(key);
                      Node cdata = doc.createCDATASection(row.get(key));
                      currentElement.appendChild(cdata);
                      product.appendChild(currentElement);
                  }
              }
              
       }
    
    }
}
