package com.test.liftoff.Controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.test.liftoff.Model.SaveData;

import java.util.ArrayList;

public class SaveManager {
    private static final Json json = new Json();
    private static final String SAVE_FILE = "all_saves.json";


    public static class SaveContainer {
        public ArrayList<SaveData> slots = new ArrayList<>();
    }

    private static SaveContainer loadContainer() {
        if (!Gdx.files.local(SAVE_FILE).exists()) {
            return new SaveContainer();
        }
        try {
            String text = Gdx.files.local(SAVE_FILE).readString();
            SaveContainer container = json.fromJson(SaveContainer.class, text);
            return (container != null && container.slots != null) ? container : new SaveContainer();
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Error reading database: " + e.getMessage());
            return new SaveContainer();
        }
    }

    public static void save(int slot, SaveData data) {
        try {
            SaveContainer container = loadContainer();


            container.slots.removeIf(s -> s.slotId == slot);


            data.slotId = slot;
            data.timestamp = System.currentTimeMillis();
            container.slots.add(data);

            String serializedText = json.toJson(container);
            Gdx.files.local(SAVE_FILE).writeString(serializedText, false);
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Error writing database: " + e.getMessage());
        }
    }

    public static SaveData load(int slot) {
        SaveContainer container = loadContainer();
        for (SaveData d : container.slots) {
            if (d.slotId == slot) return d;
        }
        return null;
    }

    public static boolean hasSave(int slot) {
        SaveContainer container = loadContainer();
        for (SaveData d : container.slots) {
            if (d.slotId == slot) return true;
        }
        return false;
    }


    public static ArrayList<SaveSlotInfo> getAllSlotsSorted() {
        SaveContainer container = loadContainer();
        ArrayList<SaveSlotInfo> displayList = new ArrayList<>();


        for (SaveData d : container.slots) {
            displayList.add(new SaveSlotInfo(d.slotId, true, d.timestamp));
        }


        for (int i = 1; i <= 4; i++) {
            final int lookupId = i;
            boolean slotPopulated = container.slots.stream().anyMatch(s -> s.slotId == lookupId);
            if (!slotPopulated) {
                displayList.add(new SaveSlotInfo(lookupId, false, 0L));
            }
        }


        displayList.sort((a, b) -> {
            if (a.exists && b.exists) {
                return Long.compare(b.timestamp, a.timestamp);
            }
            if (a.exists) return -1;
            if (b.exists) return 1;
            return Integer.compare(a.slotId, b.slotId);
        });

        return displayList;
    }

    public static class SaveSlotInfo {
        public int slotId;
        public boolean exists;
        public long timestamp;

        public SaveSlotInfo(int slotId, boolean exists, long timestamp) {
            this.slotId = slotId;
            this.exists = exists;
            this.timestamp = timestamp;
        }
    }
}
