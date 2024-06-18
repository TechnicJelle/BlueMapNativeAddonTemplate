package com.technicjelle.BlueMapNativeAddonTemplate;

import com.technicjelle.BMUtils.BMNative.BMNLogger;
import com.technicjelle.BMUtils.BMNative.BMNMetadata;
import com.technicjelle.UpdateChecker;
import de.bluecolored.bluemap.api.BlueMapAPI;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.Consumer;

public class BlueMapNativeAddonTemplate implements Runnable {
	private BMNLogger logger;
	private UpdateChecker updateChecker;
	private @Nullable Config config;

	@Override
	public void run() {
		String addonID;
		String addonVersion;
		try {
			addonID = BMNMetadata.getAddonID(this.getClass().getClassLoader());
			addonVersion = BMNMetadata.getKey(this.getClass().getClassLoader(), "version");
			logger = new BMNLogger(this.getClass().getClassLoader());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		logger.logInfo("Starting " + addonID + " " + addonVersion);
		updateChecker = new UpdateChecker("TechnicJelle", addonID, addonVersion);
		updateChecker.checkAsync();
		BlueMapAPI.onEnable(onEnableListener);
		BlueMapAPI.onDisable(onDisableListener);
	}

	final private Consumer<BlueMapAPI> onEnableListener = api -> {
		updateChecker.getUpdateMessage().ifPresent(logger::logWarning);

		try {
			config = Config.load(api);
		} catch (IOException e) {
			config = null;
			throw new RuntimeException(e);
		}

		logger.logInfo("Hello, " + config.getWorld() + "!");
	};

	final private Consumer<BlueMapAPI> onDisableListener = api -> {
		if (config == null) return;
		logger.logInfo("Goodbye, " + config.getWorld() + "!");
	};
}
