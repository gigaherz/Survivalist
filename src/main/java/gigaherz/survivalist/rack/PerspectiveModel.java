package gigaherz.survivalist.rack;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gigaherz.survivalist.Survivalist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ForgeBlockStateV1;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/*
public class PerspectiveModel implements IBakedModel
{
    private final IBakedModel baseModel;
    private final Map<String, Pair<? extends IBakedModel, Matrix4f>> perspectives;

    public PerspectiveModel(IBakedModel baseModel, Map<String, Pair<? extends IBakedModel, Matrix4f>> perspectives)
    {
        this.baseModel = baseModel;
        this.perspectives = perspectives;
    }

    public PerspectiveModel(TextureAtlasSprite particleSprite, IBakedModel baseModel, TRSRTransformation[] perspectives)
    {
        this.baseModel = baseModel;
        this.perspectives = perspectives;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, long rand)
    {
        return baseModel.getQuads(state, side, rand);
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
    {
        Pair<? extends IBakedModel, Matrix4f> pair = perspectives.get(cameraTransformType.toString().toLowerCase());
        if (pair != null)
        {
            return pair;
        }
        return net.minecraftforge.client.ForgeHooksClient.handlePerspective(this, cameraTransformType);
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return baseModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return baseModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return baseModel.getParticleTexture();
    }

    @Deprecated
    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return baseModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return new ItemOverrideList(Collections.emptyList());
    }

    public static class Model implements IModel
    {
        private final ResourceLocation particle;
        private final ResourceLocation baseModel;
        private final TRSRTransformation[] transformations;

        public Model()
        {
            this.particle = null;
            this.baseModel = null;
            this.transformations = new TRSRTransformation[] {
                    TRSRTransformation.identity(),
                    TRSRTransformation.identity(),
                    TRSRTransformation.identity(),
                    TRSRTransformation.identity()
            };
        }

        public Model(@Nullable ResourceLocation particle, @Nullable ResourceLocation baseModel, TRSRTransformation[] transformations)
        {
            this.particle = particle;
            this.baseModel = baseModel;
            this.transformations = transformations;
        }

        @Override
        public Collection<ResourceLocation> getDependencies()
        {
            if (baseModel != null)
                return Collections.singletonList(baseModel);
            return Collections.emptyList();
        }

        @Override
        public Collection<ResourceLocation> getTextures()
        {
            if (particle != null)
                return Collections.singletonList(particle);
            return Collections.emptyList();
        }

        @Override
        public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            TextureAtlasSprite particleSprite = Minecraft.getInstance().getTextureMapBlocks().getMissingSprite();
            if (particle != null)
                particleSprite = bakedTextureGetter.apply(particle);

            IModel rackModel = baseModel == null ? ModelLoaderRegistry.getMissingModel() : ModelLoaderRegistry.getModelOrMissing(baseModel);
            IBakedModel rackBakedModel = rackModel.bake(state, format, bakedTextureGetter);

            Optional<TRSRTransformation> baseTransform = state.apply(Optional.empty());
            if (baseTransform.isPresent())
            {
                TRSRTransformation value = baseTransform.get();
                transformations[0] = value.compose(transformations[0]);
                transformations[1] = value.compose(transformations[1]);
                transformations[2] = value.compose(transformations[2]);
                transformations[3] = value.compose(transformations[3]);
            }

            return new PerspectiveModel(particleSprite, rackBakedModel, transformations);
        }

        @Override
        public IModelState getDefaultState()
        {
            return TRSRTransformation.identity();
        }

        @Override
        public IModel retexture(ImmutableMap<String, String> textures)
        {
            String particleTexture = textures.get("particle");
            while (particleTexture != null && particleTexture.startsWith("#"))
            {
                particleTexture = textures.get(particleTexture.substring(1));
            }

            ResourceLocation rl = particleTexture != null ? new ResourceLocation(particleTexture) : null;
            return new Model(rl, baseModel, transformations);
        }

        private static final Gson GSON = (new GsonBuilder())
                .registerTypeAdapter(TRSRTransformation.class, ForgeBlockStateV1.TRSRDeserializer.INSTANCE)
                .create();
        @Override
        public IModel process(ImmutableMap<String, String> customData)
        {
            ResourceLocation baseModel = this.baseModel;
            if (customData.containsKey("base_model"))
            {
                String data = GSON.fromJson(customData.get("base_model"), String.class);
                baseModel = new ResourceLocation(data);
                baseModel = new ResourceLocation(baseModel.getNamespace(), "block/" + baseModel.getPath());
            }
            TRSRTransformation[] transformations = Arrays.copyOf(this.transformations, 4);
            for(int i=0;i<4;i++)
            {
                String key = "transform_" + i;
                if (customData.containsKey(key))
                {
                    transformations[i] = GSON.fromJson(customData.get(key), TRSRTransformation.class);
                }
            }
            return new Model(this.particle, baseModel, transformations);
        }
    }

    public static class ModelLoader implements ICustomModelLoader
    {
        public static final ResourceLocation FAKE_LOCATION = Survivalist.location("models/block/custom/rack_with_items");

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            if (!modelLocation.getNamespace().equals(Survivalist.MODID))
                return false;
            return modelLocation.equals(FAKE_LOCATION);
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws Exception
        {
            return new Model();
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {
            // Nothing to do
        }
    }
}
*/