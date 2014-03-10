package daedalus.textalytics.gate.param;

import gate.Factory;
import gate.FeatureMap;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SentimentBean {
	public class Entityform{
		String entity;
		@Override public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Entity:");
			sb.append(entity+"\n");
			return sb.toString();
		}
	}
	public class Conceptform{
		String concept;
		@Override public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Entity:");
			sb.append(concept+"\n");
			return sb.toString();
		}
	}
	public class Entity{
		String text;
		double score;
		String score_tag;
		double sd;
		String sd_tag;
		@Override public String toString() {
		      StringBuilder sb = new StringBuilder();
		      sb.append( "Entity:[\n" );
		      sb.append("\t text\n");
		      sb.append("\t"+text+"\n");
		      if(score_tag!=null){
		    	  sb.append("\t score_tag\n");
		          sb.append("\t"+score_tag+"\n");
		      }
		      if(score_tag!=null){
		          sb.append("\t sd_tag\n");
		          sb.append("\t"+sd_tag+"\n");
		      }
		      sb.append("]");
		      return sb.toString();
		  }
	}
	public class Concept{
		String text;
		double score;
		String score_tag;
		double sd;
		String sd_tag;
		@Override public String toString() {
		      StringBuilder sb = new StringBuilder();
		      sb.append( "Entity:[\n" );
		      sb.append("\t text\n");
		      sb.append("\t"+text+"\n");
		      if(!score_tag.isEmpty()){
		    	  sb.append("\t score_tag\n");
		          sb.append("\t"+score_tag+"\n");
		      }
		      if(!sd_tag.isEmpty()){
		          sb.append("\t sd_tag\n");
		          sb.append("\t"+sd_tag+"\n");
		      }
		      sb.append("]");
		      return sb.toString();
		  }
	}

	public class Keyword{
		String text;
		double score;
		String score_tag;
		Entityform[] entity_list = null;
		Conceptform[] concept_list = null;
		@Override public String toString() {
		      StringBuilder sb = new StringBuilder();
		      sb.append( "KW_text:\n" );
		      sb.append(text+"\n");
		      sb.append( "KW_score:\n" );
		      sb.append(score_tag+"\n");
		      sb.append("Entities:{\n");
		      for(Entityform e: entity_list)
		    	  sb.append(e.toString());
		      sb.append("},\nConcepts:{\n");
		      if(this.concept_list!=null){
		    	  for(Conceptform c: concept_list)
		    		  sb.append(c.toString());
		      }
		      sb.append("}\n");
		      return sb.toString();
		  }
	}

	public class Segment{
		Long inip;
		Long endp;
		String text;
		double score;
		String score_tag;
		double sd;
		String sd_tag;
		String subjectivity;
		String irony;
		Keyword[] keyword_list= null;
		
				
		@Override public String toString() {
		      StringBuilder sb = new StringBuilder();
		      sb.append( "inip:\n" );
		      sb.append(inip+"\n");
		      sb.append( "endp:\n" );
		      sb.append(endp+"\n");
		      sb.append( "text:\n" );
		      sb.append(text+"\n");
		      sb.append( "score_tag:\n" );
		      sb.append(score_tag+"\n");
		      sb.append( "sd_tag:\n" );
		      sb.append(sd_tag+"\n");
		      sb.append( "subjectivity:\n" );
		      sb.append(subjectivity+"\n");
		      sb.append( "irony:\n" );
		      sb.append(irony+"\n");
		      sb.append("Keywords:{\n");
		      for(Keyword k: keyword_list)
		    	  sb.append(k.toString());
		      return sb.toString();
		  }
	}


	public class Sent_data{
		public Serialized_resp.Status status;
		public String model;
		public double score;
		public String score_tag;
		public double sd;
		public String sd_tag;
		public String subjectivity;
		public String irony;
		public Segment[] segment_list = null;
		public Entity[] entity_list = null;
		public Concept[] concept_list = null;
		@Override public String toString() {
		      StringBuilder sb = new StringBuilder();
		      sb.append("Status:\n");
		      sb.append(status.toString());
		      sb.append( "model:\n" );
		      sb.append(model+"\n");
		      sb.append( "score_tag:\n" );
		      sb.append(score_tag+"\n");
		      sb.append( "sd_tag:\n" );
		      sb.append(sd_tag+"\n");
		      sb.append( "subjectivity:\n" );
		      sb.append(subjectivity+"\n");
		      sb.append( "irony:\n" );
		      sb.append(irony+"\n");
		      sb.append("Keywords:\n");
		      for(Segment s: segment_list)
		    	  sb.append(s.toString());
		      sb.append("},\nEntities:{\n");
		      for(Entity e : entity_list)
		    	  sb.append(e.toString());
		      sb.append("},\nConcepts:{\n");
		      for(Concept c: concept_list)
		    	  sb.append(c.toString());
		      sb.append("}\n");
		      return sb.toString();
		  }
		public Serialized_resp serialize() throws UnsupportedEncodingException{
			Serialized_resp sr = new Serialized_resp();
			
			sr.s = this.status;
			FeatureMap doc_fm = Factory.newFeatureMap();
			if(model!=null)
				doc_fm.put("model", new String(model.getBytes(),"UTF-8"));
			Double  d = new Double(score);
			if(d!=null)
				doc_fm.put("score", d==null ? "" : d.toString());
			if(score_tag!=null)
				doc_fm.put("score_tag", new String(score_tag.getBytes(),"UTF-8"));
			d = new Double(sd);
			if(d!=null)
				doc_fm.put("sd", d==null ? "" :d.toString());
			if(sd_tag!=null)
				doc_fm.put("sd_tag", new String(sd_tag.getBytes(),"UTF-8"));
			if(subjectivity!=null)
				doc_fm.put("subjectivity", new String(subjectivity.getBytes(),"UTF-8"));
			if(irony!=null)
				doc_fm.put("irony", new String(irony.getBytes(),"utf-8"));
			int cnt = 0;
			if(entity_list!=null){
				String entity_text="",entity_score="",entity_score_tag="",entity_sd="",entity_sd_tag="";
				for(Entity e :entity_list){
					if(cnt==0){
						if(e.text!=null)
							//doc_fm.put("entity_"+cnt+"_text", new String(e.text.getBytes(),"UTF-8"));
							entity_text=new String(e.text.getBytes(),"UTF-8");
						Double s = new Double(e.score);
						if(s!=null)
							//doc_fm.put("entity_"+(cnt+1)+"_score", e.score);
							entity_score=s.toString();
						if(e.score_tag!=null)
							//doc_fm.put("entity_"+(cnt+1)+"_score_tag", new String(e.score_tag.getBytes(),"UTF-8"));
							entity_score_tag=new String(e.score_tag.getBytes(),"UTF-8");
						s = new Double(e.sd);
						if(s!=null)
							//doc_fm.put("entity_"+(cnt+1)+"_sd", e.sd);
							entity_sd=s.toString();
						if(e.sd_tag!=null)
							//doc_fm.put("entity_"+(cnt+1)+"_sd_tag", new String(e.sd_tag.getBytes(),"UTF-8"));
							entity_sd_tag=new String(e.sd_tag.getBytes(),"UTF-8");
					}else{
						if(e.text!=null)
							entity_text+=";"+new String(e.text.getBytes(),"UTF-8");
						Double s = new Double(e.score);
						if(s!=null)
							entity_score+=";"+Double.toString(e.score);
						if(e.score_tag!=null)
							entity_score_tag+=";"+new String(e.score_tag.getBytes(),"UTF-8");
						s = new Double(e.sd);
						if(s!=null)
							entity_sd+=";"+Double.toString(e.sd);
						if(e.sd_tag!=null)
							entity_sd_tag+=";"+new String(e.sd_tag.getBytes(),"UTF-8");
					}
					cnt++;
				}
				if(!entity_text.isEmpty())
					doc_fm.put("entity_text", entity_text);
				if(!entity_score.isEmpty())
					doc_fm.put("entity_score", entity_score);
				if(!entity_score_tag.isEmpty())
					doc_fm.put("entity_score_tag", entity_score_tag);
				if(!entity_sd.isEmpty())
					doc_fm.put("entity_sd", entity_sd);
				if(!entity_sd_tag.isEmpty())
					doc_fm.put("entity_sd_tag", entity_sd_tag);
			}
			cnt = 0;
			if(concept_list!=null){
				String concept_text="",concept_score="",concept_score_tag="",concept_sd="",concept_sd_tag="";
				for(Concept c :concept_list){
					if(cnt>0){
						concept_text+=";";
						concept_score+=";";
						concept_score_tag+=";";
						concept_sd+=";";
						concept_sd_tag+=";";
					}
					if(c.text!=null)
						//doc_fm.put("concept_"+(cnt+1)+"_text", new String(c.text.getBytes(),"UTF-8"));
						concept_text+=new String(c.text.getBytes(),"UTF-8");
					Double s = new Double(c.score);
					if(s!=null)
						//doc_fm.put("concept_"+(cnt+1)+"_score", c.score);
						concept_score+=Double.toString(c.score);
					if(c.score_tag!=null)
						//doc_fm.put("concept_"+(cnt+1)+"_score_tag", new String(c.score_tag.getBytes(),"UTF-8"));
						concept_score_tag+=new String(c.score_tag.getBytes(),"UTF-8");
					s = new Double(c.sd);
					if(s!=null)
						//doc_fm.put("concept_"+(cnt+1)+"_sd", c.sd);
						concept_sd+=Double.toString(c.sd);
					if(c.sd_tag!=null)
						//doc_fm.put("concept_"+(cnt+1)+"_sd_tag", new String(c.sd_tag.getBytes(),"UTF-8"));
						concept_sd_tag+=new String(c.sd_tag.getBytes(),"UTF-8");
					cnt++;
				}
				doc_fm.put("concept_text", concept_text);
				doc_fm.put("concept_score", concept_score);
				doc_fm.put("concept_score_tag", concept_score_tag);
				doc_fm.put("concept_sd", concept_sd);
				doc_fm.put("concept_sd_tag", concept_sd_tag);
			}
			
			sr.doc_fm = doc_fm;
			
			cnt = 0;
			if(segment_list!=null){
				for(Segment s : segment_list){
					Serialized_resp.Annot at = sr.new Annot();			
					at.inip = s.inip;
					at.endp = s.endp+1;
					if(s.text!=null)
						//System.out.println("Segment "+cnt+"_text: "+s.text);
						at.fm.put(/*"segment"+cnt+*/"text",s.text.isEmpty() ?"":new String(s.text.getBytes(),"UTF-8"));	
					Double sc = new Double(s.score);
					if(sc!=null)
						at.fm.put(/*"segment"+cnt*/"score", sc==null ?"":s.score);
					if(s.score_tag!=null)
						at.fm.put(/*"segment"/*+cnt*/"score_tag", s.score_tag.isEmpty() ?"":new String(s.score_tag.getBytes(),"UTF-8"));
					sc = new Double(s.sd);
					if(sc!=null)
						at.fm.put(/*"segment"+cnt*/"sd", sc==null?"":s.sd);
					if(s.sd_tag!=null)
						at.fm.put(/*"segment"+cnt*/"sd_tag", s.sd_tag.isEmpty()?"":new String(s.sd_tag.getBytes(),"UTF-8"));
					if(s.subjectivity!=null)
						at.fm.put(/*"segment"/*+cnt*/"subjectivity", s.subjectivity.isEmpty()?"":new String(s.subjectivity.getBytes(),"UTF-8"));
					if(s.irony!=null)
						at.fm.put(/*"segment"+cnt*/"irony", s.irony.isEmpty()?"":new String(s.irony.getBytes(),"UTF-8"));
					int cnt2 = 0;
					if(s.keyword_list!=null){
						String keyword_text="",keyword_score="",keyword_score_tag="",keyword_entities="",keyword_concepts="";
						for(Keyword k : s.keyword_list){
							if(cnt2>0){
								keyword_text+=";";
								keyword_score+=";";
								keyword_score_tag+=";";
								keyword_entities+=";";
								keyword_concepts+=";";
							}
							if(k.text!=null)
								//at.fm.put(/*"segment"/*+cnt*/"keyword_"+(cnt2+1)+"_text", new String(k.text.getBytes(),"UTF-8"));
								keyword_text+=new String(k.text.getBytes(),"UTF-8");
							Double sco = new Double(k.score);
							if(sco!=null)
								//at.fm.put(/*"segment"/*+cnt*/"keyword_"+(cnt2+1)+"_score", k.score);
								keyword_score += Double.toString(k.score);
							if(k.score_tag!=null)
								//at.fm.put(/*"segment"/*+cnt*/"keyword_"+(cnt2+1)+"_score_tag", new String(k.score_tag.getBytes(),"UTF-8"));
								keyword_score_tag+=new String(k.score_tag.getBytes(),"UTF-8");
							int cnt3=0;
							if(k.entity_list!=null){
							for(Entityform e:k.entity_list){
								if(e.entity!=null){
									if(cnt3>0){
										keyword_entities+="|";
									}
									//at.fm.put(/*"segment"/*+cnt*/"keyword_"+(cnt2+1)+"_entity_"+(cnt3+1),new String(e.entity.getBytes(),"UTF-8"));
									keyword_entities+=new String(e.entity.getBytes(),"UTF-8");
								}
								cnt3++;
							}
							}
							cnt3=0;
							if(k.concept_list!=null){
								for(Conceptform e:k.concept_list){
									if(e.concept!=null){
										if(cnt3>0)
											keyword_concepts+="|";
										//at.fm.put(/*"segment"/*+cnt*/"keyword_"+(cnt2+1)+"_concept_"+(cnt3+1),new String(e.concept.getBytes(),"UTF-8"));
										keyword_concepts+=new String(e.concept.getBytes(),"UTF-8");
									}
									cnt3++;
								}
							}
							cnt2++;
						}
						at.fm.put("keyword_text",keyword_text);
						at.fm.put("keyword_score",keyword_score);
						at.fm.put("keyword_score_tag",keyword_score_tag);
						at.fm.put("keyword_entities",keyword_entities);
						at.fm.put("keyword_concepts",keyword_concepts);
					}
					cnt++;
					sr.annot_list.add(at);
					}
				}
				return sr;
		}
		
	}
	
	
}
