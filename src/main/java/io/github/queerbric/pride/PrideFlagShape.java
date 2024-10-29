package io.github.queerbric.pride;

import com.mojang.blaze3d.vertex.MatrixStack;
import it.unimi.dsi.fastutil.ints.IntList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface PrideFlagShape {
	@OnlyIn(Dist.CLIENT)
	void render(IntList colors, MatrixStack matrices, float x, float y, float width, float height);
}
