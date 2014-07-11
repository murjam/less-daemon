package pt.lobo.less.daemon;

import pt.lobo.less.daemon.lessc.LessImpl;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;

public class LessModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Less.class).to(LessImpl.class);
		bind(EventBus.class).asEagerSingleton();
	}

}
