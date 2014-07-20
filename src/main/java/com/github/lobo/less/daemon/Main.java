package com.github.lobo.less.daemon;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.internal.Lists;
import com.github.lobo.less.daemon.event.ExitEvent;
import com.github.lobo.less.daemon.event.TrayReadyEvent;
import com.github.lobo.less.daemon.less.Less;
import com.github.lobo.less.daemon.model.LessFolder;
import com.github.lobo.less.daemon.preferences.PreferenceManager;
import com.github.lobo.less.daemon.tray.Tray;
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
	}

	public Main() {
		Guice.createInjector(new LessModule()).injectMembers(this);
		eventBus.register(this);
	}
	
	public void start(String... args) {
		mainCommand = new MainCommand();
		JCommander commander = new JCommander(mainCommand);
		
		loadCommand = new LoadCommand();
		commander.addCommand("load", loadCommand);
		try {
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
//		new Main().start("load", "/Users/dev/git/egoi/megan-web-ui/resources/assets/less/");
//		new Main().start("load", "/tmp/less");
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
		for(String filename : loadCommand.folders)
			folderManager.addFolder(new LessFolder(filename));
		
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
