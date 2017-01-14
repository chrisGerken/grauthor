package com.gerken.xaa.model.refimpl;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class ModelElement {

	public ModelElement() {
	}
	
	public String xmlRepresentation() {
		StringBuffer sb = new StringBuffer();
		writeTo(sb);		
		return sb.toString();
	}

	public abstract void writeTo(StringBuffer sb);

	public void writeAttr(StringBuffer sb, String name, String value) {
		sb.append(" ");
		sb.append(name);
		sb.append("=\"");
		sb.append(deEntify(value));
		sb.append("\"");
	}

	private String deEntify(String buffer) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buffer.length(); i++) {
			char c = buffer.charAt(i);
			if (c == '&') { sb.append("&amp;"); } 
			else if (c == '<') { sb.append("&lt;"); }
			else if (c == '>') { sb.append("&gt;"); }
			else if (c == '\'') { sb.append("&apos;"); }
			else if (c == '"') { sb.append("&quot;"); }
			else {sb.append(c); }
		}
		return sb.toString();
	}

	public Node[] getChildren(Node node) {
		NodeList list = node.getChildNodes();
		Node kid[] = new Node[list.getLength()];
		for (int i = 0; i < kid.length; i++) {
			kid[i] = list.item(i);
		}
		return kid;
	}

	public String getAttr(Node node, String name) {
		String value = "";
		try {
			value = node.getAttributes().getNamedItem(name).getNodeValue();
		} catch (Throwable t) {}
		return value;
	}
	
	public int getIntAttr(Node node, String name) {
		return Integer.parseInt(getAttr(node,name));
	}
	
	public boolean getBooleanAttr(Node node, String name) {
		return Boolean.parseBoolean(getAttr(node,name));
	}

}
