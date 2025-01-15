package org.rocket3;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

/**
 * Auto-crafts duration 3 rockets and paper
 *
 * @author PK268
 */
public class Rocket3Plugin extends Plugin {
	
	@Override
	public void onLoad() {
		
		//creating and registering a new module
		final Rocket3Module rocket3 = new Rocket3Module();
		RusherHackAPI.getModuleManager().registerFeature(rocket3);
		
		//logger
		this.getLogger().info("Loaded Rocket3");
		
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("Un-loaded Rocket3");
	}
	
}