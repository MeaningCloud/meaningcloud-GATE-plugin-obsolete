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


/**
 *
 * @author ADRIAN
 */
public class StilusClient {

public static class result{
public String text="",type="",level="",rule="",msg="",inip="",endp="inip";
public List<suggestion> sug_list = new ArrayList<suggestion>();
public static class suggestion{
    public String form="",confidence="";
}

}
    
    
public static List<result> collectInfo(Element response, String nameNode,gate.AnnotationSet outputAS/*, gate.AnnotationSet original*/) throws InvalidOffsetException, UnsupportedEncodingException {
 
    List<result> result_list = new ArrayList<result>();
    NodeList nodeL = response.getElementsByTagName(nameNode);
        
    for(int i=0; i<nodeL.getLength(); i++) {
        Node result_aux = nodeL.item(i);
        NodeList res_li = result_aux.getChildNodes();
        result r = new StilusClient.result();
        for(int i_it = 0;i_it<res_li.getLength();i_it++){            
            Node result = res_li.item(i_it);
            String res_name = result.getNodeName();
        
          if ("text".equals(res_name)) {
            
                r.text = new String(result.getTextContent().getBytes(),"UTF-8");
          }
          else if ("type".equals(res_name)) {
            
                r.type = new String(result.getTextContent().getBytes(),"UTF-8");
          }
          else if ("rule".equals(res_name)) {
            
                r.rule = new String(result.getTextContent().getBytes(),"UTF-8");
          }
          else if ("msg".equals(res_name)) {
             
                r.msg = new String(result.getTextContent().getBytes(),"UTF-8");
          }
          else if ("sug_list".equals(res_name)) {
            
                NodeList sugs = result.getChildNodes();
                for(int j=0;j<sugs.getLength();j++){
                    Node sug = sugs.item(j);
                    NodeList values = sug.getChildNodes();
                    StilusClient.result.suggestion s = new StilusClient.result.suggestion();
                    for(int k = 0;k<values.getLength();k++){
                        Node features = values.item(k);
                        String name = features.getNodeName();
                        
                        if ("form".equals(name)) {
                          s.form = new String(features.getTextContent().getBytes(),"UTF-8");
                        }
                        else if ("confidence".equals(name)) {
                          s.confidence = new String(features.getTextContent().getBytes(),"UTF-8");
                        }
                    }
                    r.sug_list.add(s);
                }
          }
          else if ("inip".equals(res_name)) {
            
                r.inip = result.getTextContent();
          }
          else if ("endp".equals(res_name)) {
                r.endp = result.getTextContent();

          }  
        }
        result_list.add(r);
    }
    
    return result_list;
}
    
}