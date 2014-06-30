package org.ender.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdaterConfig {
    private static final String EXTRACT = "extract";
    private static final String ITEM = "item";
    private static final String ARCH = "arch";
    private static final String OS = "os";
    private static final String FILE = "file";
    private static final String LINK = "link";
    
    public String mem, res, server, jar;
    public static File dir = new File(System.getProperty("user.home"), "/Salem/bin");

    List<Item> items = new ArrayList<UpdaterConfig.Item>();

    public UpdaterConfig(){
	if(!dir.exists()){
	    dir.mkdirs();
	}
	File cfg = new File("config.xml");
	InputStream stream = null;
	if(cfg.exists() && cfg.isFile()) {
	    try {
		stream = new FileInputStream(cfg);
	    } catch (FileNotFoundException ignore) {}
	} 
	if(stream == null) {
	    stream = UpdaterConfig.class.getResourceAsStream("/config.xml");
	}

	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder;
	try {
	    builder = factory.newDocumentBuilder();
	    Document doc = builder.parse(stream);
	    stream.close();
	    
	    NamedNodeMap attrs = doc.getDocumentElement().getAttributes();
	    mem = attrs.getNamedItem("mem").getNodeValue();
	    res = attrs.getNamedItem("res").getNodeValue();
	    server = attrs.getNamedItem("server").getNodeValue();
	    jar = attrs.getNamedItem("jar").getNodeValue();

	    NodeList groupNodes = doc.getElementsByTagName(ITEM);
	    for (int i = 0; i < groupNodes.getLength(); i++) {
		Item itm = parseItem(groupNodes.item(i));
		if (itm != null)
		    items.add(itm);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private Item parseItem(Node node) {
	Item itm = new Item();
	if (node.getNodeType() != Node.ELEMENT_NODE)
	    return null;
	Element el = (Element) node;

	itm.link = el.getAttribute(LINK);
	if(el.hasAttribute(FILE)){
	    itm.file = new File(dir, el.getAttribute(FILE));
	} else {
	    int i = itm.link.lastIndexOf("/");
	    itm.file = new File(dir, itm.link.substring(i+1));
	}
	itm.os = el.getAttribute(OS);
	itm.arch = el.getAttribute(ARCH);
	String e = el.getAttribute(EXTRACT);
	if(e.length() > 0){itm.extract = new File(dir, e);}
	return itm;
    }

    public static class Item{
	public String arch;
	public String os;
	public File file;
	public String link;
	public long date = 0;
	public long size = 0;
	public File extract = null;

    }
}
