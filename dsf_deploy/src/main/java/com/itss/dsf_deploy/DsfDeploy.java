package com.itss.dsf_deploy;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DsfDeploy {

	public static void main(String[] args) throws ParserConfigurationException, TransformerException,IOException {
		Logger logger = LogManager.getLogger(DsfDeploy.class);
		logger.info("Deploying DSF Packages");
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

		Properties prop = new Properties();
		prop.load(DsfDeploy.class.getClassLoader().getResourceAsStream("dsfDeploy.properties"));
		
		// Retrieving path for DSF packages
		String basePath = prop.getProperty("com.itss.dsf.package.path");
		logger.info("Searching for packages in " + basePath);

		// Retrieving DSF API details
		String dsfHoststring = prop.getProperty("com.itss.dsf.api.hoststring");
		String dsfAction = prop.getProperty("com.itss.dsf.api.action");
		String dsfApiUrl = prop.getProperty("com.itss.dsf.api.url");
		String dsfParameters = prop.getProperty("com.itss.dsf.api.parameters");
		String dsfUrl = "http://" + dsfHoststring + "/" + dsfApiUrl + "/" + dsfAction + dsfParameters;
		logger.info("Using the following url: " + dsfUrl);
		
		//Retrieving zip files from package directory
		File dsfPackageFolder = new File(basePath);
		if (!dsfPackageFolder.exists()) {
			logger.error("Folder " + basePath + " not found");
			return;
		}
		FilenameFilter zipFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".zip")) {
					return true;
				} else {
					return false;
				}
			}
		};
		
		File[] listOfPackages = dsfPackageFolder.listFiles(zipFilter);
		int packagesFound = listOfPackages.length;
		logger.info("Packages found: " + packagesFound);
		if (packagesFound > 0) {
			logger.info("Creating directory for processed packages");
			String processPath = basePath + "Processed\\" + formatter.format(ts);
			File processDir = new File(processPath);
			if (!processDir.exists()) {
				processDir.mkdirs();
			}
			try {
				int errorCode, packageCount;
				packageCount = 0;
				for (File file : listOfPackages ) {
					logger.info("Deploying package: " + file.getName() + " (" + ++packageCount + " of " + packagesFound + ")");
					errorCode = sendPackage(dsfUrl,file);
					if (errorCode == 200) {
						logger.info("Package result: " + errorCode + " OK");
					}
					else {
						logger.error("Package result: " + errorCode);
					}
					Files.move(Paths.get(file.getAbsolutePath()), Paths.get(processPath + "\\" + file.getName()));
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
		} else {
			logger.error("No packages found!");
		}
	}
	
	private static int sendPackage(String url, File file) {
		try {
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			entityBuilder.addPart("file", new FileBody(file));
			HttpEntity entity = entityBuilder.build();
			HttpResponse returnResponse = Request.Post(url)
	                .body(entity)
	                .execute().returnResponse();
			return returnResponse.getStatusLine().getStatusCode();
		} catch (Exception e) {
			return 503;
		}
	}
}
