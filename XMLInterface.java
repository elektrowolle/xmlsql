package com.roman.ppaper.helpers.xmlsql;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;
import android.util.Xml.Encoding;

import com.roman.ppaper.helpers.Node;



public class XMLInterface{
	
	
	public static Node parse(InputStream xml) {
		ContentHandler handler;
		
		Node root;
		Stack<Node> nodeStack;
		
		root = new Node("root");
		nodeStack = new Stack<Node>();
		nodeStack.push(root);
		
		handler = new SAXContentHandler(root, nodeStack);
		
		try {
			android.util.Xml.parse(xml, Encoding.UTF_8, handler);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return root;
	}
	
	public static InputStream openAsset(String file, Context context){
		try {
			return context.getAssets().open(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("sql", e.getMessage());
		}
		return null;
	}
	
	static class SAXContentHandler implements ContentHandler{
		
		Node root;
		Stack<Node> nodeStack;
		StringBuffer buffer;
		
		public SAXContentHandler(Node root, Stack<Node> nodeStack) {
			super();
			this.root = root;
			this.nodeStack = nodeStack;
		}

		@Override
		public void setDocumentLocator(Locator locator) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void startDocument() throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void endDocument() throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void startPrefixMapping(String prefix, String uri)
				throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void endPrefixMapping(String prefix) throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			// TODO Auto-generated method stub
			Node newNode = new Node(localName);
			
			for(int i = 0; i < atts.getLength(); i++){
				newNode.putAttribute(atts.getQName(i), atts.getValue(i));
			}
			
			nodeStack.lastElement().put(newNode);
			nodeStack.push(newNode);
			
			buffer = new StringBuffer();
			
			Log.d("xml", uri + " | " + localName + " | " + qName + " | ");
			for(int i = 0; i < atts.getLength(); i++)
				Log.d("xml", "att(" + atts.getLocalName(i) + "): " + atts.getValue(i));
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// TODO Auto-generated method stub
			Node lastElement = nodeStack.lastElement();
			lastElement.setValue(buffer.toString());
			Log.d("xml", "\\ " + uri + " " + localName + " | found data: " + lastElement.getValue());
			nodeStack.pop();
			
			
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			buffer.append(ch);
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void processingInstruction(String target, String data)
				throws SAXException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void skippedEntity(String name) throws SAXException {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
