package com.itss.modulexml_writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ModuleWriter {

	public static void main(String[] args) throws ParserConfigurationException, TransformerException,IOException {
		Logger logger = LogManager.getLogger(ModuleWriter.class);
		logger.info("Generating module.xml file");
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Properties prop = new Properties();
		prop.load(ModuleWriter.class.getClassLoader().getResourceAsStream("mwxml.properties"));

		
		Document doc = docBuilder.newDocument();
		Element moduleRoot = doc.createElement("module");
		moduleRoot.setAttribute("xmlns", "urn:jboss:module:1.0");
		moduleRoot.setAttribute("name", "com.temenos.t24");
		Element resources = doc.createElement("resources");
		Element resource;
		
		logger.debug("Reading properties from mwxml.properties");
		
		//Fetch core T24 libraries
		String libPath = prop.getProperty("stdlibPath");
		File stdlibFolder = new File(libPath);
		logger.debug("Fetching core T24 libraries from: " + libPath);
		File[] listOfStdLibFiles = stdlibFolder.listFiles();
		try {
			logger.debug("Files found: " + listOfStdLibFiles.length);
			for (File file : listOfStdLibFiles) {
				if (file.isFile()) {
					resource = doc.createElement("resource-root");
					resource.setAttribute("path", "./" + prop.getProperty("stdlibPrefix") + "/" + file.getName());
					resources.appendChild(resource);
				}
			}
		}
		catch (NullPointerException e1) {
			logger.error("Could not obtain Core T24 libraries");
			logger.debug("Files found: 0");
		}
		
		//Fetch local development T24 libraries
		libPath = prop.getProperty("l3libPath");
		File l3libFolder = new File(libPath);
		logger.debug("Fetching l3 T24 libraries from: " + libPath);
		File[] listOfL3LibFiles = l3libFolder.listFiles();
		try {
			logger.debug("Files found: " + listOfL3LibFiles.length);
			for (File file : listOfL3LibFiles) {
				if (file.isFile()) {
					resource = doc.createElement("resource-root");
					resource.setAttribute("path", "./" + prop.getProperty("l3libPrefix") + "/" + file.getName());
					resources.appendChild(resource);
				}
			}
		} catch (NullPointerException e2) {
			logger.error("Could not obtain l3 T24 libraries");
			logger.debug("Files found: 0");
		}

		doc.appendChild(moduleRoot);
		moduleRoot.appendChild(resources);
		Element dependencies = doc.createElement("dependencies");
		moduleRoot.appendChild(dependencies);
		Element dpnModule = doc.createElement("module");
		dpnModule.setAttribute("name", "com.temenos.tafj");
		dependencies.appendChild(dpnModule);
		
		String outputFilename = prop.getProperty("outputFilename");
		try (FileOutputStream output = new FileOutputStream(outputFilename)) {
			WriteXml(doc,output);
			logger.debug("File " + outputFilename + " generated successfully...");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void WriteXml(Document doc, FileOutputStream output) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(output);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		transformer.transform(source,result);
	}
}