package gigaherz.survivalist.rack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class DryingRackBakedModel implements IDynamicBakedModel
{
    private final TextureAtlasSprite particle;
    private final IBakedModel rackBakedModel;

    private final TRSRTransformation[] itemTransforms;

    private final List<Map<Pair<IBakedModel,Matrix4f>, List<BakedQuad>>> caches = Lists.newArrayList(
            Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap()
    );

    private final ItemOverrideList overrides;
    private final VertexFormat format;

    public DryingRackBakedModel(ModelBakery bakery, IUnbakedModel original, Function<ResourceLocation, IUnbakedModel> modelGetter,
                                Function<ResourceLocation, TextureAtlasSprite> textureGetter, VertexFormat format,
                                TextureAtlasSprite particle, IBakedModel rackBakedModel, TRSRTransformation[] itemTransforms)
    {
        this.particle = particle;
        this.rackBakedModel = rackBakedModel;
        this.itemTransforms = itemTransforms;
        this.overrides = new ItemOverrideList(bakery, original, modelGetter, textureGetter, Collections.emptyList(), format);
        this.format = format;
    }

    private static final Direction[] faces = Streams.concat(Arrays.stream(Direction.values()), Stream.of((Direction)null)).toArray(Direction[]::new);

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        List<BakedQuad> quads = Lists.newArrayList();

        BlockRenderLayer renderLayer = MinecraftForgeClient.getRenderLayer();
        if (renderLayer == BlockRenderLayer.SOLID)
        {
            quads.addAll(rackBakedModel.getQuads(state, side, rand));
        }
        else if (renderLayer == BlockRenderLayer.CUTOUT && side == null)
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

                if (stack.getItem() == Items.TRIDENT)
                {
                    model = Minecraft.getInstance().getItemRenderer().getItemModelMesher().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
                }

                Pair<? extends IBakedModel, Matrix4f> pair = model.handlePerspective(ItemCameraTransforms.TransformType.FIXED);
                model = pair.getLeft();
                Matrix4f matrix1 = pair.getRight();

                if (!model.isBuiltInRenderer())
                {
                    @SuppressWarnings("unchecked")
                    Map<Pair<IBakedModel, Matrix4f>, List<BakedQuad>> cache = caches.get(i);

                    Pair<IBakedModel, Matrix4f> pair2 = Pair.of(model, matrix1);
                    List<BakedQuad> cachedQuads = cache.get(pair2);
                    if (true)//cachedQuads == null)
                    {
                        javax.vecmath.Matrix4f matrix = new javax.vecmath.Matrix4f();
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
                            List<BakedQuad> outQuads = new QuadTransformer(format, new TRSRTransformation(matrix)).processMany(inQuads);

                            cachedQuads.addAll(outQuads);
                        }

                        cache.put(pair2, cachedQuads);
                    }
                    quads.addAll(cachedQuads);
                }
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

    public static class Geometry implements IModelGeometry<Geometry>
    {
        private final TRSRTransformation[] transformations;
        @Nullable
        private BlockModel baseModel;

        public Geometry(@Nullable BlockModel baseModel, TRSRTransformation[] matrices)
        {
            this.baseModel = baseModel;
            this.transformations = matrices;
        }

        @Override
        public Collection<ResourceLocation> getTextureDependencies(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
        {
            List<ResourceLocation> list = new ArrayList<>();
            if (baseModel != null)
            {
                list.addAll(baseModel.getTextures(modelGetter, missingTextureErrors));
            }
            return list;
        }

        @Override
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format, ItemOverrideList overrides)
        {
            TextureAtlasSprite particleSprite = spriteGetter.apply(new ResourceLocation(owner.resolveTexture("particle")));

            IBakedModel rackBakedModel;
            if (baseModel == null)
            {
                IUnbakedModel rackModel = ModelLoaderRegistry.getMissingModel();
                rackBakedModel = rackModel.bake(bakery, spriteGetter, sprite, format);
            }
            else
            {
                rackBakedModel = baseModel.bake(bakery, baseModel, spriteGetter, sprite, format);
            }

            Optional<TRSRTransformation> baseTransform = sprite.getState().apply(Optional.empty());
            if (baseTransform.isPresent())
            {
                TRSRTransformation value = baseTransform.get();
                transformations[0] = value.compose(transformations[0]);
                transformations[1] = value.compose(transformations[1]);
                transformations[2] = value.compose(transformations[2]);
                transformations[3] = value.compose(transformations[3]);
            }

            return new DryingRackBakedModel(bakery, owner.getOwnerModel(), bakery::getUnbakedModel, spriteGetter, format, particleSprite, rackBakedModel, transformations);
        }
    }

    public static class ModelLoader implements IModelLoader<Geometry>
    {
        public static final ModelLoader INSTANCE = new ModelLoader();

        protected ModelLoader() {}

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {
            // nothing to do
        }

        @Override
        public Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
        {
            BlockModel baseModel = null;
            if (modelContents.has("base_model"))
            {
                baseModel = deserializationContext.deserialize(JSONUtils.getJsonObject(modelContents,"base_model"), BlockModel.class);
            }
            TRSRTransformation[] transformations = new TRSRTransformation[4];
            for(int i=0;i<4;i++)
            {
                String key = "transform_" + i;
                if (modelContents.has(key))
                {
                    transformations[i] = deserializationContext.deserialize(modelContents.get(key), TRSRTransformation.class);
                }
            }
            return new Geometry(baseModel, transformations);
        }
    }
}
