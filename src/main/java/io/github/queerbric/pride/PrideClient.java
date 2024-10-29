package io.github.queerbric.pride;

import net.minecraft.resources.io.ResourceType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@OnlyIn(Dist.CLIENT)
@Mod(value = "pride", dist = Dist.CLIENT)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class PrideClient {
	@SubscribeEvent
	public static void registerResources(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(new PrideLoader());
	}
}