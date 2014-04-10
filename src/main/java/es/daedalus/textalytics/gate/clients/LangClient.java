/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.daedalus.textalytics.gate.clients;

import gate.util.InvalidOffsetException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 * @author ADRIAN
 */
public class LangClient {
    

public static List<String> collectInfo(Element response) throws InvalidOffsetException, UnsupportedEncodingException {
    
    List<String> a =  new ArrayList<String>();
              
    NodeList nodeL = response.getElementsByTagName("lang");
    for(int i=0; i<nodeL.getLength(); i++) {
        Node lang = nodeL.item(i);
        String value_name = lang.getNodeName();
            if ("lang".equals(value_name)){
              a.add(new String(lang.getTextContent().getBytes(),"UTF-8"));
            }
    }
    return a;
}
}