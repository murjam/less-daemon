package com.github.lobo.less.daemon.less;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lobo.less.daemon.Constants;
import com.github.lobo.less.daemon.event.NeedsCompileEvent;
import com.github.lobo.less.daemon.model.LessFile;
import com.github.lobo.less.daemon.preferences.PreferenceManager;
import com.github.lobo.less.daemon.ui.Dialogs;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Less {

	private static final Logger logger = LoggerFactory.getLogger(Less.class);

	public static final String DEFAULT_LESSC_PATH = "/usr/bin/lessc";

	public static final String DEFAULT_LESSC_OPTIONS = "--compress";

	public static final String DEFAULT_OUTPUT_OPTION = OutputOption.PARENT_CSS.name();

	public static enum OutputOption {
		SAME, PARENT_CSS, CUSTOM
	}

	private PreferenceManager preferenceManager;

	public interface CompileCallback {
		void done(String css);

		void error(String error, Throwable e);
	}

	@Inject
	public Less(EventBus eventBus, PreferenceManager preferenceManager) {
		this.preferenceManager = preferenceManager;
		eventBus.register(this);
	}

	@Subscribe
	public void onNeedsCompile(NeedsCompileEvent event) {
		for (LessFile file : event.getCompileSet()) {
			final String filename = file.getFilename();
			compile(filename, new CompileCallback() {

				@Override
				public void error(String error, Throwable e) {
					Dialogs.showError(error, Constants.APP_NAME);
				}

				@Override
				public void done(String css) {
					// Ensure dir exists
					Path outputPath = getOutputPath(filename);
					if(logger.isTraceEnabled())
						logger.trace("Compile output for '{}':\n{}", filename, css);
					try {
						Files.createDirectories(outputPath.getParent());
						Files.write(outputPath, css.getBytes());
						if(logger.isDebugEnabled())
							logger.debug("Saved compile result to '{}'", outputPath);
					} catch (IOException e) {
						String message = format("Error compiling {0}:\n{1}", filename, e.getMessage());
						Dialogs.showError(message, Constants.APP_NAME);
					}
				}
			});
		}
	}

	public void compile(final String filename, final CompileCallback callback) {
		Preconditions.checkNotNull(filename);
		Preconditions.checkNotNull(callback);

		List<String> command = Lists.newArrayList();
		command.add(preferenceManager.getLesscPath());
		String lesscOptions = preferenceManager.getLesscOptions();
		for (String option : Splitter.on(' ').split(lesscOptions))
			command.add(option);
		command.add(filename);

		if(logger.isDebugEnabled())
			logger.debug("Running compile command: {}", Joiner.on(' ').join(command));
		
		ProcessBuilder builder = new ProcessBuilder(command);
		try {
			final Process process = builder.start();
			new Thread(new Runnable() {
				public void run() {
					try {
						InputStream stream = process.getInputStream();
						String css = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
						if(!Strings.isNullOrEmpty(css))
							callback.done(css);
					} catch (Exception e) {
						String message = format("Error compiling {0}:\n{1}", filename, e.getMessage());
						callback.error(message, e);
					}

				}
			}).start();

			new Thread(new Runnable() {
				public void run() {
					try {
						InputStream stream = process.getErrorStream();
						String error = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
						if(!Strings.isNullOrEmpty(error)) {
							String message = format("Error compiling {0}:\n{1}", filename, error);
							callback.error(message, null);
						}
					} catch (Exception e) {
						String message = format("Error compiling {0}:\n{1}", filename, e.getMessage());
						callback.error(message, e);
					}

				}
			}).start();

			try {
				process.waitFor();
			} catch (InterruptedException e) {
				callback.error(e.getMessage(), e);
			}
			
		} catch (IOException e) {
			callback.error(e.getMessage(), e);
		}
	}

	private Path getOutputPath(String filename) {
		OutputOption outputOption = OutputOption.valueOf(preferenceManager.getOutputOption());
		Path outputDirPath;
		switch (outputOption) {
			default:
			case PARENT_CSS:
				outputDirPath = Paths.get("../css");
			break;
			case SAME:
				outputDirPath = Paths.get("./");
			break;
		}

		
		Path originalFilename = Paths.get(filename);
		outputDirPath = originalFilename.getParent().resolve(outputDirPath);
		String basename = originalFilename.toFile().getName();

		String outputFilename;

		int index = basename.lastIndexOf('.');
		if (index != -1) {
			// replace extension with css
			outputFilename = basename.substring(0, index) + ".css";
		} else {
			outputFilename = basename + ".css";
		}
		
		return Paths.get(outputDirPath.toString(), outputFilename);
	}

}
