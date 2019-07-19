/*
 * DreamHorizonCore
 * Copyright (C) 2019 Dream Horizon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.dreamhorizon.core.helper;

import com.dreamhorizon.core.configuration.ConfigurationHandler;
import com.dreamhorizon.core.configuration.enums.Message;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public class MessageHelper {
    public static final HashMap<String, String> globalPlaceHolders = new HashMap<>();
    private static final Pattern placeHolderPattern = Pattern.compile("(?<=\\{)(.*?)(?=})");
    
    /**
     * Replaces all the global and specified placeholders in the specified Strings
     * The placeholders must have the format {placeholder}.
     * It also replaces the Bukkit colour codes with their colours.
     *
     * @param placeholders {@link HashMap} of {@link String} key and {@link Object} value
     * @param messages     {@link String...} to have placeholders replaced.
     * @return {@link String} with all placeholders replaced.
     */
    public static String[] formatMessage(HashMap<String, Object> placeholders, String... messages) {
        if (placeholders == null) {
            placeholders = new HashMap<>();
        }
        placeholders.putAll(globalPlaceHolders);
        List<String> formattedMsgs = new ArrayList<>();
        for (String msg : messages) {
            Matcher m = placeHolderPattern.matcher(msg);
            while (m.find()) {
                for (int i = 1; i <= m.groupCount(); i++) {
                    if (placeholders.get(m.group(i)) == null) {
                        msg = msg.replace("{" + m.group(i) + "}", "");
                    } else {
                        msg = msg.replace("{" + m.group(i) + "}", String.valueOf(placeholders.get(m.group(i))));
                    }
                }
                formattedMsgs.add(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
        return formattedMsgs.toArray(new String[0]);
    }
    
    /**
     * Replaces all the global in the specified Strings
     * The placeholders must have the format {placeholder}.
     * It also replaces the Bukkit colour codes with their colours.
     *
     * @param messages {@link String...} to have placeholders replaced.
     * @return {@link String} with all placeholders replaced.
     */
    public static String[] formatMessage(String... messages) {
        List<String> formattedMsgs = new ArrayList<>();
        for (String msg : messages) {
            Matcher m = placeHolderPattern.matcher(msg);
            while (m.find()) {
                for (int i = 1; i <= m.groupCount(); i++) {
                    if (globalPlaceHolders.get(m.group(i)) == null) {
                        msg = msg.replace("{" + m.group(i) + "}", "");
                    } else {
                        msg = msg.replace("{" + m.group(i) + "}", String.valueOf(globalPlaceHolders.get(m.group(i))));
                    }
                }
                formattedMsgs.add(ChatColor.translateAlternateColorCodes('&', msg));
                
            }
        }
        return formattedMsgs.toArray(new String[0]);
    }
    
    /**
     * Replaces all the global and specified placeholders in the specified String
     * The placeholders must have the format {placeholder}.
     * It also replaces the Bukkit colour codes with their colours.
     *
     * @param placeholders {@link HashMap} of {@link String} key and {@link Object} value
     * @param message      {@link String} to have placeholders replaced.
     * @return {@link String} with all placeholders replaced.
     */
    public static String formatMessage(HashMap<String, Object> placeholders, String message) {
        placeholders.putAll(globalPlaceHolders);
        Matcher m = placeHolderPattern.matcher(message);
        while (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                if (placeholders.get(m.group(i)) != null) {
                    message = message.replace("{" + m.group(i) + "}", String.valueOf(placeholders.get(m.group(i))));
                }
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * Replaces all the global in the specified String
     * The placeholders must have the format {placeholder}.
     * It also replaces the Bukkit colour codes with their colours.
     *
     * @param message {@link String} to have placeholders replaced.
     * @return {@link String} with all placeholders replaced.
     */
    public static String formatMessage(String message) {
        Matcher m = placeHolderPattern.matcher(message);
        while (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                if (globalPlaceHolders.get(m.group(i)) != null) {
                    message = message.replace("{" + m.group(i) + "}", String.valueOf(globalPlaceHolders.get(m.group(i))));
                }
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static String formatMessage(HashMap<String, Object> placeHolders, Message message) {
        Object o = ConfigurationHandler.getInstance().getConfig("messages").get(message);
        return formatMessage(placeHolders, (String) o);
    }
}
