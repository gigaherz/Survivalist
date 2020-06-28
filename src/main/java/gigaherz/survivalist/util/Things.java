package gigaherz.survivalist.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Things
{
    private static final Map<String, Shape<?>> shapes = Maps.newHashMap();
    private static final Map<String, Material> materials = Maps.newHashMap();
    private static final Table<Shape<?>, Material, Thing<?>> things = TreeBasedTable.create();

    @SuppressWarnings("unchecked")
    public static <S extends IForgeRegistryEntry<S>> Shape<S> getShape(String name, IForgeRegistry<? extends S> registry)
    {
        if (shapes.containsKey(name))
        {
            Shape<?> s = shapes.get(name);
            if (s.registry != registry)
                throw new IllegalStateException("Attempted to request a shape with the same name but a different registry! " + name + " " + s.registry.getRegistryName() + " != " + registry.getRegistryName());
            return (Shape<S>) s;
        }
        else
        {
            Shape<S> s = new Shape<>(name, registry);
            shapes.put(name, s);
            return s;
        }
    }

    public static Material getMaterial(String name)
    {
        return materials.computeIfAbsent(name, Material::new);
    }

    public static <S extends IForgeRegistryEntry<S>> Thing<S> propose(Shape<S> shape, Material material, Supplier<S> factory)
    {
        return get(shape, material).propose(factory);
    }

    @SuppressWarnings("unchecked")
    public static <S extends IForgeRegistryEntry<S>> Thing<S> get(Shape<S> shape, Material material)
    {
        if (things.contains(shape,material))
        {
            return (Thing<S>) things.get(shape, material);
        }
        Thing<S> thing = new Thing<>(shape, material);
        things.put(shape, material, thing);
        return thing;
    }

    public static class Thing<S extends IForgeRegistryEntry<S>>
    {
        public final List<Supplier<? extends S>> factories = Lists.newArrayList();
        public final Shape<S> shape;
        public final Material material;

        public Thing(Shape<S> shape, Material material)
        {
            this.shape = shape;
            this.material = material;
        }

        public Thing<S> propose(Supplier<S> factory)
        {
            factories.add(factory);
            return this;
        }
    }

    public static class Shape<S extends IForgeRegistryEntry<S>> implements Comparable<Shape>
    {
        private final String name;
        private final IForgeRegistry<? extends S> registry;

        public Shape(String name, IForgeRegistry<? extends S> registry)
        {
            this.name = name;
            this.registry = registry;
        }

        @Override
        public String toString()
        {
            return String.format("{Shape:%s:%s}", name, registry.getRegistryName());
        }

        @Override
        public int compareTo(Shape o)
        {
            return name.compareTo(o.name);
        }
    }

    public static class Material implements Comparable<Material>
    {
        private final String name;

        public Material(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return String.format("{Material:%s}", name);
        }

        @Override
        public int compareTo(Material o)
        {
            return name.compareTo(o.name);
        }
    }
}
