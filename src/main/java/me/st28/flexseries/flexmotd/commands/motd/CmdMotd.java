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
package me.st28.flexseries.flexmotd.commands.motd;

import me.st28.flexseries.flexcore.command.FlexCommand;
import me.st28.flexseries.flexcore.command.FlexCommandSettings;
import me.st28.flexseries.flexcore.command.exceptions.CommandInterruptedException;
import me.st28.flexseries.flexcore.list.ListBuilder;
import me.st28.flexseries.flexcore.message.MessageReference;
import me.st28.flexseries.flexcore.message.variable.MessageVariable;
import me.st28.flexseries.flexcore.plugin.FlexPlugin;
import me.st28.flexseries.flexmotd.FlexMotd;
import me.st28.flexseries.flexmotd.backend.MotdManager;
import me.st28.flexseries.flexmotd.permission.PermissionNodes;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public final class CmdMotd extends FlexCommand<FlexMotd> {

    public CmdMotd(FlexMotd plugin) {
        super(plugin, "motd", null, new FlexCommandSettings<>().setPlayerOnly(true).permission(PermissionNodes.MOTD));

        registerSubcommand(new SCmdMotdList(this));
        registerSubcommand(new SCmdMotdSet(this));
    }

    @Override
    public void runCommand(CommandSender sender, String command, String label, String[] args, Map<String, String> parameters) {
        MotdManager motdManager = FlexPlugin.getRegisteredModule(MotdManager.class);
        String motd = motdManager.getCurrentMotd();

        if (motd == null) {
            throw new CommandInterruptedException(MessageReference.create(FlexMotd.class, "errors.motd_not_set"));
        }

        ListBuilder builder = new ListBuilder("subtitle", "MOTD", motdManager.getSelected(), label);

        builder.addMessage(MessageReference.createPlain(MessageVariable.handleReplacements((Player) sender, motd)));

        builder.sendTo(sender, 1);
    }

}