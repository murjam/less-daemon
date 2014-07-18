package com.github.lobo.less.daemon.preferences;

import static java.text.MessageFormat.format;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lobo.less.daemon.model.LessFolder;

public abstract class PreferenceUtil {

	private static final Logger logger = LoggerFactory.getLogger(PreferenceUtil.class);
	
	private static final String FOLDER_KEY_TEMPLATE = "LessFolder({0})";
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String toJson(LessFolder folder) {
		try {
			return objectMapper.writeValueAsString(folder);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public static LessFolder toFolder(String json) throws IOException {
		return objectMapper.readValue(json, LessFolder.class);
	}
	
	public static String toKey(LessFolder folder) {
		return format(FOLDER_KEY_TEMPLATE, folder.getFilename());
	}
	

	
}
