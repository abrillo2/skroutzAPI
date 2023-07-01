/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

/**
 *
 * @author owner
 */

import com.skroutz.ninjastore.utils.CustomException;
import com.skroutz.ninjastore.utils.ExcelWriter;
import com.skroutz.ninjastore.utils.HelperClass;
import static com.skroutz.ninjastore.utils.HelperClass.parseProductXml;
import static com.skroutz.ninjastore.utils.HelperClass.updateXmlFromExcel;
import com.skroutz.ninjastore.utils.PrepareDistributorData;
import git.UpdateGitRepo;
import static java.awt.Color.white;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import org.eclipse.jgit.api.errors.GitAPIException;
import xml.CreteNinJaXMLFeed;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.xml.transform.TransformerConfigurationException;
import static prepareBigBuy.ScrapBigBuy.scrapAllTargetList;
import scrapData.ScrapSkroutz;


public class UpdateXMLDistributorGui extends JPanel{
	

    public  String wwtFilePath;
    public  String xtgFilePath;
    public  String mobiFilePath;
    public String bigbuyFilePath;
    
    public static String chromeDriverFolder;
    public static String mainDir;
    public static String nsUrl;//"https://abrillo2.github.io/ninjaStore/productList.xml";
    private static String sracpBigBuyFilePath;
    
    public static String sracpFilePath;
    public static int scrapWindowSize=5;
    
    public static String docFolder;
    
    //input labels
    public final String[] inputKeys = {"Select WWT distributor xml","Select Mobiparts distributor Excel file","Select XTG Excel","Select BIGBUY Folder"};
    public final String repoUrl = "https://github.com/abrillo2/ninjaStore";
    public final String accessToken = "";
   
    
    //selected files count
    public  int fileSelectionCount= 0;
    public String productExcelur;
    
    public static JDialog popUp; 
    JPanel labelContainer;
    

   
    
    ImageIcon loading = null;
    
    public static JLabel loadingIcon = null;
    public static JLabel loadingText = null;
    public static JButton loadingButton = null;
    private SwingWorker sw1 = null;
    //label.setVisible(true);
    JFrame frame = new JFrame("Update XML Distributor");
    private List<JTextField> tInputList;
    
    public JPanel createInputsUi(){
       tInputList = new ArrayList<>();
       JPanel inputContainer = new JPanel();
       inputContainer.setLayout(new BoxLayout(inputContainer, BoxLayout.Y_AXIS));
       
       JButton buttonProcess = new JButton("Update XML");
       buttonProcess.setEnabled(false);
       buttonProcess.addActionListener(e -> processButtonPressed(buttonProcess,0));
       
           

       
       for(String key:inputKeys){
            JPanel pannelCont = new JPanel(); 
            JPanel pannel = new JPanel();
            //input label
            JLabel lableInput = new JLabel(key);
          
            
            
            JTextField tInput = new JTextField(16);
            
            tInputList.add(tInput);
            
            tInput.setEnabled(false);
            JButton buttonInput = new JButton("Select");
            
            buttonInput.addActionListener(e -> selectionButtonPressed(key,tInput,buttonProcess));
            
            pannel.add(tInput);
            pannel.add(buttonInput);
            
            pannelCont.setLayout(new BoxLayout(pannelCont, BoxLayout.Y_AXIS));
            pannelCont.add(lableInput);
            pannelCont.add(pannel);
            
            inputContainer.add(pannelCont);
            
       }
       
       
       inputContainer.add(buttonProcess);
       
       return inputContainer;
    }
    
    public JPanel createUpdateUi(){
        
            JPanel pannelCont = new JPanel(); 
            JPanel pannel = new JPanel();
            //input label
            JLabel lableInput = new JLabel("Select Excel File With List Of Products");
            
            JTextField tInput = new JTextField(16);
            tInput.setEnabled(false);
            JButton buttonInput = new JButton("Select");
            
            
            
            JButton buttonProcess = new JButton("Update XML Feed");
            buttonProcess.setEnabled(false);
            buttonProcess.addActionListener(e -> processButtonPressed(buttonProcess,1));
            
            buttonInput.addActionListener(e -> updateButtonPressed(tInput,buttonProcess));
            
            
            
            pannel.add(tInput);
            pannel.add(buttonInput);
            
            pannelCont.setLayout(new BoxLayout(pannelCont, BoxLayout.Y_AXIS));
            pannelCont.add(lableInput);
            pannelCont.add(pannel);
            pannelCont.add(buttonProcess);
            
            return pannelCont;
         
    }
    
