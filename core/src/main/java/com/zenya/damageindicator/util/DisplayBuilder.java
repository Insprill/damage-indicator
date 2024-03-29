/*
 *     Damage Indicator
 *     Copyright (C) 2021  Zenya
 *     Copyright (C) 2021-2022  Pierce Thompson
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.zenya.damageindicator.util;

import com.zenya.damageindicator.storage.StorageFileManager;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DisplayBuilder {

    private String text;
    private Double value;
    private final List<ChatColor> colors = new ArrayList<>();

    public DisplayBuilder() {
    }

    public DisplayBuilder(String text) {
        this.text = text;
    }

    public DisplayBuilder(double text) {
        this.text = String.valueOf(text);
    }

    public String getText() {
        return text;
    }

    public String getRawText() {
        return ChatColor.stripColor(text);
    }

    public DisplayBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public DisplayBuilder withText(double text) {
        this.text = String.valueOf(text);
        return this;
    }

    public DisplayBuilder withValue(double value) {
        this.value = value;
        return this;
    }

    public DisplayBuilder withColor(ChatColor... colors) {
        this.colors.addAll(Arrays.asList(colors));
        return this;
    }

    public DisplayBuilder withColor(char... codes) {
        for (char code : codes) {
            this.colors.add(ChatColor.getByChar(code));
        }
        return this;
    }

    public DisplayBuilder withRainbow() {
        return withColor('a', 'b', 'c', 'd', 'e').randomize();
    }

    public DisplayBuilder randomize() {
        Random randObj = ThreadLocalRandom.current();
        int n = colors.size();
        for (int i = 0; i < n; i++) {
            int randomValue = i + randObj.nextInt(n - i);
            ChatColor randomElement = colors.get(randomValue);
            colors.set(randomValue, colors.get(i));
            colors.set(i, randomElement);
        }
        return this;
    }

    public String build() {
        //%damage% placeholder
        if (value != null) {
            String val;
            try {
                val = String.format("%." + StorageFileManager.getConfig().getInt("indicator-decimals") + "f", value);
            } catch (Exception exc) {
                //Users doing something dumb in config
                val = String.format("%.2f", value);
            }
            text = text.replaceAll("%value%", val);
        }

        //%rainbow% & %<color>% placeholder
        String colorModifier;
        try {
            colorModifier = text.substring(text.indexOf('%'), text.indexOf('%', text.indexOf('%') + 1) + 1);
            text = text.replaceAll(colorModifier, "");
            if (colorModifier.equalsIgnoreCase("%rainbow%")) {
                withRainbow();
            } else {
                char[] colors = new char[colorModifier.length() - 2];
                colorModifier.getChars(1, colorModifier.length() - 1, colors, 0);
                withColor(colors);
            }
        } catch (StringIndexOutOfBoundsException exc) {
            //Silence errors if placeholders dont exist
        }

        //Insert colors
        if (colors.size() > 0) {
            int textIndex = text.length() - 1;
            int colorIndex = colors.size() - 1;
            while (textIndex >= 0) {
                setColorAt(textIndex, colorIndex);
                textIndex--;
                colorIndex--;
                if (colorIndex < 0) colorIndex = colors.size() - 1;
            }
        }

        //Insert formatting
        if (StorageFileManager.getConfig().getBool("bold-indicators")) setFormat('l');
        if (StorageFileManager.getConfig().getBool("italic-indicators")) setFormat('o');
        if (StorageFileManager.getConfig().getBool("underline-indicators")) setFormat('m');
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private void setColorAt(int textIndex, int colorIndex) {
        StringBuilder builder = new StringBuilder(text);
        builder.insert(textIndex, "&" + colors.get(colorIndex).getChar());
        text = builder.toString();
    }

    private void setFormat(char format) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (text.toCharArray()[i] == '&') {
                i++;
                formatted.append("&").append(text.toCharArray()[i]);
                continue;
            }
            formatted.append("&").append(format).append(text.toCharArray()[i]);
        }
        text = formatted.toString();
    }

}
