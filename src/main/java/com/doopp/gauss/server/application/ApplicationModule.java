package com.doopp.gauss.server.application;

import com.doopp.gauss.rpc.service.HelloService;
import com.doopp.gauss.rpc.service.impl.HelloServiceImpl;
import com.google.inject.*;
import freemarker.template.*;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;

public class ApplicationModule extends AbstractModule {

	@Override
	public void configure() {
		bindFreeMarkerTemplate();
	    bind(HelloService.class).to(HelloServiceImpl.class);
	}

	@Singleton
	@Provides
	public ApplicationProperties applicationProperties() {
		return new ApplicationProperties();
	}

	@Provides
	public EventLoopGroup providesEventLoopGroup() {
		return new NioEventLoopGroup();
	}

	private void bindFreeMarkerTemplate() {

		Version version = new Version("2.3.23");
		DefaultObjectWrapperBuilder defaultObjectWrapperBuilder = new DefaultObjectWrapperBuilder(version);

		Configuration cfg = new Configuration(version);
		cfg.setObjectWrapper(defaultObjectWrapperBuilder.build());
		cfg.setDefaultEncoding("UTF-8");
		// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
		cfg.setLogTemplateExceptions(false);
		// Sets how errors will appear. Here we assume we are developing HTML pages.
		// For production systems TemplateExceptionHandler.RETHROW_HANDLER is better.
		// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		// Bind instance for DI
		bind(Configuration.class).toInstance(cfg);
	}
}
