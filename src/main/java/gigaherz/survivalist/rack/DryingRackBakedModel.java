package gigaherz.survivalist.rack;

import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gigaherz.survivalist.Survivalist;
import gigaherz.survivalist.misc.QuadTransformer;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ForgeBlockStateV1;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.resource.IResourceType;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DryingRackBakedModel implements IBakedModel
{
    private final TextureAtlasSprite particle;
    private final IBakedModel rackBakedModel;

    private final TRSRTransformation[] itemTransforms;

    private final Map[] caches = new Map[] {
            Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap()
    };

    private final ItemOverrideList overrides;

    public DryingRackBakedModel(ModelBakery bakery, IUnbakedModel original, Function<ResourceLocation, IUnbakedModel> modelGetter,
                                Function<ResourceLocation, TextureAtlasSprite> textureGetter, VertexFormat format,
                                TextureAtlasSprite particle, IBakedModel rackBakedModel, TRSRTransformation[] itemTransforms)
    {
        this.particle = particle;
        this.rackBakedModel = rackBakedModel;
        this.itemTransforms = itemTransforms;
        this.overrides = new ItemOverrideList(bakery, original, modelGetter, textureGetter, Collections.emptyList(), format);
    }

    private static final Direction[] faces = Streams.concat(Arrays.stream(Direction.values()), Stream.of((Direction)null)).toArray(Direction[]::new);

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
        return getQuads(state, side, rand, null);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nullable IModelData extraData)
    {
        List<BakedQuad> quads = Lists.newArrayList();

        BlockRenderLayer renderLayer = MinecraftForgeClient.getRenderLayer();
        if (renderLayer == BlockRenderLayer.SOLID)
        {
            quads.addAll(rackBakedModel.getQuads(state, side, rand));
        }
        else if (renderLayer == BlockRenderLayer.CUTOUT && side == null && extraData != null)
        {
            ItemRenderer renderItem = Minecraft.getInstance().getItemRenderer();
            World world = Minecraft.getInstance().world;

            DryingRackItemsStateData items = extraData.getData(DryingRackTileEntity.CONTAINED_ITEMS_DATA);

            for(int i = 0; i < 4; i++)
            {
                ItemStack stack = items.stacks[i];
                if (stack.isEmpty())
                    continue;

                IBakedModel model = renderItem.getItemModelWithOverrides(stack, world, null);
                Pair<? extends IBakedModel, Matrix4f> pair = model.handlePerspective(ItemCameraTransforms.TransformType.FIXED);
                model = pair.getLeft();
                Matrix4f matrix1 = pair.getRight();

                @SuppressWarnings("unchecked")
                Map<IBakedModel, List<BakedQuad>> cache = (Map<IBakedModel, List<BakedQuad>>)caches[i];

                List<BakedQuad> cachedQuads = cache.get(model);
                if (true)//cachedQuads == null)
                {
                    Matrix4f matrix = new Matrix4f();
                    matrix.setIdentity();

                    Matrix4f matrix2 = itemTransforms[i].getMatrix(Direction.NORTH); // FIXME
                    if (matrix2 != null)
                    {
                        matrix.mul(matrix2);
                    }

                    if (matrix1 != null)
                    {
                        matrix.mul(matrix1);
                    }

                    cachedQuads = Lists.newArrayList();
                    for (Direction face : faces)
                    {
                        List<BakedQuad> inQuads = model.getQuads(null, face, rand);
                        List<BakedQuad> outQuads = QuadTransformer.processMany(inQuads, matrix);

                        cachedQuads.addAll(outQuads);
                    }
                    cache.put(model, cachedQuads);
                }
                quads.addAll(cachedQuads);
            }
        }

        return quads;
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return true;
    }

    @Override
    public boolean isGui3d()
    {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return particle;
    }

    @Deprecated
    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return overrides;
    }

    public static class Model implements IUnbakedModel
    {
        private final ResourceLocation particle;
        private final ResourceLocation baseModel;
        private final TRSRTransformation[] transformations;
        private IUnbakedModel baseModelModel;

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
        public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
        {
            List<ResourceLocation> list = new ArrayList<>();
            if (particle != null)
                list.add(particle);
            if (baseModel != null)
            {
                if (baseModelModel == null)
                    baseModelModel = ModelLoaderRegistry.getModelOrMissing(baseModel);
                list.addAll(baseModelModel.getTextures(modelGetter, missingTextureErrors));
            }
            return list;
        }

        @Nullable
        @Override
        public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format)
        {
            TextureAtlasSprite particleSprite = spriteGetter.apply(particle);

            IModel rackModel;
            if (baseModel == null)
            {
                rackModel = ModelLoaderRegistry.getMissingModel();
            }
            else
            {
                if (baseModelModel == null)
                    baseModelModel = ModelLoaderRegistry.getModelOrMissing(baseModel);
                rackModel = baseModelModel;
            }
            IBakedModel rackBakedModel = rackModel.bake(bakery, spriteGetter, sprite, format);

            Optional<TRSRTransformation> baseTransform = sprite.getState().apply(Optional.empty());
            if (baseTransform.isPresent())
            {
                TRSRTransformation value = baseTransform.get();
                transformations[0] = value.compose(transformations[0]);
                transformations[1] = value.compose(transformations[1]);
                transformations[2] = value.compose(transformations[2]);
                transformations[3] = value.compose(transformations[3]);
            }

            return new DryingRackBakedModel(bakery, this, bakery::getUnbakedModel, spriteGetter, format, particleSprite, rackBakedModel, transformations);
        }

        @Override
        public IModelState getDefaultState()
        {
            return TRSRTransformation.identity();
        }

        @Override
        public IUnbakedModel retexture(ImmutableMap<String, String> textures)
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
        public IUnbakedModel process(ImmutableMap<String, String> customData)
        {
            ResourceLocation baseModel = this.baseModel;
            if (customData.containsKey("base_model"))
            {
                String data = GSON.fromJson(customData.get("base_model"), String.class);
                baseModel = new ResourceLocation(data);
                //baseModel = new ResourceLocation(baseModel.getNamespace(), "block/" + baseModel.getPath());
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
        public static final ResourceLocation FAKE_LOCATION = Survivalist.location("models/custom/rack_with_items");

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.equals(FAKE_LOCATION);
        }

        @Override
        public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception
        {
            return new Model();
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {

        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate)
        {

        }
    }
}
