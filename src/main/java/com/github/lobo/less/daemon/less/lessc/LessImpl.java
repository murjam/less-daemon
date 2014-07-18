package com.github.lobo.less.daemon.less.lessc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lobo.less.daemon.event.NeedsCompileEvent;
import com.github.lobo.less.daemon.less.Less;
import com.github.lobo.less.daemon.model.LessFile;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LessImpl implements Less {
	
	private static final Logger logger = LoggerFactory.getLogger(LessImpl.class);
	
	private static final String LESSC_PATH = "/usr/local/bin/lessc";
	
	private static final String LESSC_OPTIONS[] = {
		"--compress"
	};
	
	@Inject
	public LessImpl(EventBus eventBus) {
		eventBus.register(this);
	}
	
	@Subscribe
	public void onNeedsCompile(NeedsCompileEvent event) {
		for(LessFile file : event.getCompileSet()) {
			try {
				compile(file.getFilename());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String compile(String filename) throws IOException {
		List<String> command = Lists.newArrayList();
		command.add(LESSC_PATH);
		for(String option : LESSC_OPTIONS)
			command.add(option);
		command.add(filename);
		ProcessBuilder builder = new ProcessBuilder(command);
		final Process process = builder.start();
		
		final StringBuilder output = new StringBuilder();
		InputStream stream = process.getInputStream();
		String css = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
		output.append(css);

		new Thread(new Runnable() {
			public void run() {
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}).start();
		
		/*
		new Thread(new Runnable() {
			public void run() {
				StringBuilder error = new StringBuilder();
				try {
					InputStream stream = process.getErrorStream();
					int b;
					while((b = stream.read()) != 0)
						error.append((char)b);
					throw new RuntimeException(error.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		*/
		
		try {
			int result = process.waitFor();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		
		return output.toString();
	}
	
}
