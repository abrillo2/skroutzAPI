/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.skroutz.ninjastore.utils;

import gui.UpdateXMLDistributorGui;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author owner
 */
public class SaxHandler extends DefaultHandler {

    private  List<String> eans;
    private  String eanKey;
    private  String stockKey;
    private  String[] productDomKey;
    private  boolean extractAll;
    
    public static String[] priceKeys = {"price","sprzedaz"};
    public static String[] mpnKeys = {"mpn","reference"};
    List<String> priceKeysList=Arrays.asList(priceKeys);
    List<String> mpnKeyList=Arrays.asList(mpnKeys);
    private String storeName = "";

    

    public SaxHandler(List<String> eans,String eanKey,String stockKey,String[] productDomKey,boolean extractAll,String storeName) {
        this.eans = eans;
        this.eanKey = eanKey;
        this.stockKey = stockKey;
        this.productDomKey = productDomKey;
        this.extractAll = extractAll;
        this.storeName = storeName;
        
    }
    public SaxHandler(String[] productDomKey, boolean extractAll,String storeName) {
        this.productDomKey = productDomKey;
        this.extractAll = extractAll;
        this.storeName = storeName;
    }
    

  private StringBuilder currentValue = new StringBuilder();
  
  //to hold list of products extracted for update of the main xml
  public List<Map<String,String>> productUpdateList  = new ArrayList<>();
  public Map<String,String> currentUpdateList =null;
  //to check if the parser is inside the product dom
  public boolean isParsingProduct = false;
  //to check if ean is to be extracted
  public boolean eanFound = false;
  
  public String additional_imageurl = "";
  
  //for parsing variation
  public boolean isVariation = false;  
  public boolean isParsingVariation = false;
  public HashMap<String, String> currenVariation = null;
  public List<Map<String,String>> currentVariationList  = null;
  public HashMap<String, List<Map<String,String>>> AllVariations = null;
  
  public int productCount;


  @Override
  public void startDocument() {
      System.out.println("Start Document ");
      productCount = 0;
      
  }

  @Override
  public void endDocument() {
      System.out.println("End Document");
  }

  @Override
  public void startElement(
          String uri,
          String localName,
          String qName,
          Attributes attributes) {

      // reset the tag value
      currentValue.setLength(0);
      

      if ((Arrays.asList(productDomKey).contains(qName.trim()))) {
          isParsingProduct = true;
          currentUpdateList = new HashMap<>();
          additional_imageurl = "";
          
          if(extractAll){
              productCount++;
              UpdateXMLDistributorGui.loadingText.setText(storeName+" ,Parsing XML Product #"+productCount);
          }
          
          
      }else if(qName.trim().equalsIgnoreCase("variations")){
          isVariation = true;
      }else if(qName.trim().equalsIgnoreCase("variation")){
          isParsingVariation = true;
          currenVariation = new HashMap<>();
      }
      

  }

  @Override
  public void endElement(String uri,
                         String localName,
                         String qName) {
      
      String currenVal = currentValue.toString().trim();

      if(isParsingProduct && extractAll && !isVariation){
          
          
          
          if(!isVariation){
              
             
              
                        if((Arrays.asList(productDomKey).contains(qName.trim()))) {
                            
                          
                            
                           
                
                            currentUpdateList.put("additional_imageurl", additional_imageurl);
                            productUpdateList.add(currentUpdateList);
                            currentUpdateList =null;
                            isParsingProduct = false;

                        }else if("additional_imageurl".equalsIgnoreCase(qName.trim())){
                           additional_imageurl = additional_imageurl+","+currenVal;
                        
                        }else{
                             
                            currentUpdateList.put(qName.trim(), currenVal);
                        }
          
          }else if (qName.equalsIgnoreCase("variations")) {
              isVariation=false;
          
          }
          

          
      }else if(isParsingProduct){
          
          if(!isVariation){
                if(mpnKeyList.contains(qName.strip())){
                    currentUpdateList.put("mpn", currenVal);
                }else if(priceKeysList.contains(qName.strip())){
                    currentUpdateList.put("price", currenVal);
                
                }else if (qName.equalsIgnoreCase(eanKey)) {

                      currentUpdateList.put(eanKey, currenVal);

                      eanFound = eans.contains(currenVal); 

                }else if(qName.equalsIgnoreCase(stockKey)) {
                      int stockInt = HelperClass.convertToInt(currenVal.replace("+", ""));
                      String stockStr = stockInt!=-1?stockInt+"":"0";
                      currentUpdateList.put("stock", stockStr);
                }else if((Arrays.asList(productDomKey).contains(qName.trim()))) {

                      if(eanFound && currentUpdateList.size() >= 2){
                          productCount++;
                          UpdateXMLDistributorGui.loadingText.setText("Parsing XML from "+storeName+" ,Total Located Products #"+productCount);
                          
                          productUpdateList.add(currentUpdateList);
                      }

                     currentUpdateList =null;
                     isParsingProduct = false;
                     eanFound = false;

                }
          }else if (qName.equalsIgnoreCase("variations")) {
              isVariation=false;
          
          }
      }

  }

  // http://www.saxproject.org/apidoc/org/xml/sax/ContentHandler.html#characters%28char%5B%5D,%20int,%20int%29
  // SAX parsers may return all contiguous character data in a single chunk,
  // or they may split it into several chunks
  @Override
  public void characters(char ch[], int start, int length) {

      // The characters() method can be called multiple times for a single text node.
      // Some values may missing if assign to a new string

      // avoid doing this
      // value = new String(ch, start, length);

      // better append it, works for single or multiple calls
      currentValue.append(ch, start, length);

  }

}
