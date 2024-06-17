package com.technicjelle.BlueMapNativeAddonTemplate;

import com.technicjelle.BMUtils.BMNative;
import com.technicjelle.UpdateChecker;
import de.bluecolored.bluemap.api.BlueMapAPI;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlueMapNativeAddonTemplate implements Runnable {
	private Logger logger;
	private UpdateChecker updateChecker;
	private @Nullable Config config;

	@Override
	public void run() {
		String addonID;
		String addonVersion;
		try {
			addonID = BMNative.getAddonID(this.getClass().getClassLoader());
			addonVersion = BMNative.getAddonMetadataKey(this.getClass().getClassLoader(), "version");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		logger = Logger.getLogger(addonID);
		logger.log(Level.INFO, "Starting " + addonID + " " + addonVersion);
		updateChecker = new UpdateChecker("TechnicJelle", addonID, addonVersion);
		updateChecker.checkAsync();
		BlueMapAPI.onEnable(onEnableListener);
		BlueMapAPI.onDisable(onDisableListener);
	}

	final private Consumer<BlueMapAPI> onEnableListener = api -> {
		updateChecker.logUpdateMessage(logger);

		try {
			config = Config.load(api);
		} catch (IOException e) {
			config = null;
			throw new RuntimeException(e);
		}

		logger.log(Level.INFO, "Hello, " + config.getWorld() + "!");
	};

	final private Consumer<BlueMapAPI> onDisableListener = api -> {
		if (config == null) return;
		logger.log(Level.INFO, "Goodbye, " + config.getWorld() + "!");
	};
}
