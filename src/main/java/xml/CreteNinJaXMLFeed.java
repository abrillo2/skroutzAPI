/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xml;

import com.skroutz.ninjastore.utils.CustomException;
import gui.UpdateXMLDistributorGui;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author owner
 */
public class CreteNinJaXMLFeed {
    //tracking numbers
    int totalProductAdded=0;
    int skipedProducts =0;
    
    //required xml keys
    public static final String[] productFeedKeys = {"id","name","category",
                                             "price_with_vat","manufacturer",
                                              "image","mpn","ean","link","availability",
                                              "quantity","additional_imageurl","description","size","distributor","disPrice"};
    
    private final String cdKeys = " category image link name additional_imageurl";
    
    
    private final String[] varKeys =  {"link","availability","ean","price_with_vat","size","quantity"};
    private List<Map<String, String>> products;
    
    
    public CreteNinJaXMLFeed(List<Map<String, String>> products){
        this.products = products;
    }
    
        //create xml file
    public void createXML(Node document, String destUri) throws TransformerConfigurationException, UnsupportedEncodingException, FileNotFoundException, TransformerException, InterruptedException, IOException{
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
                        
                        UpdateXMLDistributorGui.loadingText.setText("<html><h2>XML Crated at</h2> <br/><h3>"+destUri+"</h3><br/>"
                                + "<h4>Number of Products Added #"+totalProductAdded+"<h4><br/>"
                                 +"<h4>Number of Products skipped #"+skipedProducts+"<h4></html>");
                        
                        writer.flush();
                        writer.close();
                        
                       
                        
                        
    }    
    
    public void feedXML(String xmlLocation) throws TransformerConfigurationException, InterruptedException, CustomException, IOException{
        
        //prepare today's date
        Date date  = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm");
        String today=sdf.format(date);
        //xml save location
        
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
            Element productsElement = doc.createElement("products");
            rootElement.appendChild(productsElement);
            
            
          
            createXMLFeed(doc,productsElement);
            
            
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
    
    
    /**
     * 
     * @param products 
     */
    public void createXMLFeed(Document doc,Element productsElement) throws CustomException{
        
        //tracking numbers
        totalProductAdded=0;
        skipedProducts =0;
        
        UpdateXMLDistributorGui.loadingText.setText("Creating NinjaStore XML Feed...");
        
        List<String> idList = new ArrayList<>();
        List<String> eanList = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        
        
        
        for(Map<String, String> product:products){
            
            
            
            String id = product.get("id")!=null?product.get("id"):"";
            String ean = product.get("ean")!=null?product.get("ean"):"";
            String name = product.get( "name")!=null?product.get("name"):"";
            String size = product.get("size")!=null?product.get("size"):"";
            String mpn = product.get("mpn")!=null?product.get("mpn"):"";
            
            

            UpdateXMLDistributorGui.loadingText
                    .setText("Creating NinjaStore XML Feed, Current Product->EAN:"+ean+"");
            
            
            
            
            if((!id.isBlank()) && (!idList.contains(id)) && (!eanList.contains(ean))){
                
                
                
                totalProductAdded++;
                
                
                idList.add(id);
                eanList.add(ean);
                
                if(nameList.contains(name)){
                    name = name + " " +mpn;
                    
                    if(nameList.contains(name)){
                       name = name + " " +ean;
                    }
                    
                }
                
                nameList.add(name);
                product.put("name", name);
                
                
                //create current iteration's element
                Element productElement = doc.createElement("product");
                productsElement.appendChild(productElement);
                
                for(String key:productFeedKeys){
                    
                    
                    
                     String currentVal =product.containsKey(key) ? product.get(key):"";
                     currentVal = currentVal != null?currentVal:"";
                     
                     if(currentVal.length() == 1 && currentVal.equalsIgnoreCase("-")){
                         currentVal = currentVal.replace("-", "");
                                 
                     }
                     
                     if(key.equalsIgnoreCase("size")){
                         if(currentVal.equalsIgnoreCase("4763")){
                             currentVal = "47-63";
                             product.put("size", currentVal);
                         }
                     }
                     
                     
                     if(currentVal.isBlank()) continue;
                    
                     if(key.equalsIgnoreCase("additional_imageurl")){
                          String[] additionalImages = currentVal.split(",");
                          for(String url:additionalImages){
                            if(url.isBlank()) continue;
                            setProductKeyVal(key,doc,productElement,url);
                           
                          }
                      
                     }else if(key.equals("size")){
                          
                           setProductKeyVal(key,doc,productElement,currentVal);
                         
                           
                           Element variations = doc.createElement("variations");
                           productElement.appendChild(variations);
                           
                           Element variation = doc.createElement("variation");
                           variations.appendChild(variation);
                           
                           for(String keyV:varKeys){
                                  String currentValVar =product.containsKey(keyV) ? product.get(keyV):"";
                                     
                                  currentValVar=currentValVar!=null?currentValVar:"";

                                    if(currentValVar.length() == 1 && currentValVar.equalsIgnoreCase("-")){
                                        currentValVar = currentValVar.replace("-", "");

                                    }                                  
                                  
                                  if(currentValVar.isBlank()) continue;
                                  setProductKeyVal(keyV,doc,variation,currentValVar);
                           }
                           //hard coded variation fields
                            setProductKeyVal("variationid",doc,variation,ean+"."+product.get("size"));
                            
                            if(product.containsKey("mpn")){
                                setProductKeyVal("manufacturersku",doc,variation,product.get("mpn").replace("-",""));
                            }
                           
                     }else{
                         setProductKeyVal(key,doc,productElement,currentVal);
                     }
                     
                }
            }else{
                skipedProducts++;
                continue;
            }
        }
        if(totalProductAdded < 1000){
            throw new CustomException("Expected Number of Data For creating XML is Less Than Expectd(1000)");
        }
    }
    
    //create product entry
    public void setProductKeyVal(String key,Document doc,Element productElement, String currentVal){
        if(!cdKeys.contains(key)){
                      Element currentElement = doc.createElement(key);
                      currentElement.setTextContent(currentVal);
                      productElement.appendChild(currentElement);
                    }else{
                        Element currentElement = doc.createElement(key);
                        Node cdata = doc.createCDATASection(currentVal);
                        currentElement.appendChild(cdata);
                        productElement.appendChild(currentElement);
                    }
    }  
}
