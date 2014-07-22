package com.github.lobo.less.daemon;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.github.lobo.less.daemon.event.ExitEvent;
import com.github.lobo.less.daemon.event.TrayReadyEvent;
import com.github.lobo.less.daemon.less.Less;
import com.github.lobo.less.daemon.model.LessFolder;
import com.github.lobo.less.daemon.preferences.PreferenceManager;
import com.github.lobo.less.daemon.tray.Tray;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Inject;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	@Inject
	Tray tray;
	@Inject
	EventBus eventBus;
	@Inject
	PreferenceManager preferenceManager;
	@Inject
	FolderManager folderManager;
	@Inject
	FolderWatcherService watchService;
	@Inject
	Less compiler;

	private LoadCommand loadCommand;

	private MainCommand mainCommand;

	@Parameters(commandDescription = "Main Options")
	private class MainCommand {
		@Parameter(names = {
			"-h", "--help"
		}, help = true, description = "this help")
		private boolean help;

		@Parameter(names = {
			"-d", "--debug"
		}, help = true, description = "set log level to debug")
		private boolean debug;
		
	}
	
	@Parameters(commandDescription="Load folders from command line")
	private class LoadCommand {
		@Parameter(description="Folders to load (ex.: folder1/ folder2/ folder3/")
		public List<String> folders = Lists.newArrayList();
		
		@Parameter(names = {
			"-n", "--no-save-folders"
		}, help = true, description = "don't save folders added by command line (useful to start from other app)")
		private boolean noSaveFolders;
	}

	public Main() {
		Guice.createInjector(new LessModule()).injectMembers(this);
		eventBus.register(this);
	}
	
	public void start(String... args) {
		preferenceManager.getLesscPath();
		
		mainCommand = new MainCommand();
		JCommander commander = new JCommander(mainCommand);
		
		loadCommand = new LoadCommand();
		commander.addCommand("load", loadCommand);
		try {
			if(mainCommand.help) {
				commander.usage();
				return;
			}
			commander.parse(args);
			EventQueue.invokeLater(tray);
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			commander.usage();
		} catch (Exception e) {
			if (mainCommand.debug)
				e.printStackTrace();
			else
				System.err.println(e.getMessage());
		}

	}

	public static void main(String[] args) throws Exception {
		new Main().start(args);
	}
	
	private static String[] morePaths() {
		String os = System.getProperty("os.name").toLowerCase();
		
		if(os.startsWith("mac") || os.startsWith("linux"))
			return new String[] {
				"/usr/local/bin", "/opt/local/bin", System.getProperty("user.home") + File.separatorChar + "bin"
			};
		
		return new String[0];
	}

	public static String findInPath(final String executable) {
		return findInPath(executable, morePaths());
	}
	
	public static String findInPath(final String executable, String... morePath) {
		Set<String> paths = paths(morePath);
		for (String dir : paths) {
			Path dirPath = Paths.get(dir);
			if (!Files.exists(dirPath))
				continue;
			try (
				DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, new DirectoryStream.Filter<Path>() {
					@Override
					public boolean accept(Path entry) throws IOException {
						return entry.getFileName().startsWith(executable);
					}
				})) {
				Iterator<Path> streamIterator = stream.iterator();
				if (streamIterator.hasNext())
					return streamIterator.next().toString();
			} catch (IOException e) {
				logger.warn("Error reading directory '{}': {}", dirPath, e.getMessage());
			}
		}
		return null;
	}

	public static Set<String> paths() {
		return paths(morePaths());
	}
	
	public static Set<String> paths(String... morePath) {
		Iterable<String> split = Splitter.on(':').split(System.getenv("PATH"));
		Set<String> paths = Sets.newHashSet(split);
		for (String more : morePath)
			paths.add(more);
		return paths;
	}

	@Subscribe
	public void onTrayReady(TrayReadyEvent event) {
		try {
			Set<LessFolder> folders = preferenceManager.readFolderSet();
			// Add from preferences
			for (LessFolder folder : folders)
				folderManager.addFolder(folder, false);
		} catch (IOException e) {
			logger.warn("Error reading preferences: {}", e.getMessage());
		}

		// Add from command line
		for (String filename : loadCommand.folders)
			folderManager.addFolder(new LessFolder(filename), !loadCommand.noSaveFolders);

		try {
			watchService.start();
		} catch (Exception e) {
			logger.error("Error starting file alteration monitor: " + e.getMessage(), e);
			exit(1);
		}
	}

	@Subscribe
	public void onExitEvent(ExitEvent event) {
		exit(event.getStatus());
	}

	private void exit(int status) {
		System.exit(status);
	}

}
