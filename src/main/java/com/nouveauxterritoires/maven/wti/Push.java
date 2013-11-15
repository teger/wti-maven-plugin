package com.nouveauxterritoires.maven.wti;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
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

import com.nouveauxterritoires.maven.wti.utils.Constants;

@MojoGoal("push")
@MojoPhase("generate-sources")
public class Push extends AbstractMojo {

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
		url.append("/files/");
		url.append(fileId);
		url.append("/locales/");
		
		// The execution:
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			// loop on the locales codes
		
			getLog().debug("locales : " + locales);
			String[] result = locales.split(",");
			
			HttpPut httpPut = null;
			if (result != null) {
				for (int i = 0; i < result.length; i++) {
					
					String locale = result[i];
					getLog().debug("locale : " + locale);
					String urlPUT = url + locale.trim();
					
					getLog().info("url upload : " + urlPUT);
					httpPut = new HttpPut(urlPUT);
					
					MultipartEntityBuilder builder = MultipartEntityBuilder.create();
					builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
					
					getLog().debug(srcPath+"/"+ fileName + "_" + locale + ".properties");
					File file = new File(srcPath+"/"+ fileName + "_" + locale + ".properties");
				    FileBody fb = new FileBody(file);
				    
					builder.addTextBody("title", fileName + "_" + locale + ".properties", ContentType.TEXT_PLAIN);
					builder.addTextBody("desc", "", ContentType.TEXT_PLAIN);
					builder.addPart("file", fb);
					
					httpPut.setEntity(builder.build());
					 
					HttpResponse response = httpclient.execute(httpPut);
					getLog().info("response code : " + response.getStatusLine());
					
					
					/*BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				    String body = "";
				    String content = "";

				    while ((body = rd.readLine()) != null) 
				    {
				        content += body + "\n";
				    }
				    return content.trim();*/
				    
				    
				}
				
			}
		} catch (ClientProtocolException e) {
			getLog().error("ClientProtocolException : " + e);
		} catch (IOException e) {
			getLog().error("IOException : " + e);
		}
		
		
		
	}

}
