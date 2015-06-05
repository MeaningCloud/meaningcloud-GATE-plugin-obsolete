package es.daedalus.meaningcloud.gate.param;

import gate.Factory;
import gate.FeatureMap;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class SentimentBean {
	public class SentimentedEntity {
		String form, variant, type, score_tag;
		Long inip, endp;

		@Override
		public String toString() {
			HashMap<String, Object> map = new HashMap();
			if (form != null)
				map.put("form", form);
			if (variant != null)
				map.put("variant", variant);
			if (type != null)
				map.put("type", type);
			if (score_tag != null)
				map.put("score_tag", score_tag);
			if (inip!= null)
				map.put("inip", inip);
			if (endp != null)
				map.put("endp", endp);
			return map.toString();
		}
	}

	public class SentimentedConcept {
		String form, variant, type, score_tag;
		Long inip, endp;

		@Override
		public String toString() {
			HashMap<String, Object> map = new HashMap();
			if (form != null)
				map.put("form", form);
			if (variant != null)
				map.put("variant", variant);
			if (type != null)
				map.put("type", type);
			if (score_tag != null)
				map.put("score_tag", score_tag);
			if (inip!= null)
				map.put("inip", inip);
			if (endp != null)
				map.put("endp", endp);
			return map.toString();
		}
	}

	public class PolarityTerm {
		String text, tag_stack, confidence, score_tag;
		Long inip, endp;
		SentimentedEntity[] sentimented_entity_list = null;
		SentimentedConcept[] sentimented_concept_list = null;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("PT_text:\n");
			sb.append(text + "\n");
			sb.append("PT_tag_stack:\n");
			sb.append(tag_stack + "\n");
			sb.append("PT_confidence:\n");
			sb.append(confidence + "\n");
			sb.append("PT_score_tag:\n");
			sb.append(score_tag + "\n");
			sb.append("Sentimented Entities:{\n");
			if (this.sentimented_entity_list != null) {
				for (SentimentedEntity e : sentimented_entity_list)
					sb.append(e.toString());
			}
			sb.append("},\nSentimented Concepts:{\n");
			if (this.sentimented_concept_list != null) {
				for (SentimentedConcept c : sentimented_concept_list)
					sb.append(c.toString());
			}
			sb.append("}\n");
			return sb.toString();
		}
	}

	public class Segment {
		Long inip;
		Long endp;
		Long confidence;
		String text;
		String segment_type;
		String score_tag;
		String agreement;
		PolarityTerm[] polarity_term_list = null;
		Segment[] segment_list = null;
		SentimentedEntity[] sentimented_entity_list = null;
		SentimentedConcept[] sentimented_concept_list = null;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("inip:\n");
			sb.append(inip + "\n");
			sb.append("endp:\n");
			sb.append(endp + "\n");
			sb.append("text:\n");
			sb.append(text + "\n");
			sb.append("segment type:\n");
			sb.append(segment_type + "\n");
			sb.append("score_tag:\n");
			sb.append(score_tag + "\n");
			sb.append("confidence:\n");
			sb.append(confidence + "\n");
			sb.append("agreement:\n");
			sb.append(agreement + "\n");
			if(polarity_term_list != null) {
				sb.append("Polarity Terms:{\n");
				for (PolarityTerm k : polarity_term_list)
					sb.append(k.toString());
			}
			if(segment_list != null) {
				sb.append("},\nSegments:{\n");
				for (Segment k : segment_list)
					sb.append(k.toString());
			}
			if(sentimented_entity_list != null) {
				sb.append("},\nSentimented Entities:{\n");
				for (SentimentedEntity k : sentimented_entity_list)
					sb.append(k.toString());
			}
			if(sentimented_concept_list != null) {
				sb.append("},\nSentimented Concepts:{\n");
				for (SentimentedConcept k : sentimented_concept_list)
					sb.append(k.toString());
			}
			sb.append("}\n");
			return sb.toString();
		}
	}
	
	public class Sentence {
		String text, bop, confidence, score_tag, agreement;
		Long inip, endp;
		Segment[] segment_list = null;
		SentimentedEntity[] sentimented_entity_list = null;
		SentimentedConcept[] sentimented_concept_list = null;
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("inip:\n");
			sb.append(inip + "\n");
			sb.append("endp:\n");
			sb.append(endp + "\n");
			sb.append("text:\n");
			sb.append(text + "\n");
			sb.append("score_tag:\n");
			sb.append(score_tag + "\n");
			sb.append("confidence:\n");
			sb.append(confidence + "\n");
			sb.append("agreement:\n");
			sb.append(agreement + "\n");
			if(segment_list != null) {
				sb.append("},\nSegments:{\n");
				for (Segment k : segment_list)
					sb.append(k.toString());
			}
			if(sentimented_entity_list != null) {
				sb.append("},\nSentimented Entities:{\n");
				for (SentimentedEntity k : sentimented_entity_list)
					sb.append(k.toString());
			}
			if(sentimented_concept_list != null) {
				sb.append("},\nSentimented Concepts:{\n");
				for (SentimentedConcept k : sentimented_concept_list)
					sb.append(k.toString());
			}
			sb.append("}\n");
			return sb.toString();
		}
	}

	public class Sent_data {
		public Serialized_resp.Status status;
		public String model;
		public String score_tag;
		public String agreement;
		public String confidence;
		public String subjectivity;
		public String irony;
		public Sentence[] sentence_list = null;
		public SentimentedEntity[] sentimented_entity_list = null;
		public SentimentedConcept[] sentimented_concept_list = null;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Status:\n");
			sb.append(status.toString());
			sb.append("model:\n");
			sb.append(model + "\n");
			sb.append("agreement:\n");
			sb.append(agreement + "\n");
			sb.append("confidence:\n");
			sb.append(confidence + "\n");
			sb.append("score_tag:\n");
			sb.append(score_tag + "\n");
			sb.append("subjectivity:\n");
			sb.append(subjectivity + "\n");
			sb.append("irony:\n");
			sb.append(irony + "\n");
			sb.append("Sentences:{\n");
			for (Sentence s : sentence_list)
				sb.append(s.toString());
			sb.append("},\nEntities:{\n");
			for (SentimentedEntity e : sentimented_entity_list)
				sb.append(e.toString());
			sb.append("},\nConcepts:{\n");
			for (SentimentedConcept c : sentimented_concept_list)
				sb.append(c.toString());
			sb.append("}\n");
			return sb.toString();
		}

		public Serialized_resp serialize() throws UnsupportedEncodingException {
			Serialized_resp sr = new Serialized_resp();

			sr.s = this.status;
			FeatureMap doc_fm = Factory.newFeatureMap();
			if (model != null)
				doc_fm.put("model", new String(model.getBytes(), "UTF-8"));
			if (score_tag != null)
				doc_fm.put("score_tag", new String(score_tag.getBytes(),
						"UTF-8"));
			if (agreement != null)
				doc_fm.put("agreement", new String(agreement.getBytes(), "UTF-8"));
			if (subjectivity != null)
				doc_fm.put("subjectivity", new String(subjectivity.getBytes(),
						"UTF-8"));
			if (confidence != null)
				doc_fm.put("confidence", new String(confidence.getBytes(), "UTF-8"));
			if (irony != null)
				doc_fm.put("irony", new String(irony.getBytes(), "utf-8"));
			int cnt = 0;
			if (sentimented_entity_list != null) {
				ArrayList<String> entity_form = new ArrayList<String>(), entity_variant = new ArrayList<String>(), entity_type = new ArrayList<String>(), entity_score_tag = new ArrayList<String>();
				for (SentimentedEntity e : sentimented_entity_list) {
					entity_form.add(e.form != null ? new String(e.form
							.getBytes(), "UTF-8") : "");
					entity_variant.add(e.variant != null ? new String(e.variant
							.getBytes(), "UTF-8") : "");
					entity_type.add(e.type!= null ? new String(e.type.getBytes(),
							"UTF-8") : "");
					entity_score_tag.add(e.score_tag != null ? new String(
							e.score_tag.getBytes(), "UTF-8") : "");
				}
				doc_fm.put("entity_form", entity_form);
				doc_fm.put("entity_variant", entity_variant);
				doc_fm.put("entity_type", entity_type);
				doc_fm.put("entity_score_tag", entity_score_tag);
			}
			cnt = 0;
			if (sentimented_concept_list != null) {
				ArrayList<String> concept_form = new ArrayList<String>(), concept_variant = new ArrayList<String>(), concept_type = new ArrayList<String>(), concept_score_tag = new ArrayList<String>();
				for (SentimentedConcept c : sentimented_concept_list) {
					concept_form.add(c.form != null ? new String(c.form
							.getBytes(), "UTF-8") : "");
					concept_variant.add(c.variant != null ? new String(c.variant
							.getBytes(), "UTF-8") : "");
					concept_type.add(c.type != null ? new String(c.type.getBytes(),
							"UTF-8") : "");
					concept_score_tag.add(c.score_tag != null ? new String(
							c.score_tag.getBytes(), "UTF-8") : "");
				}
				doc_fm.put("concept_form", concept_form);
				doc_fm.put("concept_variant", concept_variant);
				doc_fm.put("concept_type", concept_type);
				doc_fm.put("concept_score_tag", concept_score_tag);
			}

			sr.doc_fm = doc_fm;

			if (sentence_list != null) {
				for (Sentence st : sentence_list) {
					for (Segment s : st.segment_list) {
						Serialized_resp.Annot at = sr.new Annot();
						at.inip = s.inip;
						at.endp = s.endp + 1;
						if (s.text != null)
							at.fm.put("text", s.text.isEmpty() ? "" : new String(
									s.text.getBytes(), "UTF-8"));
						if (s.score_tag != null)
							at.fm.put("score_tag", s.score_tag.isEmpty() ? ""
									: new String(s.score_tag.getBytes(), "UTF-8"));
						if (s.segment_type != null)
							at.fm.put("segment_type",
									s.segment_type.isEmpty() ? "" : new String(
											s.segment_type.getBytes(), "UTF-8"));
						if (s.agreement != null)
							at.fm.put("agreement", s.agreement.isEmpty() ? "" : new String(
									s.agreement.getBytes(), "UTF-8"));
						if (s.confidence != null)
							at.fm.put("confidence", s.confidence == null ? "" : s.confidence.toString());
						if (s.polarity_term_list != null) {
							ArrayList<String> polarity_term_text = new ArrayList<String>(), polarity_term_tag_stack = new ArrayList<String>(), polarity_term_score_tag = new ArrayList<String>(), polarity_term_confidence = new ArrayList<String>();
							ArrayList<ArrayList<String>> polarity_term_sentimented_entity_form = new ArrayList<ArrayList<String>>(), polarity_term_sentimented_entity_variant = new ArrayList<ArrayList<String>>(), polarity_term_sentimented_entity_type = new ArrayList<ArrayList<String>>(), polarity_term_sentimented_concept_form = new ArrayList<ArrayList<String>>(), polarity_term_sentimented_concept_variant = new ArrayList<ArrayList<String>>(), polarity_term_sentimented_concept_type = new ArrayList<ArrayList<String>>();
							for (PolarityTerm pt : s.polarity_term_list) {
								polarity_term_text.add(pt.text != null ? new String(pt.text
										.getBytes(), "UTF-8") : "");
								polarity_term_tag_stack.add(pt.tag_stack != null ? new String(pt.tag_stack
										.getBytes(), "UTF-8") : "");
								polarity_term_score_tag
										.add(pt.score_tag != null ? new String(
												pt.score_tag.getBytes(), "UTF-8")
												: "");
								polarity_term_confidence
								.add(pt.confidence != null ? new String(
										pt.confidence.getBytes(), "UTF-8")
										: "");
								if (pt.sentimented_entity_list != null) {
									ArrayList<String> entity_form = new ArrayList<String>(), entity_variant = new ArrayList<String>(), entity_type = new ArrayList<String>();
									for (SentimentedEntity e : pt.sentimented_entity_list) {
										if (e.form != null)
											entity_form
													.add(e.form != null ? new String(
															e.form.getBytes(),
															"UTF-8") : "");
										if (e.variant != null)
											entity_variant
													.add(e.variant != null ? new String(
															e.variant.getBytes(),
															"UTF-8") : "");
										if (e.type != null)
											entity_type
													.add(e.type != null ? new String(
															e.type.getBytes(),
															"UTF-8") : "");
									}
									polarity_term_sentimented_entity_form.add(entity_form);
									polarity_term_sentimented_entity_variant.add(entity_variant);
									polarity_term_sentimented_entity_type.add(entity_type);
								}
								if (pt.sentimented_concept_list != null) {
									ArrayList<String> concept_form = new ArrayList<String>(), concept_variant = new ArrayList<String>(), concept_type = new ArrayList<String>();									
									for (SentimentedConcept c : pt.sentimented_concept_list) {
										if (c.form != null)
											concept_form
													.add(c.form != null ? new String(
															c.form.getBytes(),
															"UTF-8") : "");
										if (c.variant != null)
											concept_variant
													.add(c.variant != null ? new String(
															c.variant.getBytes(),
															"UTF-8") : "");
										if (c.type != null)
											concept_type
													.add(c.type != null ? new String(
															c.type.getBytes(),
															"UTF-8") : "");
									}
									polarity_term_sentimented_concept_form.add(concept_form);
									polarity_term_sentimented_concept_variant.add(concept_variant);
									polarity_term_sentimented_concept_type.add(concept_type);
								}
							}
							at.fm.put("polarity_term_text", polarity_term_text);
							at.fm.put("polarity_term_tag_stack", polarity_term_tag_stack);
							at.fm.put("polarity_term_score_tag", polarity_term_score_tag);
							at.fm.put("polarity_term_sentimented_entity_form", polarity_term_sentimented_entity_form);
							at.fm.put("polarity_term_sentimented_entity_variant", polarity_term_sentimented_entity_variant);
							at.fm.put("polarity_term_sentimented_entity_type", polarity_term_sentimented_entity_type);
							at.fm.put("polarity_term_sentimented_concept_form", polarity_term_sentimented_concept_form);
							at.fm.put("polarity_term_sentimented_concept_variant", polarity_term_sentimented_concept_variant);
							at.fm.put("polarity_term_sentimented_concept_type", polarity_term_sentimented_concept_type);
						}
						sr.annot_list.add(at);
					}
				}
			}
			return sr;
		}

	}

}
