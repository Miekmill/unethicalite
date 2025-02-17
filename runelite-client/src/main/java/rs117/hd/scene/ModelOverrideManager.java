package rs117.hd.scene;

import com.google.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import rs117.hd.HdPlugin;
import rs117.hd.scene.model_overrides.ModelOverride;
import rs117.hd.utils.AABB;
import rs117.hd.utils.Env;
import rs117.hd.utils.ModelHash;
import rs117.hd.utils.ResourcePath;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;

import static rs117.hd.utils.ResourcePath.loadJson;
import static rs117.hd.utils.ResourcePath.path;

@Singleton
@Slf4j
public class ModelOverrideManager {
    private static final String ENV_MODEL_OVERRIDES = "RLHD_MODEL_OVERRIDES_PATH";
    private static final ResourcePath modelOverridesPath =  Env.getPathOrDefault(ENV_MODEL_OVERRIDES,
        () -> path(ModelOverrideManager.class, "model_overrides.json"));

    @Inject
    private Client client;

    @Inject
    private HdPlugin plugin;

    private final HashMap<Long, ModelOverride> modelOverrides = new HashMap<>();
    private final HashMap<Long, AABB[]> modelsToHide = new HashMap<>();

    public void startUp() {

            modelOverrides.clear();
            modelsToHide.clear();

            try {
                ModelOverride[] entries = loadJson(this.getClass().getResourceAsStream("model_overrides.json"), ModelOverride[].class);
                if (entries == null)
                    throw new IOException("Empty or invalid path ");
                for (ModelOverride entry : entries) {
                    for (int npcId : entry.npcIds)
                        addEntry(ModelHash.packUuid(npcId, ModelHash.TYPE_NPC), entry);
                    for (int objectId : entry.objectIds)
                        addEntry(ModelHash.packUuid(objectId, ModelHash.TYPE_OBJECT), entry);
                }
                if (client.getGameState() == GameState.LOGGED_IN)
                    plugin.reloadScene();
                log.debug("Loaded {} model overrides", modelOverrides.size());
            } catch (IOException ex) {
                log.error("Failed to load model overrides:", ex);
            }
    }

    private void addEntry(long uuid, ModelOverride entry) {
        ModelOverride old = modelOverrides.put(uuid, entry);
        if (old != null)
            log.debug("ID {} clashes between entries '{}' and '{}'", ModelHash.getIdOrIndex(uuid), entry.description, old.description);
        modelsToHide.put(uuid, entry.hideInAreas);
    }

    public boolean shouldHideModel(long hash, int x, int z) {
        long uuid = ModelHash.getUuid(client, hash);

        AABB[] aabbs = modelsToHide.get(uuid);
        if (aabbs != null && hasNoActions(uuid)) {
            WorldPoint location = ModelHash.getWorldLocation(client, x, z);
            for (AABB aabb : aabbs)
                if (aabb.contains(location))
                    return true;
        }

        return false;
    }

    private boolean hasNoActions(long uuid) {
        int id = ModelHash.getIdOrIndex(uuid);
        int type = ModelHash.getType(uuid);

        String[] actions = {};

        switch (type) {
            case ModelHash.TYPE_OBJECT:
                actions = client.getObjectDefinition(id).getActions();
                break;
            case ModelHash.TYPE_NPC:
                actions = client.getNpcDefinition(id).getActions();
                break;
        }

        for (String action : actions) {
            if (action != null)
                return false;
        }

        return true;
    }

    @NonNull
    public ModelOverride getOverride(long hash) {
        return modelOverrides.getOrDefault(ModelHash.getUuid(client, hash), ModelOverride.NONE);
    }
}
