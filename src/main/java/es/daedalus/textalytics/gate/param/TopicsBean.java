package es.daedalus.textalytics.gate.param;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.daedalus.textalytics.gate.TextalyticsParser;
import es.daedalus.textalytics.gate.param.TokenBean.Token;


public class TopicsBean {
	public Serialized_resp.Status status;
	public Entity_json[] entity_list = null;
	public Entity_json[] concept_list = null;
	public Time_expression[] time_expression_list = null;
	public Money_expression[] money_expression_list = null;
	public Uri[] uri_list = null;
	public Phone_expression[] phone_expression_list = null;
	public Other[] other_expression_list = null;
	public Quotation[] quotation_list = null;
	public Relation[] relation_list = null;
	
	public static class Entity_json{
		public String form,dictionary,id,relevance;
		public Sementity[] sementity_list = null;
		public Semgeo[] semgeo_list = null;
		public String[] semld_list = null;
		public Semrefer[] semrefer_list = null;
		public Semtheme[] semtheme_list = null;
		public Standard[] standard_list = null;
		public Variant variant_list = null;
	
	public static class Sementity{
		public String sementity_class, fiction,id,confidence,type;
	}

	public static class Semgeo{
	    public static class Element{
	        public static class Std{
	        	public String name,value;
	        }
	        public String form,id,name;
	        public Std[] std_list = null;
	    }
	    public Element[] semgeo_list = null; 
	}

	public static class Semrefer{
	    public static class Organization{
	    	public String form,id;
	    }
	    public static class Affinity{
	    	public String form,id;
	    }
	    
	    public Organization[] organization_list = null;
	    public Affinity[] affinity_list = null;
	}

	public static class Semtheme{
		public String id,type;
	}

	public static class Standard{
		public String value;
	}

	public static class Variant{
		public String form,inip,endp;
	}
}
	
	public static class Time_expression{
		String form,normalized_form,actual_time,precision;
		Long inip,endp;		
	}

	public static class Money_expression{
		String form,amount,numeric_value,currency;
		Long inip,endp;		
	}

	public static class Uri{
		String form,type;
		Long inip,endp;		
	}
	
	public static class Phone_expression{
		String form;
		Long inip,endp;		
	}
	
	public static class Other{
		String form,type;
		Long inip,endp;		
	}
	
	public static class Quotation{
		String form,who,who_lemma,verb,verb_lemma;
		Long inip,endp;		
	}
	
	public static class Relation{
		String form, degree;
		Long inip,endp;	
		Subject subject;
		Verb verb;
		Complement[] complement_list;
		public static class Subject{
			String form;
			String[] lemma_list = null;
			String[] sense_id_list = null;
		}
		public static class Verb{
			String form;
			String[] lemma_list = null;
			String[] sense_id_list = null;
			String[] semantic_lemma_list = null;
		}
		public static class Complement{
			String form,type,degree;
		}
	}
	
	public static class Entity{
		public String form="",dictionary="",id="",relevance="";
		public List<sementity> sementity_list = new ArrayList<sementity>();
		public List<semgeo> semgeo_list = new ArrayList<semgeo>();
		public List<semld> semld_list = new ArrayList<semld>();
		public List<semrefer> semrefer_list = new ArrayList<semrefer>();
		public List<semtheme> semtheme_list = new ArrayList<semtheme>();
		public List<standard> standard_list = new ArrayList<standard>();
		public List<variant> variant_list = new ArrayList<variant>();
	//}

	public static class sementity{
		public String sementity_class="", sementity_fiction="",sementity_id="",sementity_confidence="",sementity_type="";
	}

	public static class semgeo{
	    public static class element{
	        public static class std{
	        	public String name="",value="";
	        }
	        public String form="",id="",name="";
	        public List<std> stdArray = new ArrayList<std>();
	    }
	    public List<element> semgeo_list = new ArrayList<element>(); 
	}

	public static class semld{
		public String semld="";
		public String sourceDic = "";
	}

	public static class semrefer{
	    public static class organization{
	    	public String form="",id="";
	    }
	    public static class affinity{
	    	public String form="",id="";
	    }
	    
	    public List<organization> organization_list = new ArrayList<organization>();
	    public List<affinity> affinity_list = new ArrayList<affinity>();
	}

	public static class semtheme{
		public String id="",type="";
	}

	public static class standard{
		public String name="",value="";
	}

	public static class variant{
		public String form="",inip="",endp="";
	}
}	

