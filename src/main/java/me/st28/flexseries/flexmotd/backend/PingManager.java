/**
 * FlexMotd - Licensed under the MIT License (MIT)
 *
 * Copyright (c) Stealth2800 <http://stealthyone.com/>
 * Copyright (c) contributors <https://github.com/FlexSeries>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.st28.flexseries.flexmotd.backend;

import me.st28.flexseries.flexlib.log.LogHelper;
import me.st28.flexseries.flexlib.plugin.module.FlexModule;
import me.st28.flexseries.flexlib.plugin.module.ModuleDescriptor;
import me.st28.flexseries.flexlib.storage.flatfile.YamlFileManager;
import me.st28.flexseries.flexmotd.FlexMotd;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class PingManager extends FlexModule<FlexMotd> implements Listener {

    private YamlFileManager dataFile;

    private final Map<String, Map<String, String>> groups = new HashMap<>();

    private String selectedMessage;
    private final Map<String, String> messages = new HashMap<>();

    private File imageDir;
    private String selectedImage;
    private final Map<String, CachedServerIcon> images = new HashMap<>();

    public PingManager(FlexMotd plugin) {
        super(plugin, "ping", "Manages the message that shows up on the client's server list.", new ModuleDescriptor().setGlobal(true).setSmartLoad(false));
    }

    @Override
    protected void handleEnable() {
        dataFile = new YamlFileManager(getDataFolder() + File.separator + "selected.yml");
        imageDir = new File(getDataFolder() + File.separator + "images");
        imageDir.mkdirs();

        FileConfiguration config = dataFile.getConfig();

        selectedMessage = config.getString("selected.message", "default");
        selectedImage = config.getString("selected.image", "default");
    }

    @Override
    protected void handleReload() {
        imageDir.mkdirs();

        FileConfiguration config = getConfig();

        groups.clear();
        messages.clear();
        images.clear();

        // Load groups
        ConfigurationSection groupSec = config.getConfigurationSection("groups");
        if (groupSec != null) {
            for (String name : groupSec.getKeys(false)) {
                ConfigurationSection subSec = groupSec.getConfigurationSection(name);
                Map<String, String> groupData = new HashMap<>();

                for (String subName : subSec.getKeys(false)) {
                    groupData.put(subName, subSec.getString(subName));
                }

                groups.put(name.toLowerCase(), groupData);
            }
        }

        // Load messages
        ConfigurationSection messageSec = config.getConfigurationSection("message.messages");
        for (String name : messageSec.getKeys(false)) {
            messages.put(name.toLowerCase(), ChatColor.translateAlternateColorCodes('&', StringEscapeUtils.unescapeJava(messageSec.getString(name))));
        }

        // Load images
        ConfigurationSection imageSec = config.getConfigurationSection("image.images");

        for (String name : imageSec.getKeys(false)) {
            String fileName = imageSec.getString(name);
            File file = fileName == null ? null : new File(imageDir + File.separator + fileName);

            if (file == null || !file.exists()) {
                LogHelper.warning(this, "Image file '" + fileName + "' not found.");
            } else {
                try {
                    images.put(name.toLowerCase(), Bukkit.getServer().loadServerIcon(file));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void handleSave(boolean async) {
        FileConfiguration config = dataFile.getConfig();

        config.set("selected.message", selectedMessage);
        config.set("selected.image", selectedImage);

        dataFile.save();
    }

    public Map<String, Map<String, String>> getGroups() {
        return Collections.unmodifiableMap(groups);
    }

    public void setGroup(String name) {
        Validate.notNull(name, "Name cannot be null.");
        Map<String, String> group = groups.get(name.toLowerCase());
        Validate.notNull(group, "Group '" + name + "' isn't loaded.");

        if (group.containsKey("message")) {
            selectedMessage = group.get("message");
        }

        if (group.containsKey("image")) {
            selectedImage = group.get("image");
        }
    }

    public String getSelectedMessage() {
        return selectedMessage;
    }

    public Map<String, String> getMessages() {
        return Collections.unmodifiableMap(messages);
    }

    public boolean setMessage(String name) {
        Validate.notNull(name, "Name cannot be null.");

        name = name.toLowerCase();

        if (!messages.containsKey(name)) {
            throw new IllegalArgumentException("No message named '" + name + "' is loaded.");
        }

        if (selectedMessage.equals(name)) {
            return false;
        }

        selectedMessage = name;
        return true;
    }

    public String getSelectedImage() {
        return selectedImage;
    }

    public Map<String, CachedServerIcon> getImages() {
        return Collections.unmodifiableMap(images);
    }

    public boolean setImage(String name) {
        Validate.notNull(name, "Name cannot be null.");

        name = name.toLowerCase();

        if (!images.containsKey(name)) {
            throw new IllegalArgumentException("No image named '" + name + "' is loaded.");
        }

        if (selectedImage.equals(name)) {
            return false;
        }

        selectedImage = name;
        return true;
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent e) {
        String currentMessage = selectedMessage == null ? null : messages.get(selectedMessage);
        if (currentMessage == null) {
            if (selectedMessage == null) {
                currentMessage = ChatColor.RED + "No message currently selected.";
            } else {
                currentMessage = ChatColor.RED + "Unknown message: " + ChatColor.GOLD + selectedMessage;
            }
        }

        e.setMotd(currentMessage);

        CachedServerIcon currentImage = selectedImage == null ? null : images.get(selectedImage);
        if (currentImage != null) {
            e.setServerIcon(currentImage);
        }
    }

}