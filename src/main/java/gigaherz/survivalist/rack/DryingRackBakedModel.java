package gigaherz.survivalist.rack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.QuadTransformer;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class DryingRackBakedModel implements IDynamicBakedModel
{
    private final TextureAtlasSprite particle;
    private final IBakedModel rackBakedModel;

    private final TransformationMatrix[] itemTransforms;

    private final List<Map<Pair<IBakedModel, TransformationMatrix>, List<BakedQuad>>> caches = Lists.newArrayList(
            Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap()
    );

    private final ItemOverrideList overrides;
    private final boolean isSideLit;

    public DryingRackBakedModel(ModelBakery bakery, IUnbakedModel original, Function<ResourceLocation, IUnbakedModel> modelGetter,
                                Function<RenderMaterial, TextureAtlasSprite> textureGetter,
                                TextureAtlasSprite particle, IBakedModel rackBakedModel, TransformationMatrix[] itemTransforms, boolean isSideLit)
    {
        this.particle = particle;
        this.rackBakedModel = rackBakedModel;
        this.itemTransforms = itemTransforms;
        this.isSideLit = isSideLit;
        this.overrides = new ItemOverrideList(bakery, original, modelGetter, textureGetter, Collections.emptyList());
    }

    private static final Direction[] faces = Streams.concat(Arrays.stream(Direction.values()), Stream.of((Direction) null)).toArray(Direction[]::new);

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        List<BakedQuad> quads = Lists.newArrayList();

        RenderType renderLayer = MinecraftForgeClient.getRenderLayer();
        if (renderLayer == RenderType.getSolid())
        {
            quads.addAll(rackBakedModel.getQuads(state, side, rand));
        }
        else if (renderLayer == RenderType.getCutout() && side == null)
        {
            ItemRenderer renderItem = Minecraft.getInstance().getItemRenderer();
            World world = Minecraft.getInstance().world;

            DryingRackItemsStateData items = extraData.getData(DryingRackTileEntity.CONTAINED_ITEMS_DATA);

            for (int i = 0; i < 4; i++)
            {
                ItemStack stack = items.stacks[i];
                if (stack.isEmpty())
                    continue;

                MatrixStack matrixStack = new MatrixStack();
                matrixStack.push(); // pushMatrix

                TransformationMatrix ct = itemTransforms[i];
                matrixStack.getLast().getMatrix().mul(ct.getMatrix()); // current().getPositionMatrix().multiply(getPositionMatrix)
                matrixStack.getLast().getNormal().mul(ct.getNormalMatrix()); // current().getNormalMatrix().multiply

                IBakedModel model = renderItem.getItemModelWithOverrides(stack, world, null);

                if (stack.getItem() == Items.TRIDENT)
                {
                    model = Minecraft.getInstance().getItemRenderer().getItemModelMesher().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
                }

                model = model.handlePerspective(ItemCameraTransforms.TransformType.FIXED, matrixStack);

                if (!model.isBuiltInRenderer())
                {
                    @SuppressWarnings("unchecked")
                    Map<Pair<IBakedModel, TransformationMatrix>, List<BakedQuad>> cache = caches.get(i);

                    Matrix4f positionTransform = matrixStack.getLast().getMatrix(); // current() // getPositionMatrix
                    TransformationMatrix transformMatrix = new TransformationMatrix(positionTransform);

                    Pair<IBakedModel, TransformationMatrix> pair = Pair.of(model, transformMatrix);
                    List<BakedQuad> cachedQuads = cache.get(pair);
                    if (cachedQuads == null)
                    {

                        cachedQuads = Lists.newArrayList();
                        for (Direction face : faces)
                        {
                            List<BakedQuad> inQuads = model.getQuads(null, face, rand);
                            List<BakedQuad> outQuads = new QuadTransformer(transformMatrix).processMany(inQuads);

                            cachedQuads.addAll(outQuads);
                        }

                        cache.put(pair, cachedQuads);
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
    public boolean func_230044_c_()
    {
        return isSideLit;
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
        private final TransformationMatrix[] transformations;
        @Nullable
        private BlockModel baseModel;

        public Geometry(@Nullable BlockModel baseModel, TransformationMatrix[] matrices)
        {
            this.baseModel = baseModel;
            this.transformations = matrices;
        }


        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> missingTextureErrors)
        {
            List<RenderMaterial> list = new ArrayList<>();
            if (baseModel != null)
            {
                list.addAll(baseModel.getTextures(modelGetter, missingTextureErrors));
            }
            return list;
        }


        @Override
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform sprite, ItemOverrideList overrides, ResourceLocation modelLocation)
        {
            TextureAtlasSprite particleSprite = spriteGetter.apply(owner.resolveTexture("particle"));

            TransformationMatrix[] transformations = Arrays.copyOf(this.transformations, 4);

            IBakedModel rackBakedModel = null;
            if (baseModel != null)
            {
                TransformationMatrix baseTransform = sprite.getRotation();
                rackBakedModel = baseModel.bakeModel(bakery, spriteGetter, new SimpleModelTransform(baseTransform), modelLocation); // bake

                if (!baseTransform.isIdentity())
                {
                    baseTransform = baseTransform.blockCenterToCorner();
                    transformations[0] = baseTransform.compose(transformations[0]);
                    transformations[1] = baseTransform.compose(transformations[1]);
                    transformations[2] = baseTransform.compose(transformations[2]);
                    transformations[3] = baseTransform.compose(transformations[3]);
                }
            }

            return new DryingRackBakedModel(bakery, owner.getOwnerModel(), bakery::getUnbakedModel, spriteGetter, particleSprite, rackBakedModel, transformations, owner.isSideLit());
        }
    }

    public static class ModelLoader implements IModelLoader<Geometry>
    {
        public static final ModelLoader INSTANCE = new ModelLoader();

        protected ModelLoader()
        {
        }

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
                baseModel = deserializationContext.deserialize(JSONUtils.getJsonObject(modelContents, "base_model"), BlockModel.class);
            }
            TransformationMatrix[] transformations = new TransformationMatrix[4];
            for (int i = 0; i < 4; i++)
            {
                String key = "transform_" + i;
                if (modelContents.has(key))
                {
                    transformations[i] = deserializationContext.deserialize(modelContents.get(key), TransformationMatrix.class);
                }
            }
            return new Geometry(baseModel, transformations);
        }
    }
}