    public JPanel createDownloadUi(){
            JPanel pannelCont = new JPanel(); 
            //input label
            JButton buttonProcess = new JButton("Download Product Feed Excel");
            buttonProcess.addActionListener(e -> processButtonPressed(buttonProcess,2));
            pannelCont.setLayout(new BoxLayout(pannelCont, BoxLayout.Y_AXIS));
            pannelCont.add(buttonProcess);
            
            return pannelCont;
    
    }
    
    
    public JPanel createScrapUi(){
            JPanel pannelCont = new JPanel(); 
            JPanel pannel = new JPanel();
            //input label
            JLabel lableInput = new JLabel("Select Excel File With List Of EAN and PRICE for scrapping");
            
            JTextField tInput = new JTextField(26);
            tInput.setEnabled(false);
            JButton buttonInput = new JButton("Select");
            
            // array of choices
            String choices[] = { "5", "6", "7", "8", "9" };
            //input label
            JLabel lableChoice = new JLabel("Select Number of Browser Windows To Open at a time");
            JComboBox jc = new JComboBox(choices);
            jc.setSelectedIndex(0);
            jc.addActionListener(e-> comboBoxPressed(jc ));
            
            
            
            JButton buttonProcess = new JButton("Scrapp Skroutz");
            buttonProcess.setEnabled(false);
            buttonProcess.addActionListener(e -> processButtonPressed(buttonProcess,3));
            
            buttonInput.addActionListener(e -> scrappButtonPressed(tInput,buttonProcess));
            
            
            
            pannel.add(tInput);
            pannel.add(buttonInput);

            
            
            pannelCont.setLayout(new BoxLayout(pannelCont, BoxLayout.Y_AXIS));
            pannelCont.add(lableInput);
            pannelCont.add(pannel);
            pannelCont.add(lableChoice);
            pannelCont.add(jc);
            pannelCont.add(buttonProcess);
            
            return pannelCont;
    }
    
