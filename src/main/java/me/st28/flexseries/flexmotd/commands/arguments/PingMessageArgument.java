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
package me.st28.flexseries.flexmotd.commands.arguments;

import me.st28.flexseries.flexlib.command.CommandContext;
import me.st28.flexseries.flexlib.command.argument.DummyArgument;
import me.st28.flexseries.flexlib.plugin.FlexPlugin;
import me.st28.flexseries.flexmotd.backend.PingManager;

import java.util.ArrayList;
import java.util.List;

public class PingMessageArgument extends DummyArgument {

    public PingMessageArgument(String name, boolean isRequired) {
        super(name, isRequired);
    }

    @Override
    public List<String> getSuggestions(CommandContext context, String input) {
        return new ArrayList<>(FlexPlugin.getGlobalModule(PingManager.class).getMessages().keySet());
    }

}