package com.itss.mavenpoc;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class moduleWriter {

	public static void main(String[] args) throws ParserConfigurationException, TransformerException,IOException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Properties prop = readPropertiesFile("mwxml.properties");
		
		Document doc = docBuilder.newDocument();
		Element moduleRoot = doc.createElement("module");
		moduleRoot.setAttribute("xmlns", "urn:jboss:module:1.0");
		moduleRoot.setAttribute("name", "com.temenos.t24");
		doc.appendChild(moduleRoot);
		
		Element resources = doc.createElement("resources");
		Element resource;
		moduleRoot.appendChild(resources);
		
		//Fetch core T24 libraries
		File stdlibFolder = new File(prop.getProperty("stdlibPrefix"));
		File[] listOfStdLibFiles = stdlibFolder.listFiles();
		
		for (File file : listOfStdLibFiles) {
			if (file.isFile()) {
				resource = doc.createElement("resource-root");
				resource.setAttribute("path", "./" + prop.getProperty("stdlibPrefix") + "/" + file.getName());
				resources.appendChild(resource);

			}
		}
		
		//Fetch local development T24 libraries
		File l3libFolder = new File(prop.getProperty("l3libPrefix"));
		File[] listOfL3LibFiles = l3libFolder.listFiles();
		
		for (File file : listOfL3LibFiles) {
			if (file.isFile()) {
				resource = doc.createElement("resource-root");
				resource.setAttribute("path", "./" + prop.getProperty("l3libPrefix") + "/" + file.getName());
				resources.appendChild(resource);

			}
		}

		
		
		moduleRoot.appendChild(doc.createElement("dependencies"));
		
		try (FileOutputStream output = new FileOutputStream(prop.getProperty("outputFilename"))) {
			WriteXml(doc,output);
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

		transformer.transform(source,result);
	}

	private static Properties readPropertiesFile(String fileName) throws IOException {
	      FileInputStream fis = null;
	      Properties prop = null;
	      try {
	         fis = new FileInputStream(fileName);
	         prop = new Properties();
	         prop.load(fis);
	      } catch(FileNotFoundException fnfe) {
	         fnfe.printStackTrace();
	      } catch(IOException ioe) {
	         ioe.printStackTrace();
	      } finally {
	         fis.close();
	      }
	      return prop;
	   }	
	
}
