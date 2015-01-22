package es.daedalus.meaningcloud.gate.param;

import gate.Factory;
import gate.FeatureMap;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class SentimentBean {
	public class Entityform {
		String text, id, type;

		@Override
		public String toString() {
			HashMap<String, Object> map = new HashMap();
			if (text != null)
				map.put("text", text);
			if (type != null)
				map.put("type", type);
			if (text != null)
				map.put("id", id);
			return map.toString();
		}
	}

	public class Conceptform {
		String text, id, type;

		@Override
		public String toString() {
			HashMap<String, Object> map = new HashMap();
			if (text != null)
				map.put("text", text);
			if (type != null)
				map.put("type", type);
			if (text != null)
				map.put("id", id);
			return map.toString();
		}
	}

	public class Entity {
		String text, type, id;
		Double score;
		String score_tag;
		Double sd;
		String sd_tag;

		@Override
		public String toString() {
			HashMap<String, Object> map = new HashMap();
			if (text != null)
				map.put("text", text);
			if (type != null)
				map.put("type", type);
			if (id != null)
				map.put("id", id);
			if (score != null)
				map.put("score", score);
			if (score_tag != null)
				map.put("score_tag", score_tag);
			if (sd != null)
				map.put("sd", sd);
			if (sd_tag != null)
				map.put("sd_tag", sd_tag);
			return map.toString();
		}
	}

	public class Concept {
		String text, type, id;
		Double score;
		String score_tag;
		Double sd;
		String sd_tag;

		@Override
		public String toString() {
			HashMap<String, Object> map = new HashMap();
			if (text != null)
				map.put("text", text);
			if (type != null)
				map.put("type", type);
			if (id != null)
				map.put("id", id);
			if (score != null)
				map.put("score", score);
			if (score_tag != null)
				map.put("score_tag", score_tag);
			if (sd != null)
				map.put("sd", sd);
			if (sd_tag != null)
				map.put("sd_tag", sd_tag);
			return map.toString();
		}
	}

	public class Keyword {
		String text;
		Double score;
		String score_tag;
		Entityform[] entity_list = null;
		Conceptform[] concept_list = null;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("KW_text:\n");
			sb.append(text + "\n");
			sb.append("KW_score:\n");
			sb.append(score_tag + "\n");
			sb.append("Entities:{\n");
			for (Entityform e : entity_list)
				sb.append(e.toString());
			sb.append("},\nConcepts:{\n");
			if (this.concept_list != null) {
				for (Conceptform c : concept_list)
					sb.append(c.toString());
			}
			sb.append("}\n");
			return sb.toString();
		}
	}

	public class Segment {
		Long inip;
		Long endp;
		String text;
		Double score;
		String score_tag;
		Double sd;
		String sd_tag;
		String subjectivity;
		String irony;
		Keyword[] keyword_list = null;

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
			sb.append("sd_tag:\n");
			sb.append(sd_tag + "\n");
			sb.append("subjectivity:\n");
			sb.append(subjectivity + "\n");
			sb.append("irony:\n");
			sb.append(irony + "\n");
			sb.append("Keywords:{\n");
			for (Keyword k : keyword_list)
				sb.append(k.toString());
			return sb.toString();
		}
	}

	public class Sent_data {
		public Serialized_resp.Status status;
		public String model;
		public Double score;
		public String score_tag;
		public Double sd;
		public String sd_tag;
		public String subjectivity;
		public String irony;
		public Segment[] segment_list = null;
		public Entity[] entity_list = null;
		public Concept[] concept_list = null;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Status:\n");
			sb.append(status.toString());
			sb.append("model:\n");
			sb.append(model + "\n");
			sb.append("score_tag:\n");
			sb.append(score_tag + "\n");
			sb.append("sd_tag:\n");
			sb.append(sd_tag + "\n");
			sb.append("subjectivity:\n");
			sb.append(subjectivity + "\n");
			sb.append("irony:\n");
			sb.append(irony + "\n");
			sb.append("Keywords:\n");
			for (Segment s : segment_list)
				sb.append(s.toString());
			sb.append("},\nEntities:{\n");
			for (Entity e : entity_list)
				sb.append(e.toString());
			sb.append("},\nConcepts:{\n");
			for (Concept c : concept_list)
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
			if (score != null)
				doc_fm.put("score", score == null ? "" : score);
			if (score_tag != null)
				doc_fm.put("score_tag", new String(score_tag.getBytes(),
						"UTF-8"));
			if (sd != null)
				doc_fm.put("sd", sd == null ? "" : sd.toString());
			if (sd_tag != null)
				doc_fm.put("sd_tag", new String(sd_tag.getBytes(), "UTF-8"));
			if (subjectivity != null)
				doc_fm.put("subjectivity", new String(subjectivity.getBytes(),
						"UTF-8"));
			if (irony != null)
				doc_fm.put("irony", new String(irony.getBytes(), "utf-8"));
			int cnt = 0;
			if (entity_list != null) {
				ArrayList<String> entity_text = new ArrayList<String>(), entity_type = new ArrayList<String>(), entity_id = new ArrayList<String>(), entity_score_tag = new ArrayList<String>(), entity_sd_tag = new ArrayList<String>();
				ArrayList</* Double */String> entity_score = new ArrayList<String>(), entity_sd = new ArrayList<String>();
				for (Entity e : entity_list) {
					entity_text.add(e.text != null ? new String(e.text
							.getBytes(), "UTF-8") : "");
					entity_type.add(e.type != null ? new String(e.type
							.getBytes(), "UTF-8") : "");
					entity_id.add(e.id != null ? new String(e.id.getBytes(),
							"UTF-8") : "");
					entity_score.add(e.score != null ? e.score.toString() : "");
					entity_score_tag.add(e.score_tag != null ? new String(
							e.score_tag.getBytes(), "UTF-8") : "");
					entity_sd.add(e.sd != null ? e.sd.toString() : "");
					entity_sd_tag.add(e.sd_tag != null ? new String(e.sd_tag
							.getBytes(), "UTF-8") : "");
				}
				doc_fm.put("entity_text", entity_text);
				doc_fm.put("entity_type", entity_type);
				doc_fm.put("entity_id", entity_id);
				doc_fm.put("entity_score", entity_score);
				doc_fm.put("entity_score_tag", entity_score_tag);
				doc_fm.put("entity_sd", entity_sd);
				doc_fm.put("entity_sd_tag", entity_sd_tag);
			}
			cnt = 0;
			if (concept_list != null) {
				ArrayList<String> concept_text = new ArrayList<String>(), concept_type = new ArrayList<String>(), concept_id = new ArrayList<String>(), concept_score = new ArrayList<String>(), concept_score_tag = new ArrayList<String>(), concept_sd = new ArrayList<String>(), concept_sd_tag = new ArrayList<String>();
				for (Concept c : concept_list) {
					concept_text.add(c.text != null ? new String(c.text
							.getBytes(), "UTF-8") : "");
					concept_type.add(c.type != null ? new String(c.type
							.getBytes(), "UTF-8") : "");
					concept_id.add(c.id != null ? new String(c.id.getBytes(),
							"UTF-8") : "");
					concept_score.add(c.score != null ? Double
							.toString(c.score) : "");
					concept_score_tag.add(c.score_tag != null ? new String(
							c.score_tag.getBytes(), "UTF-8") : "");
					concept_sd.add(c.sd != null ? Double.toString(c.sd) : "");
					concept_sd_tag.add(c.sd_tag != null ? new String(c.sd_tag
							.getBytes(), "UTF-8") : "");
				}
				doc_fm.put("concept_text", concept_text);
				doc_fm.put("concept_type", concept_type);
				doc_fm.put("concept_id", concept_id);
				doc_fm.put("concept_score", concept_score);
				doc_fm.put("concept_score_tag", concept_score_tag);
				doc_fm.put("concept_sd", concept_sd);
				doc_fm.put("concept_sd_tag", concept_sd_tag);
			}

			sr.doc_fm = doc_fm;

			if (segment_list != null) {
				for (Segment s : segment_list) {
					Serialized_resp.Annot at = sr.new Annot();
					at.inip = s.inip;
					at.endp = s.endp + 1;
					if (s.text != null)
						at.fm.put("text", s.text.isEmpty() ? "" : new String(
								s.text.getBytes(), "UTF-8"));
					at.fm.put("score", (s.score != null ? s.score : ""));
					if (s.score_tag != null)
						at.fm.put("score_tag", s.score_tag.isEmpty() ? ""
								: new String(s.score_tag.getBytes(), "UTF-8"));
					at.fm.put("sd", (s.sd != null ? s.sd : ""));
					if (s.sd_tag != null)
						at.fm.put("sd_tag", s.sd_tag.isEmpty() ? ""
								: new String(s.sd_tag.getBytes(), "UTF-8"));
					if (s.subjectivity != null)
						at.fm.put("subjectivity",
								s.subjectivity.isEmpty() ? "" : new String(
										s.subjectivity.getBytes(), "UTF-8"));
					if (s.irony != null)
						at.fm.put("irony", s.irony.isEmpty() ? "" : new String(
								s.irony.getBytes(), "UTF-8"));
					if (s.keyword_list != null) {
						ArrayList<String> keyword_text = new ArrayList<String>(), keyword_score = new ArrayList<String>(), keyword_score_tag = new ArrayList<String>();
						ArrayList<ArrayList<String>> keyword_entity_text = new ArrayList<ArrayList<String>>(), keyword_entity_type = new ArrayList<ArrayList<String>>(), keyword_entity_id = new ArrayList<ArrayList<String>>(), keyword_concept_text = new ArrayList<ArrayList<String>>(), keyword_concept_type = new ArrayList<ArrayList<String>>(), keyword_concept_id = new ArrayList<ArrayList<String>>();
						for (Keyword k : s.keyword_list) {
							keyword_text.add(k.text != null ? new String(k.text
									.getBytes(), "UTF-8") : "");
							keyword_score.add(k.score_tag != null ? Double
									.toString(k.score) : "");
							keyword_score_tag
									.add(k.score_tag != null ? new String(
											k.score_tag.getBytes(), "UTF-8")
											: "");

							if (k.entity_list != null) {
								ArrayList<String> entity_text = new ArrayList<String>(), entity_type = new ArrayList<String>(), entity_id = new ArrayList<String>();
								for (Entityform e : k.entity_list) {
									if (e.text != null)
										entity_text
												.add(e.text != null ? new String(
														e.text.getBytes(),
														"UTF-8") : "");
									if (e.type != null)
										entity_type
												.add(e.type != null ? new String(
														e.type.getBytes(),
														"UTF-8") : "");
									if (e.id != null)
										entity_id
												.add(e.id != null ? new String(
														e.id.getBytes(),
														"UTF-8") : "");
								}
								keyword_entity_text.add(entity_text);
								keyword_entity_type.add(entity_type);
								keyword_entity_id.add(entity_id);
							}
							if (k.concept_list != null) {
								ArrayList<String> concept_text = new ArrayList<String>(), concept_type = new ArrayList<String>(), concept_id = new ArrayList<String>();
								for (Conceptform e : k.concept_list) {
									if (e.text != null)
										concept_text
												.add(e.text != null ? new String(
														e.text.getBytes(),
														"UTF-8") : "");
									if (e.type != null)
										concept_type
												.add(e.type != null ? new String(
														e.type.getBytes(),
														"UTF-8") : "");
									if (e.id != null)
										concept_id
												.add(e.id != null ? new String(
														e.id.getBytes(),
														"UTF-8") : "");
								}
								keyword_concept_text.add(concept_text);
								keyword_concept_type.add(concept_type);
								keyword_concept_id.add(concept_id);
							}
						}
						at.fm.put("keyword_text", keyword_text);
						at.fm.put("keyword_score", keyword_score);
						at.fm.put("keyword_score_tag", keyword_score_tag);
						at.fm.put("keyword_entity_text", keyword_entity_text);
						at.fm.put("keyword_entity_type", keyword_entity_type);
						at.fm.put("keyword_entity_id", keyword_entity_id);
						at.fm.put("keyword_concept_text", keyword_concept_text);
						at.fm.put("keyword_concept_type", keyword_concept_type);
						at.fm.put("keyword_concept_id", keyword_concept_id);
					}
					sr.annot_list.add(at);
				}
			}
			return sr;
		}

	}

}