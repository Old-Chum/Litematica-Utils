package me.oldchum.litematicautils;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LitematicaUtils implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("litematica-utils");

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
	}
}