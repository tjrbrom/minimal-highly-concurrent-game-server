package com.gameserver.mechanics;

import com.gameserver.config.AppConfig;
import com.gameserver.config.AppConfigLoader;
import com.google.inject.Binder;
import com.google.inject.Module;

public final class GameModule implements Module {

    @Override
    public void configure(Binder binder) {
        // Bind the application configuration to Guice
        binder.bind(AppConfig.class).toInstance(AppConfigLoader.load());
    }

}
