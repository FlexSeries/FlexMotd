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
import me.st28.flexseries.flexlib.message.reference.McmlMessageReference;
import me.st28.flexseries.flexlib.message.variable.MessageVariable;
import me.st28.flexseries.flexlib.player.PlayerExtendedJoinEvent;
import me.st28.flexseries.flexlib.plugin.module.FlexModule;
import me.st28.flexseries.flexlib.plugin.module.ModuleDescriptor;
import me.st28.flexseries.flexmotd.FlexMotd;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MotdManager extends FlexModule<FlexMotd> implements Listener {

    private String current;
    private final Map<String, String> motds = new HashMap<>();

    public MotdManager(FlexMotd plugin) {
        super(plugin, "motd", "Manages MOTDs", new ModuleDescriptor().setGlobal(true).setSmartLoad(false));
    }

    @Override
    protected void handleReload() {
        FileConfiguration config = getConfig();
        motds.clear();

        current = config.getString("current", null);
        if (current != null && current.equals("")) {
            current = null;
        }

        ConfigurationSection motdSec = config.getConfigurationSection("motds");
        if (motdSec != null) {
            for (String key : motdSec.getKeys(false)) {
                if (motds.containsKey(key.toLowerCase())) {
                    LogHelper.warning(this, "A MOTD named '" + key + "' is already loaded.");
                    continue;
                }

                String raw = motdSec.getString(key);
                motds.put(key.toLowerCase(), raw.substring(0, raw.length() - 1));
            }
        }
    }

    public Map<String, String> getMotds() {
        return Collections.unmodifiableMap(motds);
    }

    public String getSelected() {
        return current;
    }

    public String getCurrentMotd() {
        return current == null ? null : motds.get(current.toLowerCase());
    }

    public boolean setMotd(String name) {
        Validate.notNull(name, "Name cannot be null.");

        name = name.toLowerCase();

        if (!motds.containsKey(name)) {
            throw new IllegalArgumentException("No MOTD named '" + name + "' is loaded.");
        }

        if (current.equals(name)) {
            return false;
        }

        current = name;
        return true;
    }

    @EventHandler
    public void onPlayerJoinLoaded(PlayerExtendedJoinEvent e) {
        String motd = getCurrentMotd();

        if (motd != null) {
            e.addLoginMessage(FlexMotd.class, "motd", new McmlMessageReference(MessageVariable.handleReplacements(e.getPlayer(), motd)));
        }
    }

}