	public String toString(){
		String ret = "";
		
		if(entity_list!=null){
			ret += "Entity [";
			int cnt = 0;
			for(Entity_json e: entity_list){
				try{
					if(cnt==0)
						ret+=new String(e.form.getBytes(),"utf-8");
					else
						ret+=";"+new String(e.form.getBytes(),"utf-8");
				}catch(Exception ex){
					Logger.getLogger(TopicsBean.class.getName()).log(Level.SEVERE, null, ex);
				}
				cnt++;
			}
		ret+="]";
		}
		
		if(this.concept_list!=null){
			if(ret.length()>0)
				ret+=",";
			ret += "Concept [";
			int cnt = 0;
			for(Entity_json e: concept_list){
				try{
				if(cnt==0)
					ret+=new String(e.form.getBytes(),"utf-8");
				else
					ret+=";"+new String(e.form.getBytes(),"utf-8");
				}catch(Exception ex){
					Logger.getLogger(TopicsBean.class.getName()).log(Level.SEVERE, null, ex);
				}
				cnt++;
			}
		ret+="]";
		}
		
		if(this.money_expression_list!=null){
			if(ret.length()>0)
				ret+=",";
			ret += "MoneyExpr [";
			int cnt = 0;
			for(Money_expression e: money_expression_list){
				try{
				if(cnt==0)
					ret+=new String(e.form.getBytes(),"utf-8");
				else
					ret+=";"+new String(e.form.getBytes(),"utf-8");
				}catch(Exception ex){
					Logger.getLogger(TopicsBean.class.getName()).log(Level.SEVERE, null, ex);
				}
				cnt++;
			}
		ret+="]";
		}
		
		if(this.time_expression_list!=null){
			if(ret.length()>0)
				ret+=",";
			ret += "TimeExpr [";
			int cnt = 0;
			for(Time_expression e: time_expression_list){
				try{
				if(cnt==0)
					ret+=new String(e.form.getBytes(),"utf-8");
				else
					ret+=";"+new String(e.form.getBytes(),"utf-8");
				}catch(Exception ex){
					Logger.getLogger(TopicsBean.class.getName()).log(Level.SEVERE, null, ex);
				}
				cnt++;
			}
		ret+="]";
		}
		
		if(this.uri_list!=null){
			if(ret.length()>0)
				ret+=",";
			ret += "URI [";
			int cnt = 0;
			for(Uri e: uri_list){
				try{
				if(cnt==0)
					ret+=new String(e.form.getBytes(),"utf-8");
				else
					ret+=";"+new String(e.form.getBytes(),"utf-8");
				}catch(Exception ex){
					Logger.getLogger(TopicsBean.class.getName()).log(Level.SEVERE, null, ex);
				}
				cnt++;
			}
		ret+="]";
		}
		
		if(this.phone_expression_list!=null){
			if(ret.length()>0)
				ret+=",";
			ret += "Phone [";
			int cnt = 0;
			for(Phone_expression e: phone_expression_list){
				try{
				if(cnt==0)
					ret+=new String(e.form.getBytes(),"utf-8");
				else
					ret+=";"+new String(e.form.getBytes(),"utf-8");
				}catch(Exception ex){
					Logger.getLogger(TopicsBean.class.getName()).log(Level.SEVERE, null, ex);
				}
				cnt++;
			}
		ret+="]";
		}
		
		if(this.other_expression_list!=null){
			if(ret.length()>0)
				ret+=",";
			ret += "Other [";
			int cnt = 0;
			for(Other e: other_expression_list){
				try{
				if(cnt==0)
					ret+=new String(e.form.getBytes(),"utf-8");
				else
					ret+=";"+new String(e.form.getBytes(),"utf-8");
				}catch(Exception ex){
					Logger.getLogger(TopicsBean.class.getName()).log(Level.SEVERE, null, ex);
				}
				cnt++;
			}
		ret+="]";
		}
		
		if(this.quotation_list!=null){
			if(ret.length()>0)
				ret+=",";
			ret += "Quotation [";
			int cnt = 0;
			for(Quotation e: quotation_list){
				try{
				if(cnt==0)
					ret+=new String(e.form.getBytes(),"utf-8");
				else
					ret+=";"+new String(e.form.getBytes(),"utf-8");
				}catch(Exception ex){
					Logger.getLogger(TopicsBean.class.getName()).log(Level.SEVERE, null, ex);
				}
				cnt++;
			}
		ret+="]";
		}
		
		if(this.relation_list!=null){
			if(ret.length()>0)
				ret+=",";
			ret += "Relation [";
			int cnt = 0;
			for(Relation e: relation_list){
				try{
				if(cnt==0)
					ret+=new String(e.form.getBytes(),"utf-8");
				else
					ret+=";"+new String(e.form.getBytes(),"utf-8");
				}catch(Exception ex){
					Logger.getLogger(TopicsBean.class.getName()).log(Level.SEVERE, null, ex);
				}
				cnt++;
			}
		ret+="]";
		}
		return ret;
	}
}
