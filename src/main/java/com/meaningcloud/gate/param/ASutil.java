/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.meaningcloud.gate.param;

import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;

import java.util.HashSet;

/**
 *
 * @author ADRIAN
 */
public class ASutil {

	public static AnnotationSet getFilteredAS(AnnotationSet inputAS,
			String inputAnnExpr) {
		// We allow inputAnnExpr of the form
		// Annotation.feature == value or just Annotation.feature

		String annFeature;
		String annFeatureValue;
		String[] inputAnnArr = inputAnnExpr.split("(\\.)|(\\s*==\\s*)");

		// if(this.getdebug())Out.println("Array size: "+inputAnnArr.length);

		// Assume a simple ann name unless we have a feature and feature value
		// present
		String annName = inputAnnArr[0];
		// String annName = inputAnnExpr;
		AnnotationSet filteredAS = inputAS.get(annName);

		if (inputAnnArr.length == 3 || inputAnnArr.length == 2) {
			annFeature = inputAnnArr[1];
			if (inputAnnArr.length == 2) {
				HashSet<String> feats = new HashSet<String>();
				feats.add(annFeature);
				filteredAS = inputAS.get(annName, feats);
			} else {
				FeatureMap annFeats = Factory.newFeatureMap();
				annFeatureValue = inputAnnArr[2];
				annFeats.put(annFeature, annFeatureValue);
				filteredAS = inputAS.get(annName, annFeats);
			}
		}

		return filteredAS;
	}

}
