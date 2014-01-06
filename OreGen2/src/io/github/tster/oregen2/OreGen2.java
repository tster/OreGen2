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
		double doubleFromString;
		Map.Entry<String, Object> entry;
		
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
			if (event.getToBlock().getType() == Material.COBBLESTONE && event.getToBlock().getRelative(event.getFace()).getType() == Material.STATIONARY_WATER) {
	        	// If the 'to' block is cobble now and the block behind that is water
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
			    }
			}
			// Long method to find the highest number of decimal places in the config file
			// This value will be used to convert decimal chances into draws
			for (Object value : configMap.values()) {
				try {
					doubleFromString = Double.parseDouble(value.toString());
				} catch (NumberFormatException e) {
					getLogger().info("Invalid config.yml, please review it.");
				}
				totalDraws = totalDraws + doubleFromString * Math.pow(10,maxDecimalPlaces);
			}
			// Finds the total number of draws - in doing so converting decimal chances to draws
			drawNumber = rand.nextInt((int) totalDraws) + 1;
			// Generates a random number between 1 and the total number of draws
			for (Map.Entry<String, Object> entry : configMap.entrySet()) {
				// For each key in config
				try {
					// Try to get the chance as a double
					doubleFromString = Double.parseDouble(entry.getValue().toString());
				} catch (NumberFormatException e) {
					// Catch exceptions in conversion and respond with an error message
					getLogger().info("Invalid number'"+entry.getValue().toString()+"'. Please review config.yml!");
				}
				if (drawNumber <= doubleFromString * Math.pow(10, maxDecimalPlaces) + currentDraw) {
					// If the random number is less than or equal to the key's value + the current draw
					if (blockMaterial == null) {
						try {
							// Try converting the key to a material
							blockMaterial = Material.getMaterial(entry.getKey());
						} catch (Exception e) {
							// Catch any exceptions and respond with an error message
							getLogger().info("Invalid material name '"+entry.getKey()+"'. Please review config.yml!");
						}
					}

					// Set the output block to the material associated with the key name
				} else {
					currentDraw = (int) (currentDraw + (Double.parseDouble(entry.getValue().toString()) * Math.pow(10, maxDecimalPlaces)));
					// Otherwise add to the current draw
				}
			}
			return blockMaterial;
			// Return the output
		}
}