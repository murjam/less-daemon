package com.github.lobo.less.daemon;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
	private static final Logger logger = LoggerFactory.getLogger(Test.class);
	public static void main(String[] args) throws BackingStoreException {
		Preferences prefs = Preferences.userNodeForPackage(Test.class);
		String key = "test1";
		System.out.println(prefs.get(key, null));
		prefs.flush();
	}
}
