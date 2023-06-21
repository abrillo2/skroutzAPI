package git;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.skroutz.ninjastore.utils.HelperClass;
import gui.UpdateXMLDistributorGui;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;


/**
 *
 * @author owner
 */
public class UpdateGitRepo {
    
    private final String repoDir;
    private final String repoUrl;
    
    
    UsernamePasswordCredentialsProvider credentialsProvider;
    
    
    public UpdateGitRepo(String repoDir,String repoUrl,String accessToken){
        
        UpdateXMLDistributorGui.loadingText.setText("Getting NinjaStore XML Feed");
        
        this.repoDir = repoDir;
        this.repoUrl = repoUrl;
        this.credentialsProvider = new UsernamePasswordCredentialsProvider("PRIVATE-TOKEN",accessToken);
                
    }
    
    
    public void cloneRepo() throws GitAPIException, IOException{
        //FileUtils.deleteDirectory(new File(repoDir));
        File f = new File(repoDir+"/productList.xml");
        if(f.exists()){ 
            // do something
        }else {

                // Not present or not a Git repository.
                Git git = Git.cloneRepository()
                .setURI(repoUrl)
                .setCredentialsProvider(credentialsProvider)
                .setDirectory(new File(repoDir))
                .call();
                git.getRepository().close();
                git.close();
        }
       
        

    }
    
    public void pushToRepo() throws IOException, URISyntaxException, GitAPIException{
           
           
            Git git = Git.open(new File(repoDir)); 
            
            //create zip of xml
            HelperClass.zipFile(repoDir+"/productList.zip",repoDir+"/productList.xml");
            
            git.add().addFilepattern(".").call();
            git.commit().setMessage("product list "+new Date()).call();
            
             // add remote repo:
            RemoteAddCommand remoteAddCommand = git.remoteAdd();
            remoteAddCommand.setName("origin");
            remoteAddCommand.setUri(new URIish(repoUrl));
            // you can add more settings here if needed
            remoteAddCommand.call();
            
            
            // push to remote:
            PushCommand pushCommand = git.push();
            pushCommand.setCredentialsProvider(credentialsProvider);
            // you can add more settings here if needed
            pushCommand.call();
            
            
            UpdateXMLDistributorGui.loadingText.setText("<html><h2>NinJa Store Product Feed XML Updated<h2>"
                    + "<br/>"+UpdateXMLDistributorGui.loadingText.getText()+"</html>");
            
            git.getRepository().close();
            git.close();
           
            
           
    }
    
}
