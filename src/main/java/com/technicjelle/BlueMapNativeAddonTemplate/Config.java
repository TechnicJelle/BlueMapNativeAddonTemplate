package com.technicjelle.BlueMapNativeAddonTemplate;

import com.technicjelle.BMUtils.BMNative.BMNConfigDirectory;
import de.bluecolored.bluemap.api.BlueMapAPI;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.io.IOException;
import java.nio.file.Path;

@ConfigSerializable
public class Config {
	private static final String fileName = "settings.conf";

	@Comment("The world to greet")
	private @Nullable String world;

	public static Config load(BlueMapAPI api) throws IOException {
		BMNConfigDirectory.BMNCopy.fromJarResource(api, Config.class.getClassLoader(), fileName, fileName, false);
		Path configDirectory = BMNConfigDirectory.getAllocatedDirectory(api, Config.class.getClassLoader());
		Path configFile = configDirectory.resolve(fileName);

		HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
				.defaultOptions(options -> options.implicitInitialization(false))
				.path(configFile).build();

		Config config = loader.load().get(Config.class);
		if (config == null) {
			throw new IOException("Failed to load config");
		}
		return config;
	}

	public @Nullable String getWorld() {
		return world;
	}
}
