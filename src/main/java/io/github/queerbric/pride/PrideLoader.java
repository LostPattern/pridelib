package io.github.queerbric.pride;

import com.google.gson.Gson;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.io.Resource;
import net.minecraft.resources.io.ResourceManager;
import net.minecraft.resources.io.ResourceReloader;
import net.minecraft.resources.io.SynchronousResourceReloader;
import net.minecraft.util.profiling.Profiler;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

public class PrideLoader implements ResourceReloader {
	private static final Identifier ID = Identifier.of("pride", "flags");
	private static final Logger LOGGER = LoggerFactory.getLogger("pride");
	private static final Gson GSON = new Gson();
	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9a-fA-F]{6}$");

	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager resourceManager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
		return CompletableFuture.supplyAsync(() -> loadFlags(resourceManager), prepareExecutor)
				.thenCompose(synchronizer::whenPrepared)
				.thenAcceptAsync(PrideLoader::applyFlags, applyExecutor);
	}

	static class Config {
		String[] flags;
	}



	public static List<PrideFlag> loadFlags(ResourceManager manager) {
		var flags = new ArrayList<PrideFlag>();

		outer:
		for (var entry : manager.findResources("flags", path -> path.path().endsWith(".json")).entrySet()) {
			Identifier id = entry.getKey();
			String[] parts = id.path().split("/");
			String name = parts[parts.length - 1];
			name = name.substring(0, name.length() - 5);

			try (var reader = new InputStreamReader(entry.getValue().open())) {
				PrideFlag.Properties builder = GSON.fromJson(reader, PrideFlag.Properties.class);

				for (String color : builder.colors) {
					if (!HEX_COLOR_PATTERN.matcher(color).matches()) {
						LOGGER.warn("[pride] Malformed flag data for flag " + name + ", " + color
								+ " is not a valid color, must be a six-digit hex color like #FF00FF");
						continue outer;
					}
				}

				var flag = new PrideFlag(name, builder);
				flags.add(flag);
			} catch (Exception e) {
				LOGGER.warn("[pride] Malformed flag data for flag " + name, e);
			}
		}

		var prideFile = new File(FMLPaths.CONFIGDIR.get().toFile(), "pride.json");
		if (prideFile.exists()) {
			try (var reader = new FileReader(prideFile)) {
				Config config = GSON.fromJson(reader, Config.class);

				if (config.flags != null) {
					List<String> list = Arrays.asList(config.flags);
					flags.removeIf(flag -> !list.contains(flag.getId()));
				}
			} catch (Exception e) {
				LOGGER.warn("[pride] Malformed flag data for pride.json config");
			}
		} else {
			var id = Identifier.of("pride", "flags.json");

			Optional<Resource> resource = manager.getResource(id);
			if (resource.isPresent()) {
				try (var reader = new InputStreamReader(resource.get().open())) {
					Config config = GSON.fromJson(reader, Config.class);

					if (config.flags != null) {
						List<String> list = Arrays.asList(config.flags);
						flags.removeIf(flag -> !list.contains(flag.getId()));
					}
				} catch (Exception e) {
					LOGGER.warn("[pride] Malformed flag data for flags.json", e);
				}
			}
		}

		return flags;
	}

	private static void applyFlags(List<PrideFlag> flags) {
		PrideFlags.setFlags(flags);
	}
}
