package com.github.lobo.less.daemon.preferences;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class PreferenceUtil {

	private static final Logger logger = LoggerFactory.getLogger(PreferenceUtil.class);
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String toJson(Set<String> folderSet) {
		try {
			return objectMapper.writeValueAsString(folderSet);
		} catch (JsonProcessingException e) {
			logger.error("Error converting folder set to json: " + e.getMessage(), e);
			return "[]";
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> fromJson(String json) {
		try {
			return objectMapper.readValue(json, List.class);
		} catch (IOException e) {
			logger.error("Error converting folder set from json: " + e.getMessage(), e);
			return Lists.newArrayList();
		}
	}
	

	
}
