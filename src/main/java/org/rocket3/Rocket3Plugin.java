package org.example;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

/**
 * Example rusherhack plugin
 *
 * @author John200410
 */
public class Rocket3Plugin extends Plugin {
	
	@Override
	public void onLoad() {
		
		//logger
		this.getLogger().info("Loaded Rocket3");
		
		//creating and registering a new module
		final Rocket3Module rocket3 = new Rocket3Module();
		RusherHackAPI.getModuleManager().registerFeature(rocket3);
		
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("Un-Loaded Rocket3");
	}
	
}