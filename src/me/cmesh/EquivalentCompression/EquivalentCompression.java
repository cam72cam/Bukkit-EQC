package me.cmesh.EquivalentCompression;

import org.bukkit.plugin.java.JavaPlugin;

public class EquivalentCompression extends JavaPlugin {
	public static EquivalentCompression Instance;
	private EQCListener listener;
	
	public EquivalentCompression() {
		Instance = this;
		listener = new EQCListener();
	}
	
	public void onEnable() {
		Recipies.registerAll();
		getServer().getPluginManager().registerEvents(listener, this);
	}
}
