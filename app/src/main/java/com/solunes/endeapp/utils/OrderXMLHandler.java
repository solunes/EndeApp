package com.solunes.endeapp.utils;

/**
 * Created by jhonlimaster on 12-02-17.
 */

import android.util.Log;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OrderXMLHandler extends DefaultHandler {
    private static final String TAG = "OrderXMLHandler";

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        Log.e(TAG, "startElement: " + localName);
        Log.e(TAG, "startElement: " + qName);
        currentElement = true;
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        Log.e(TAG, "endElement: " + localName);
        Log.e(TAG, "endElement: " + qName);
        value = "";
        currentElement = false;
    }

    boolean currentElement;
    String value = "";

    public void characters(char[] ch, int start, int length) throws SAXException {
        value += new String(ch, start, length);
        Log.e(TAG, "characters: " + value.trim());
    }

}
