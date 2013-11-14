package com.nouveauxterritoires.maven.wti;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.jfrog.maven.annomojo.annotations.MojoPhase;

import com.nouveauxterritoires.maven.wti.utils.Constants;

@MojoGoal("pull")
@MojoPhase("generate-sources")
public class Pull extends AbstractMojo {

	@MojoParameter(alias = "file.name", required = false)
    private String fileName = "messages";
	
	@MojoParameter(alias = "project.token", required = true)
    private String projectToken;

	@MojoParameter(alias = "file.id", required = true)
    private String fileId;

	@MojoParameter(alias = "locales", required = true)
    private String locales;
	
	@MojoParameter(alias="output.path", expression="${project.build.directory}/classes", required=false)
	private String outputPath;
	
	@MojoParameter(alias="overwrite.src",required=false)
	private boolean overwriteSrc = false;
	
	@MojoParameter(alias="src.path", expression="${basedir}/src/main/resources",required=false)
	private String srcPath;
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		CloseableHttpResponse response = null;
		getLog().debug("outputPath : " + outputPath);
		try {
			if (projectToken != null && fileId != null && locales != null) {
				StringBuffer url = new StringBuffer(Constants.URL_WTI);
				url.append(projectToken);
				url.append("/files/");
				url.append(fileId);
				url.append("/locales/");
				
				CloseableHttpClient httpclient = HttpClients.createDefault();
				// loop on the locales codes
				getLog().debug("locales : " + locales);
				String[] result = locales.split(",");
				
				HttpGet httpGet = null;
				if (result != null) {
					for (int i = 0; i < result.length; i++) {
						
						String locale = result[i];
						getLog().debug("locale : " + locale);
						String urlGET = url + locale.trim();
						
						getLog().info("url download : " + urlGET);
						httpGet = new HttpGet(urlGET);
						response = httpclient.execute(httpGet);
						
						getLog().debug(response.getStatusLine().toString());
					    HttpEntity entity = response.getEntity();
					    
					    
					    if (entity != null) {
					        String content = EntityUtils.toString(entity);
					        //getLog().debug("PAGE :" + content);
					        
					        // and now save the file
					        File file = new File(outputPath+"/"+ fileName + "_" + locale + ".properties");
					        FileOutputStream fos = new FileOutputStream(file);
					        
					        // if file doesnt exists, then create it
							if (!file.exists()) {
								file.createNewFile();
							}
				 
							// get the content in bytes
							byte[] contentInBytes = content.getBytes();
				 
							fos.write(contentInBytes);
							fos.flush();
							fos.close();
				 
							if (overwriteSrc) {
								getLog().info("Overwrite src");
								// and now save the file
						        file = new File(srcPath+"/"+ fileName + "_" + locale + ".properties");
						        fos = new FileOutputStream(file);
						        
						        // if file doesnt exists, then create it
								if (!file.exists()) {
									file.createNewFile();
								}
					 
								// get the content in bytes
								contentInBytes = content.getBytes();
					 
								fos.write(contentInBytes);
								fos.flush();
								fos.close();
							}
							getLog().info(file.getPath() +  " Done!");
					    }
					    EntityUtils.consume(entity);
					}
				}
			} else {
				getLog().info("wti pull : Nothing to do !");
			}
		} catch (ClientProtocolException e) {
			getLog().error("ClientProtocolException : " + e);
		} catch (IOException e) {
			getLog().error("IOException : " + e);
		} finally {
		    try {response.close();} catch(Exception e){}
		}
		
	}
    
}
