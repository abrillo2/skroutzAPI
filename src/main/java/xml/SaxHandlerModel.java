/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xml;

import com.skroutz.ninjastore.utils.SaxHandler;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author owner
 */
public class SaxHandlerModel {

    private List<String> eans;
    private String[] productDomKey;
    private String eanKey;
   
    private SAXParser saxParser ;
    private SaxHandler saxHandler;
    private String stockKey;
    private String url;
    private String storeName = "";
    
    
    
    public SaxHandlerModel(String[] productDomKey){
        this.productDomKey = productDomKey;       
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    
    

    public void setEans(List<String> eans) {
        this.eans = eans;
    }

    public void setProductDomKey(String[] productDomKey) {
        this.productDomKey = productDomKey;
    }

    public void setEanKey(String eanKey) {
        this.eanKey = eanKey;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public void setFilename(String fileName){
       this.url = fileName;
    }

    public void setStockKey(String stockKey) {
        this.stockKey = stockKey;
    }
    
    
    
    
    
    
    public void setSaxHandlerAll(){
        this.saxHandler = new SaxHandler(productDomKey,true,this.storeName);
    }
    
    public void setSaxHandlerFilter(){
        this.saxHandler = new SaxHandler(eans,eanKey,stockKey,productDomKey,false,this.storeName);
    }
    
    public void setSaxParser(){
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            this.saxParser = factory.newSAXParser();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SaxHandlerModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(SaxHandlerModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<Map<String, String>> getProductUpdateList(){
        try {
            this.setSaxParser();
            
            //if(!isUrl){
            this.saxParser.parse(url,this.saxHandler);
            /*}else{
                this.saxParser.parse(new InputSource(new URL(this.url).openStream()),this.saxHandler);
            }*/
            
            return this.saxHandler.productUpdateList;
        } catch (MalformedURLException ex) {
            Logger.getLogger(SaxHandlerModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SaxHandlerModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(SaxHandlerModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
        
    }
    
    
    
}
