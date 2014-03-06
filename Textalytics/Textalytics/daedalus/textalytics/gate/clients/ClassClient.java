/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daedalus.textalytics.gate.clients;

import gate.util.InvalidOffsetException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import daedalus.textalytics.gate.TextalyticsClass;
import daedalus.textalytics.gate.TextalyticsClass.category;
import daedalus.textalytics.gate.TextalyticsClass.category.term;

import com.google.gson.Gson;

/**
 *
 * @author ADRIAN
 */
public class ClassClient {
    

public static List<category> collectInfo(Element response) throws InvalidOffsetException, UnsupportedEncodingException {
    
    List<category> a =  new ArrayList<category>();
              
    NodeList nodeL = response.getElementsByTagName("category");
    for(int i=0; i<nodeL.getLength(); i++) {
        Node category = nodeL.item(i);
        NodeList features = category.getChildNodes();
        daedalus.textalytics.gate.TextalyticsClass.category cat = new daedalus.textalytics.gate.TextalyticsClass.category();
        for(int j=0;j<features.getLength();j++){
            Node value = features.item(j);
            String value_name = value.getNodeName();
            if ("code".equals(value_name)) {
                
                    cat.code = new String(value.getTextContent().getBytes(),"UTF-8");
            }
            else if ("label".equals(value_name)) {
                    cat.label = new String(value.getTextContent().getBytes(),"UTF-8");
            }
            else if ("abs_relevance".equals(value_name)) {
                  
                    cat.abs_relevance = new String(value.getTextContent().getBytes(),"UTF-8");
            }
            else if ("relevance".equals(value_name)) {
                    cat.relevance = new String(value.getTextContent().getBytes(),"UTF-8");
            }
            else if ("term_list".equals(value_name)) {
            	System.out.println("There are terms");
                	NodeList terms = value.getChildNodes();
                    cat.term_list  = new ArrayList<term>(terms.getLength());
                    for(int k=0;k<terms.getLength();k++){
                        daedalus.textalytics.gate.TextalyticsClass.category.term t = new daedalus.textalytics.gate.TextalyticsClass.category.term();
                        Node term = terms.item(k);
                        NodeList term_values = term.getChildNodes();
                        for(int l=0;l<term_values.getLength();l++){
                            Node v = term_values.item(l);
                            String term_name = v.getNodeName();

                            if("form".equals(term_name)) {
                              t.form = new String(v.getTextContent().getBytes(), "UTF-8");
                            } else if("abs_relevance".equals(term_name)) {
                              t.abs_relevance = new String(v.getTextContent().getBytes(), "UTF-8");
                            }
                        }
                    cat.term_list.add(t);
                    }
            }
            
        }
        a.add(cat);
    }
    return a;
}


}