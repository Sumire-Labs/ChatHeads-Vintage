/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chad_heads_cre;

import dzwdz.chad_heads_cre.config.ChatHeadsConfigState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ChadHeadsCRE.MOD_ID,
        name = ChadHeadsCRE.MOD_NAME,
        version = "@{MOD_VERSION}",
        acceptedMinecraftVersions = "[1.12.2]",
        acceptableRemoteVersions = "*",
        dependencies = "required-after:cleanroom@[@{CLEANROOM_VERSION},)",
        clientSideOnly = true
)
public final class ChadHeadsCRE {

    public static final String MOD_ID = "chad_heads_cre";
    public static final String MOD_NAME = "Chad Heads CRE";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Compatibility.initialize();
        ChatHeadsConfigState.reloadAliases();
        LOGGER.info("Initialized {} {}", MOD_NAME, "@{MOD_VERSION}");
    }
}
