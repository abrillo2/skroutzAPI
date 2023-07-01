/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.skroutz.ninjastore;

import com.skroutz.ninjastore.utils.CustomException;
import com.skroutz.ninjastore.utils.HelperClass;
import gui.UpdateXMLDistributorGui;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.xml.transform.TransformerConfigurationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import prepareBigBuy.ParseBigBuyXML;
import prepareBigBuy.PrePareBigBuyDistributor;
import static prepareBigBuy.ScrapBigBuy.scrapAllTargetList;

/**
 *
 * @author owner
 */
public class NinjaStore {
    


    public static void main(String[] args) throws GitAPIException, URISyntaxException, IOException {

        
        
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            UpdateXMLDistributorGui udg = new UpdateXMLDistributorGui();
            try {
            udg.createAndShowGUI();
            } catch (IOException ex) {
            Logger.getLogger(NinjaStore.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            });
            
            /*String excelUri = "/Users/abraham/Downloads/new products all.xlsx";
            UpdateXMLDistributorGui.loadingText = new JLabel("wtf");
            
            ScrapSkroutz.scrapTargetList(excelUri,5);*/
            /* UpdateXMLDistributorGui.loadingText = new JLabel("wtf");
            ParseBigBuyXML pbx = new ParseBigBuyXML();
            pbx.parseBigBuyProducts();*/
            //UpdateXMLDistributorGui.loadingText = new JLabel("wtf");
            /*HelperClass.updateAvailableity();*/
            
            /*scrapAllTargetList("/Users/abraham/Downloads/download");*/
            
           // PrePareBigBuyDistributor.testBigBuy();
     

        
       
       
       
       
       
    }  
}
