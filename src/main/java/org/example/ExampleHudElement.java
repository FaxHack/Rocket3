package org.example;

import org.rusherhack.client.api.feature.hud.ResizeableHudElement;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.render.graphic.TextureGraphic;

/**
 * Example rusherhack hud element
 * <p>
 * There are other hud element types than ResizeableHudElement, look at the other classes in the package
 *
 * @author John200410
 */
public class ExampleHudElement extends ResizeableHudElement {
	
	private TextureGraphic graphic = null;
	
	public ExampleHudElement() {
		super("ExampleHudElement");
		
		//try loading graphic
		try {
			this.graphic = new TextureGraphic("exampleplugin/graphics/rh_head.png", 235, 234);
		} catch (Throwable t) {
			this.getLogger().error("Failed to load graphic", t);
		}
	}
	
	
	@Override
	public double getWidth() {
		return 200;
	}
	
	@Override
	public double getHeight() {
		return 200;
	}
}
