/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads;

import dzwdz.chat_heads.config.ChatHeadsConfigState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ChatHeadsVintage.MOD_ID,
        name = ChatHeadsVintage.MOD_NAME,
        version = Tags.VERSION,
        acceptedMinecraftVersions = "[1.12.2]",
        acceptableRemoteVersions = "*",
        dependencies = "after:mixinbooter",
        clientSideOnly = true
)
public final class ChatHeadsVintage {

    public static final String MOD_ID = "chat_heads";
    public static final String MOD_NAME = "Chat Heads Vintage";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Compatibility.initialize();
        ChatHeadsConfigState.reloadAliases();
        LOGGER.info("Initialized {} {}", MOD_NAME, Tags.VERSION);
    }
}