    //create ui for scrapping big buy data
    public JPanel createScrappUiBigBuy(){
        
            JPanel pannelCont = new JPanel(); 
            JPanel pannel = new JPanel();
            //input label
            JLabel lableInput = new JLabel("Select A Folder Containing BIG BUY product EXCEL");
            
            JTextField tInput = new JTextField(16);
            tInput.setEnabled(false);
            JButton buttonInput = new JButton("Select");
            
            
            
            JButton buttonProcess = new JButton("Start Processing");
            buttonProcess.setEnabled(false);
            buttonProcess.addActionListener(e -> processButtonPressed(buttonProcess,4));
            
            buttonInput.addActionListener(e -> scrappBigBuyButtonPressed(tInput,buttonProcess));
            
            
            
            pannel.add(tInput);
            pannel.add(buttonInput);
            
            pannelCont.setLayout(new BoxLayout(pannelCont, BoxLayout.Y_AXIS));
            pannelCont.add(lableInput);
            pannelCont.add(pannel);
            pannelCont.add(buttonProcess);
            
            return pannelCont;
         
    }
    
   
    
    
    public void createAndShowGUI() throws MalformedURLException, IOException {
        
        //Create and set up the window.
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        URL url = new URL("https://abrillo2.github.io/ninjaStore/Loading_icon.gif");
       
        loading = new ImageIcon(url);
        
        labelContainer = new JPanel();
        
        
        loadingIcon = new JLabel(loading);
        loadingText = new JLabel("processing...");
        loadingButton = new JButton("close");
        loadingButton.addActionListener(e -> closeModal());
        loadingButton.setEnabled(false);
        
        loadingIcon.setHorizontalAlignment(JLabel.CENTER);
        loadingText.setHorizontalAlignment(JLabel.CENTER);
        
       
        labelContainer.setLayout(new GridBagLayout());
        labelContainer.setBackground(white);
       
        GridBagConstraints gbc = new GridBagConstraints();  
        
        gbc.gridx = 0;  
        gbc.gridy = 0;  
        labelContainer.add(loadingIcon,gbc);
        gbc.gridx = 0;  
        gbc.gridy = 1;          
        labelContainer.add(loadingText,gbc);
        gbc.gridx = 0;  
        gbc.gridy = 2;          
        labelContainer.add(loadingButton,gbc);
        
        labelContainer.setMinimumSize(new Dimension(200,200) );
        
        
        popUp = new JDialog(frame , "Processing", true);
        popUp.setMinimumSize(new Dimension(600,500));
        popUp.add(labelContainer);
        //popUp.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        popUp.addWindowListener(new java.awt.event.WindowAdapter() {
    
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    if(sw1!=null){
                        sw1.cancel(true);
                    }
                }
        });
        
        
        
        
        
        
        
      
        
        //file input selects container panel
        JPanel inputContainer = new JPanel();
        inputContainer.add(createInputsUi());
        
        //update xml excel panel
        JPanel updateXmlExcelPanel = new JPanel();
        updateXmlExcelPanel.add(createUpdateUi());
        
        
        //download xml excel panel
        JPanel downloadXmlExcelPanel = new JPanel();
        downloadXmlExcelPanel.add(createDownloadUi());
        
        //scrap skroutz excel panel
        JPanel scrappSkroutzPanel = new JPanel();
        scrappSkroutzPanel.add(createScrapUi());
        
        //scrapp big buy panel
        JPanel scrappBigBuyPanel = new JPanel();
        scrappBigBuyPanel.add(createScrappUiBigBuy());
        
        
        JTabbedPane tp=new JTabbedPane();  
        tp.setBounds(50,50,200,200);  
        tp.add("Update XML - Distributor",inputContainer);  
        tp.add("Update XML - Excel",updateXmlExcelPanel);  
        tp.add("Download XML - Excel",downloadXmlExcelPanel);    
        tp.add("Scrap Skroutz - Excel",scrappSkroutzPanel);   
        tp.add("Process Big Buy Data",scrappBigBuyPanel);   
        
        
        
        frame.add(tp);
        frame.setPreferredSize(new Dimension(700,500));
        frame.pack();
        frame.setVisible(true);
        
        
    
    }

    private void selectionButtonPressed(String key, JTextField tInput, JButton buttonProcess) {
        
        FileNameExtensionFilter filter = null;
        JFileChooser jFileInputChooser = new JFileChooser(mainDir);
        jFileInputChooser.setAcceptAllFileFilterUsed(false);
        
        int labelIndex = 0;
        if(key.equalsIgnoreCase(inputKeys[0])) {
	    filter = new FileNameExtensionFilter("XML Files", "xml");  
            labelIndex =0;
	}else if(key.equalsIgnoreCase(inputKeys[1])) {
	    filter = new FileNameExtensionFilter("Excel Files", "xlsx"); 
            labelIndex = 1;
	}else if(key.equalsIgnoreCase(inputKeys[2])){
            filter = new FileNameExtensionFilter("Excel Files", "xlsx"); 
            labelIndex = 2;
        }else if(key.equalsIgnoreCase(inputKeys[3])){
            filter = null;
            labelIndex = 3;
        }
        
        if(filter != null){
            jFileInputChooser.addChoosableFileFilter(filter);
        }else{
            jFileInputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        
        int fileInput = jFileInputChooser.showOpenDialog(this);
        if (fileInput == JFileChooser.APPROVE_OPTION){
            
            String inputPath = jFileInputChooser.getSelectedFile().getAbsolutePath();
                    tInput.setText(inputPath);
            File file = new File(inputPath);
            String parent = file.getParent();
            mainDir =  parent;
                            //set field val
            switch (labelIndex){
                case 0:
                    wwtFilePath = inputPath;
                    break;
                case 1:
                    mobiFilePath = inputPath;
                    break;
                case 2:
                    xtgFilePath = inputPath;
                    break;
                case 3:
                    bigbuyFilePath = inputPath;
                    break;
                default:
                    break;
            }
            
            fileSelectionCount++;
            
            buttonProcess.setEnabled(true);
                    
                  
        }
        

        
        
        
        
        
    }

    private void processButtonPressed(JButton buttonProcess,int tabIndex) {
        
                    
                   
                   
                    String homeDir = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
                    
                    docFolder = homeDir + "/"+"NinjaStore_Product_Feed";
                    chromeDriverFolder = docFolder+"/Driver";
                    nsUrl = docFolder + "/ninjaStoreRepo/"+"productList.xml";
                    try {
                        Files.createDirectories(Paths.get(docFolder));
                        Files.createDirectories(Paths.get(chromeDriverFolder));
                    } catch (IOException ex) {
                        //Logger.getLogger(UpdateXMLDistributorGui.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    

                    
                    
                  
                    sw1 = new SwingWorker() 
                    {
                        private boolean errorr;
              
                        @Override
                        protected String doInBackground() throws Exception 
                        {
                            
                            try{
                                this.errorr = false;
                                HelperClass.testConnection();
                                switch(tabIndex){
                                    case 0:
                                        updateXMLFeedDistributor();
                                        break;
                                    case 1:
                                        updateXMLFeedExcel();
                                        break;
                                    case 2:
                                        downloadProductExcel();
                                        break;
                                     case 3:
                                        scrapData();
                                        break;
                                     case 4:
                                         ScrapBigBuy();
                                         break;
                                     default:  
                                        break;
                                }
                            }catch(Exception e){
                                this.errorr = true;
                                String eMessage = e.getMessage();
                                eMessage = eMessage.contains("google.com")?"No Internet Connection":eMessage;
                                loadingText.setText("<html><h2>Error Encounted</h2> <br/><h3>"+eMessage+"</h3></html>");
                            }
                                
                            String res = "Finished Execution";
                            
                            return res;
                        }
              
                        @Override
                        protected void process(List chunks)
                        {
                        }
              
                        @Override
                        protected void done() 
                        {
                            String titile = this.errorr?"Erorr":"Done";
                            loadingButton.setEnabled(true);
                            loadingIcon.setVisible(false);
                            popUp.setTitle(titile);
                           
                        }




                    };
                      
                    // executes the swingworker on worker thread
                    sw1.execute(); 
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            loadingIcon.setVisible(true);
                            loadingButton.setEnabled(false);
                            popUp.setVisible(true);
                            popUp.setTitle("Processing...");
                        }
                    });
                    
    }

    private void updateButtonPressed(JTextField tInput, JButton buttonProcess) {
        
        FileNameExtensionFilter filter = null;
        JFileChooser jFileInputChooser = new JFileChooser(mainDir);
        jFileInputChooser.setAcceptAllFileFilterUsed(false);
        
        filter = new FileNameExtensionFilter("Excel Files", "xlsx");
        jFileInputChooser.addChoosableFileFilter(filter);
        
        int fileInput = jFileInputChooser.showOpenDialog(this);
        if (fileInput == JFileChooser.APPROVE_OPTION){
            buttonProcess.setEnabled(true);
            String inputPath = jFileInputChooser.getSelectedFile().getAbsolutePath();
            tInput.setText(inputPath);
            
            productExcelur = inputPath;
            
            File file = new File(inputPath);
            String parent = file.getParent();
            mainDir =  parent;
                    
            
        }
    }
    
    private void scrappButtonPressed(JTextField tInput, JButton buttonProcess) {
        
        FileNameExtensionFilter filter = null;
        JFileChooser jFileInputChooser = new JFileChooser(mainDir);
        jFileInputChooser.setAcceptAllFileFilterUsed(false);
        
        filter = new FileNameExtensionFilter("Excel Files", "xlsx");
        jFileInputChooser.addChoosableFileFilter(filter);
        
        int fileInput = jFileInputChooser.showOpenDialog(this);
        if (fileInput == JFileChooser.APPROVE_OPTION){
            buttonProcess.setEnabled(true);
            String inputPath = jFileInputChooser.getSelectedFile().getAbsolutePath();
            tInput.setText(inputPath);
            
            sracpFilePath = inputPath;
            
            File file = new File(inputPath);
            String parent = file.getParent();
            mainDir =  parent;
                    
            
        }
    }
    
    
    private void scrappBigBuyButtonPressed(JTextField tInput, JButton buttonProcess) {
        FileNameExtensionFilter filter = null;
        JFileChooser jFileInputChooser = new JFileChooser(mainDir);
        jFileInputChooser.setAcceptAllFileFilterUsed(false);
        
        jFileInputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int fileInput = jFileInputChooser.showOpenDialog(this);
        if (fileInput == JFileChooser.APPROVE_OPTION){
            buttonProcess.setEnabled(true);
            String inputPath = jFileInputChooser.getSelectedFile().getAbsolutePath();
            tInput.setText(inputPath);
            
            sracpBigBuyFilePath = inputPath;
            
            File file = new File(inputPath);
            String parent = file.getParent();
            mainDir =  parent;
                    
            
        }
    }
    
    public void updateXMLFeedDistributor() throws GitAPIException, IOException, URISyntaxException, TransformerConfigurationException, InterruptedException, CustomException{
                                /**
                             * 
                             * Clone Repo 
                             */
                            
                            String repoDir = docFolder + "/ninjaStoreRepo";
                            UpdateGitRepo ugr = new UpdateGitRepo(repoDir,repoUrl,accessToken);
                            ugr.cloneRepo();
                            
                            /**
                             * read distributor product list
                             */
                            PrepareDistributorData pdd = new PrepareDistributorData(xtgFilePath,wwtFilePath,mobiFilePath,bigbuyFilePath);
                            pdd.setAllData();
                            pdd.updateProductStock();
                            
                          
                            
                            /**
                             * create xml file to push*/
                             
                            CreteNinJaXMLFeed cnxf = new CreteNinJaXMLFeed(pdd.getNinjaStoreData());
                            cnxf.feedXML(repoDir+"/"+"productList.xml");

                          
                            ExcelWriter.writeExcel(pdd.getNinjaStoreData(),docFolder+"/Ninja Store Product Listed.xlsx",cnxf.productFeedKeys);
                            
                            
                            //update store xml
                            ugr.pushToRepo();
                            
                            this.wwtFilePath =null;
                            this.xtgFilePath =null;
                            this.mobiFilePath =null;
                            clearInputList();
    }
    
   private void updateXMLFeedExcel() throws GitAPIException, IOException, URISyntaxException, TransformerConfigurationException, InterruptedException, CustomException {
       
                             /**
                             * 
                             * Clone Repo 
                             */
                            String repoDir = docFolder + "/ninjaStoreRepo";
                            UpdateGitRepo ugr = new UpdateGitRepo(repoDir,repoUrl,accessToken);
                            ugr.cloneRepo();
                            
                            updateXmlFromExcel(productExcelur,repoDir+"/"+"productList.xml");
                            
                            //update store xml
                            ugr.pushToRepo();
   
   }
   
   private void downloadProductExcel() throws GitAPIException, IOException {
       
                                    /**
                             * 
                             * Clone Repo 
                             */
                            String repoDir = docFolder + "/ninjaStoreRepo";
                            UpdateGitRepo ugr = new UpdateGitRepo(repoDir,repoUrl,accessToken);
                            ugr.cloneRepo();
       
       
       String[] productDomKey = {"product"};
        
       List<Map<String, String>>  productList = parseProductXml(productDomKey,nsUrl);
      
      
       
       ExcelWriter.writeExcel(productList,docFolder+"/Product List Downlaoded.xlsx",CreteNinJaXMLFeed.productFeedKeys);
   }
   
   
   private void scrapData() throws IOException{
   
        File ChromeDriverPathFull = new File(chromeDriverFolder+"/chromedriver.exe");
                    
        if(!ChromeDriverPathFull.exists()){
          loadingText.setText("Chrome Webdriver Could Not Be Located");
          return;
        }else{
          loadingText.setText("Located Chrome WebDriver...");
        }
        ScrapSkroutz.scrapTargetList(sracpFilePath,scrapWindowSize,chromeDriverFolder+"/chromedriver.exe");
        
   }

    private void closeModal() {
        popUp.setVisible(false);
        
    }

    private void comboBoxPressed(JComboBox jc) {
        try{
            scrapWindowSize = (Integer) jc.getSelectedItem();
            
        }catch(Exception e){
            scrapWindowSize = 5;
        }
    }
    
    private void clearInputList(){
         for(JTextField JTF:tInputList){
             JTF.setText("");
         }   
    }
    
    private void ScrapBigBuy() throws IOException {
        scrapAllTargetList(sracpBigBuyFilePath,chromeDriverFolder+"/chromedriver.exe");
    }
}
