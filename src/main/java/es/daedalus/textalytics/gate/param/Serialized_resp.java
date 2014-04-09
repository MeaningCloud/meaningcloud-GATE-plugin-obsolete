package es.daedalus.textalytics.gate.param;

import gate.Factory;
import gate.FeatureMap;

import java.util.ArrayList;


public class Serialized_resp{
	public Status s= new Status();		
	public FeatureMap doc_fm = Factory.newFeatureMap();
	public ArrayList<Annot> annot_list= new ArrayList<Annot>();
	
	public class Annot{
		public Long inip=0L;
		public Long endp=0L;
		public FeatureMap fm= Factory.newFeatureMap();
	}
	public class Status{
		public int code;
		public String msg;
		public int credits;
		@Override public String toString() {
		      StringBuilder sb = new StringBuilder();
		      sb.append( "Code:" );
		      sb.append(code+"\n");
		      sb.append("Msg:");
		      sb.append(msg+"\n");
		      sb.append( "Credits:" );
		      sb.append(credits+"\n");
		      return sb.toString();
		  }
	}
}
