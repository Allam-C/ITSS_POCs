package com.itss;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * TODO: Document me!
 *
 * @author allam
 *
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) throws ParserConfigurationException, TransformerException {
        // TODO Auto-generated method stub
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        
        Document doc = docBuilder.newDocument();
        Element moduleRoot = doc.createElement("module");
        moduleRoot.setAttribute("xmlns", "urn:jboss:module:1.0");
        moduleRoot.setAttribute("name", "com.temenos.t24");
        doc.appendChild(moduleRoot);

        
        Element resources = doc.createElement("resources");
        Element resource = doc.createElement("resource-root");
        moduleRoot.appendChild(resources);
        resources.appendChild(resource);
        resource.setAttribute("path", "path/to/lib");
        
        moduleRoot.appendChild(doc.createElement("dependencies"));
        
        try (FileOutputStream output = new FileOutputStream("module.xml")) {
            writeXml(doc,output);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param doc
     * @param output
     */
    private static void writeXml(Document doc, FileOutputStream output) throws TransformerException{
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);
        
        transformer.transform(source, result);
        
    }

}
