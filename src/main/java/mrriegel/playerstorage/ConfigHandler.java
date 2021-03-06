package mrriegel.playerstorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigHandler {

    public static Configuration config;

    public static boolean infiniteSpace, keeper;
    public static int itemCapacity;
    public static Map<String, Unit2> apples = new HashMap<>();
    @SuppressWarnings("serial")
    public static List<String> appleList = new ArrayList<String>(6) {
        @Override
        public String get(int index) {
            if (index >= size()) {
                return "";
            }
            return super.get(index);
        }
    };

    public static void refreshConfig(File file) {
        config = new Configuration(file);
        Gson gson = new Gson();
        infiniteSpace = config.getBoolean("infiniteSpace", Configuration.CATEGORY_GENERAL, false, "Enable infinite inventory.");
        itemCapacity = config.getInt("itemCapacity", Configuration.CATEGORY_GENERAL, 2000, 0, Integer.MAX_VALUE, "Item capacity at the beginning");
        Property prop = config.get(Configuration.CATEGORY_GENERAL, "appleTiers", Arrays.asList(//
                new Unit1("blockIron", 3200), //
                new Unit1("blockGold", 25600), //
                new Unit1("blockDiamond", 102400), //
                new Unit1("netherStar", 2000000))//
                .stream().map(gson::toJson).toArray(String[]::new));
        prop.setLanguageKey("appleTiers");
        prop.setComment("Tiers for apples");
        for (String s : prop.getStringList()) {
            try {
                @SuppressWarnings("serial")
                Unit1 unit = gson.fromJson(s, new TypeToken<Unit1>() {
                }.getType());
                if (unit != null) {
                    if (!apples.containsKey(unit.oreName)) {
                        apples.put(unit.oreName, unit.entry);
                    }
                    appleList.add(unit.oreName);
                }
            } catch (JsonParseException e) {
                Logger.getLogger(PlayerStorage.MODNAME).warning("Your config file is not correct. " + s);
                e.printStackTrace();
            }
        }

        if (config.hasChanged()) {
            config.save();
        }
    }

    static class Unit1 {

        public String oreName;
        public Unit2 entry;

        public Unit1(String oreName, int i1) {
            super();
            this.oreName = oreName;
            this.entry = new Unit2(i1);
        }
    }

    public static class Unit2 {

        public int itemLimit;

        public Unit2(int itemLimit) {
            super();
            this.itemLimit = itemLimit;
        }
    }

}
