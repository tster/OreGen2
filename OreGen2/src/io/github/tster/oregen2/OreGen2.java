package io.github.tster.oregen2;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class OreGen2 extends JavaPlugin implements Listener {
	
		Map<String, Object> configMap = new HashMap<String, Object>();
		Material blockMaterial;
		Integer maxDecimalPlaces = 0;
		Random rand = new Random();
		Integer drawNumber;
		double totalDraws = 0;
		Integer currentDraw = 0;
		
		public void onEnable(){
			getServer().getPluginManager().registerEvents(this, this);
			getLogger().info("OreGen2 by Tster has been enabled!");
			// Notify the host that the plugin has been enabled
			if (new File(this.getDataFolder(), "config.yml").exists() == false) {
				getLogger().info("OreGen2 Config not found, writing default to file.");
				this.saveDefaultConfig();
		    }
			// If config does not exist, write the default values to the file
			configMap = getConfig().getValues(false);
			// This puts the information from the config file into a map - for use later
		}
	 
		
		public void onDisable(){
			getLogger().info("OreGen2 by Tster has been disabled!");
			// Notify the host that the plugin has been disabled
		}
		
		
		@EventHandler
	    public void cobbleGeneration(BlockFromToEvent event) {
			getLogger().info("cobble generation" + event.getToBlock().getType() + event.getToBlock().getRelative(event.getFace()).getType());
			if (event.getToBlock().getType() == Material.COBBLESTONE && event.getToBlock().getRelative(event.getFace()).getType() == Material.STATIONARY_WATER) {
	        	// If the 'to' block is cobble now and the block behind that is water
	        	getLogger().info("called");	
	        	event.getToBlock().setType(chooseBlock());
	        	event.setCancelled(true);
	        	// Set the block to a random block - chosen by the chooseBlock function
	        }    
		}
		
		
		public Material chooseBlock() {
			totalDraws = 0;
			currentDraw = 0;
			blockMaterial = null;
			for (Object value : configMap.values()) {
			    if (value.toString().length() - value.toString().indexOf('.') - 1 > maxDecimalPlaces) {
			    	// If the max number of decimal places is bigger than maxDecimalPlaces (starts at 0)
			    	maxDecimalPlaces = value.toString().length() - value.toString().indexOf('.') - 1;
			    	// Set the max number of decimal places
			    	getLogger().info("1  "+ maxDecimalPlaces);
			    }
			}
			// Long method to find the highest number of decimal places in the config file
			// This value will be used to convert decimal chances into draws
			for (Object value : configMap.values()) {
				getLogger().info("test" + value.toString());
				totalDraws = totalDraws + Double.parseDouble(value.toString()) * Math.pow(10,maxDecimalPlaces);
			}
			// Finds the total number of draws - in doing so converting decimal chances to draws
			getLogger().info("" + totalDraws);
			drawNumber = rand.nextInt((int) totalDraws) + 1;
			getLogger().info("draws" + drawNumber);
			// Generates a random number between 1 and the total number of draws
			for (Map.Entry<String, Object> entry : configMap.entrySet()) {
				// For each key in config
				if (drawNumber <= (Double.parseDouble(entry.getValue().toString()) * Math.pow(10, maxDecimalPlaces)) + currentDraw) {
					// If the random number is less than or equal to the key's value + the current draw
					getLogger().info("caught" + entry.getKey());
					if (blockMaterial == null) {
						blockMaterial = Material.getMaterial(entry.getKey());
						getLogger().info("should out: " + blockMaterial);
					}

					// Set the output block to the material associated with the key name
				} else {
					currentDraw = (int) (currentDraw + (Double.parseDouble(entry.getValue().toString()) * Math.pow(10, maxDecimalPlaces)));
					// Otherwise add to the current draw
					getLogger().info("added");
				}
			}
			return blockMaterial;
			// Return the output
		}
}