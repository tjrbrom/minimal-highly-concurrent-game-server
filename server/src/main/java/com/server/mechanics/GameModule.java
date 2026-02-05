package com.server.mechanics;

import com.server.config.AppConfig;
import com.server.config.AppConfigLoader;
import com.google.inject.Binder;
import com.google.inject.Module;

public final class GameModule implements Module {

    @Override
    public void configure(Binder binder) {
        // Bind the application configuration to Guice
        binder.bind(AppConfig.class).toInstance(AppConfigLoader.load());
    }

}
