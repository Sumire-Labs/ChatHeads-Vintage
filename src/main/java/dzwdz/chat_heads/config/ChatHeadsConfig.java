/*
 * Copyright (c) 2020 dzwdz
 * Copyright (c) 2026 Sumire Labs, s12kuma01
 * SPDX-License-Identifier: MPL-2.0
 */
package dzwdz.chat_heads.config;

import dzwdz.chat_heads.ChatHeadsVintage;
import net.minecraftforge.common.config.Config;

@Config(modid = ChatHeadsVintage.MOD_ID, name = ChatHeadsVintage.MOD_ID, category = "")
public final class ChatHeadsConfig {

    @Config.Name("general")
    @Config.LangKey("chat_heads.config.general")
    public static General general = new General();

    @Config.Name("detection")
    @Config.LangKey("chat_heads.config.detection")
    public static Detection detection = new Detection();

    private ChatHeadsConfig() {
    }

    public static final class General {

        @Config.Comment("Master switch for chat head rendering and text offsets.")
        @Config.LangKey("chat_heads.config.enabled")
        public boolean enabled = true;

        @Config.Comment({
                "BEFORE_NAME places the head immediately before the detected sender name.",
                "BEFORE_LINE places the head in a fixed column before the whole chat line."
        })
        @Config.LangKey("chat_heads.config.render_position")
        public RenderPosition renderPosition = RenderPosition.BEFORE_NAME;

        @Config.Comment("Align messages without a detected player with player messages in BEFORE_LINE mode.")
        @Config.LangKey("chat_heads.config.offset_non_player_text")
        public boolean offsetNonPlayerText = true;

        @Config.Comment("Draw a small shadow behind player heads.")
        @Config.LangKey("chat_heads.config.draw_shadow")
        public boolean drawShadow = true;

        @Config.Comment("Scales the hat layer outwards to create a subtle 3D effect.")
        @Config.LangKey("chat_heads.config.three_dee_ness")
        @Config.RangeDouble(min = 0.0D, max = 1.0D)
        @Config.SlidingOption
        public double threeDeeNess = 0.0D;
    }

    public static final class Detection {

        @Config.Comment({
                "STRICT trusts structured click commands only.",
                "HEURISTIC additionally scans messages for online player names, display names and aliases."
        })
        @Config.LangKey("chat_heads.config.sender_detection")
        public SenderDetectionMode senderDetection = SenderDetectionMode.HEURISTIC;

        @Config.Comment("Try to identify players in system messages such as join and leave messages.")
        @Config.LangKey("chat_heads.config.handle_system_messages")
        public boolean handleSystemMessages = true;

        @Config.Comment({
                "Learn and save nickname aliases from EssentialsX /realname responses.",
                "The expected response format is: nickname is profileName"
        })
        @Config.LangKey("chat_heads.config.detect_name_aliases")
        public boolean detectNameAliases = true;

        @Config.Comment({
                "Manual nickname mappings in the form nickname=profileName.",
                "Invalid entries are ignored and reported in the log."
        })
        @Config.LangKey("chat_heads.config.name_aliases")
        public String[] nameAliases = new String[0];
    }

}
