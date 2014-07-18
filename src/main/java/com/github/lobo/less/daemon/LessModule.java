package com.github.lobo.less.daemon;

import org.apache.commons.io.monitor.FileAlterationMonitor;

import com.github.lobo.less.daemon.less.Less;
import com.github.lobo.less.daemon.less.lessc.LessImpl;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class LessModule extends AbstractModule {

	private static final long DEFAULT_INTERVAL = 1000;

	@Override
	protected void configure() {
		bind(Less.class).to(LessImpl.class);
		bind(EventBus.class).asEagerSingleton();
	}
	
	@Provides
	@Singleton
	FileAlterationMonitor getFileAlterationMonitor() {
		return new FileAlterationMonitor(DEFAULT_INTERVAL);
	}

}
