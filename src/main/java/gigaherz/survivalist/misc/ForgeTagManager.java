package gigaherz.survivalist.misc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/*
public class ForgeTagManager extends ReloadListener<Map<ResourceLocation, List<JsonObject>>>
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String SUFFIX = ".json";

    private static final Map<IForgeRegistry<?>, String> paths = Maps.newConcurrentMap();

    private final Gson gson;

    private class

    @Override
    protected Map<String, Map<ResourceLocation, List<JsonObject>>> prepare(IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        Map<ResourceLocation, List<JsonObject>> objects = Maps.newHashMap();

        String path = "tags/tile_entity_types";
        for(ResourceLocation loc : resourceManagerIn.getAllResourceLocations(path, f -> f.endsWith(SUFFIX)))
        {
            String path = loc.getPath();
            ResourceLocation regName = new ResourceLocation(loc.getNamespace(), path.substring(path.length(), path.length() - path.length()-SUFFIX.length()));
            try
            {
                for(IResource res : resourceManagerIn.getAllResources(loc))
                {
                    try (
                            InputStream inputstream = res.getInputStream();
                            Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                    ) {
                        JsonObject contents = JSONUtils.fromJson(this.gson, reader, JsonObject.class);
                        if (contents != null) {
                            objects.computeIfAbsent(regName, k -> Lists.newArrayList()).add(contents);
                        } else {
                            LOGGER.error("Couldn't load data file {} from {} as it's null or empty", regName, loc);
                        }
                    } catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
                        LOGGER.error("Couldn't parse data file {} from {}", regName, loc, jsonparseexception);
                    }
                }
            }
            catch (IOException e)
            {
                LOGGER.error("Couldn't get resources for location {}", loc);
            }
        }

        return objects;
    }

    @Override
    protected void apply(Map<ResourceLocation, List<JsonObject>> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {

    }
}
*/