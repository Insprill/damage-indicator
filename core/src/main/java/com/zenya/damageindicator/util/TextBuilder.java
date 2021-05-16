package com.zenya.damageindicator.util;

import com.zenya.damageindicator.file.ConfigManager;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TextBuilder {
    private String text;
    private Double value;
    private List<ChatColor> colors = new ArrayList<>();

    public TextBuilder() {

    }

    public TextBuilder(String text) {
        this.text = text;
    }

    public TextBuilder(double text) {
        this.text = String.valueOf(text);
    }

    public String getText() {
        return text;
    }

    public String getRawText() {
        return ChatColor.stripColor(text);
    }

    public TextBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public TextBuilder withText(double text) {
        this.text = String.valueOf(text);
        return this;
    }

    public TextBuilder withValue(double value) {
        this.value = value;
        return this;
    }

    public TextBuilder withColor(ChatColor... colors) {
        this.colors.addAll(Arrays.asList(colors));
        return this;
    }

    public TextBuilder withColor(char... colors) {
        for(char color : colors) {
            this.colors.add(ChatColor.getByChar(color));
        }
        return this;
    }

    public TextBuilder withRainbow() {
        return withColor('a', 'b', 'c', 'd', 'e').randomize();
    }

    public TextBuilder randomize() {
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
        if(value != null) {
            String val;
            try {
                val = String.format("%." + ConfigManager.INSTANCE.getInt("indicator-decimals") + "f", value);
            } catch(Exception exc) {
                //Users doing something dumb in config
                val = String.format("%.2f", value);
            }
            text = text.replaceAll("%value%", val);
        }

        //%rainbow% & %<color>% placeholder
        String colorModifier = "";
        try {
            colorModifier = text.substring(text.indexOf('%'), text.indexOf('%', text.indexOf('%') + 1) + 1);
            text = text.replaceAll(colorModifier, "");
            if(colorModifier.toLowerCase().equals("%rainbow%")) {
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
        if(colors.size() > 0) {
            int textIndex = text.length() - 1;
            int colorIndex = colors.size() - 1;
            while(textIndex >= 0) {
                setColorAt(textIndex, colorIndex);
                textIndex--;
                colorIndex--;
                if(colorIndex < 0) colorIndex = colors.size() - 1;
            }
        }

        //Insert formatting
        if(ConfigManager.INSTANCE.getBool("bold-indicators")) setFormat('l');
        if(ConfigManager.INSTANCE.getBool("italic-indicators")) setFormat('o');
        if(ConfigManager.INSTANCE.getBool("underline-indicators")) setFormat('m');
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private void setColorAt(int textIndex, int colorIndex) {
        StringBuffer buffer = new StringBuffer(text);
        buffer.insert(textIndex, "&" + colors.get(colorIndex).getChar());
        text = buffer.toString();
    }

    private void setFormat(char format) {
        String formatted = "";
        for(int i=0 ; i<text.length(); i++) {
            if(text.toCharArray()[i] == '&') {
                i++;
                formatted += "&" + text.toCharArray()[i];
                continue;
            }
            formatted += "&" + format + text.toCharArray()[i];
        }
        text = formatted;
    }
}
