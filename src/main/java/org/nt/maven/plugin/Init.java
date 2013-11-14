package org.nt.maven.plugin;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.jfrog.maven.annomojo.annotations.MojoPhase;
import org.nt.maven.plugin.utils.Constants;

@MojoGoal("init")
@MojoPhase("generate-sources")
public class Init extends AbstractMojo {

	@MojoParameter(alias = "file.name", required = false)
    private String fileName = "messages";
	
	@MojoParameter(alias = "project.token", required = true)
    private String projectToken;

	@MojoParameter(alias = "file.id", required = true)
    private String fileId;

	@MojoParameter(alias = "locales", required = true)
    private String locales;
    
	@MojoParameter(expression="${basedir}/src/main/resources",required=false)
	private String srcPath;
	
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		StringBuffer url = new StringBuffer(Constants.URL_WTI);
		url.append(projectToken);
		url.append("/files");
		
		// The execution:
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			// loop on the locales codes
		
			getLog().debug("locales : " + locales);
			String[] result = locales.split(",");
			
			HttpPost httpPost = null;
			if (result != null) {
				for (int i = 0; i < result.length; i++) {
					
					String locale = result[i];
					getLog().debug("locale : " + locale);
					String urlPost = url.toString();
					
					getLog().info("url upload : " + urlPost);
					httpPost = new HttpPost(urlPost);
					
					MultipartEntityBuilder builder = MultipartEntityBuilder.create();
					builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
					
					getLog().debug(srcPath+"/"+ fileName + "_" + locale + ".properties");
					File file = new File(srcPath+"/"+ fileName + "_" + locale + ".properties");
				    FileBody fb = new FileBody(file);
				    
				    
					builder.addTextBody("name", fileName + "_" + locale + ".properties", ContentType.TEXT_PLAIN);
					builder.addPart("file", fb);
					
					
					httpPost.setEntity(builder.build());
					 
					HttpResponse response = httpclient.execute(httpPost);
					getLog().info("response code : " + response.getStatusLine());
					
				}
				
			}
		} catch (ClientProtocolException e) {
			getLog().error("ClientProtocolException : " + e);
		} catch (IOException e) {
			getLog().error("IOException : " + e);
		}
		
		
		
	}

}
