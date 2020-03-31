package gigaherz.survivalist.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;

import java.io.Closeable;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class MultiVBORenderer implements Closeable
{
    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    public static MultiVBORenderer of(Consumer<IRenderTypeBuffer> vertexProducer)
    {
        final Map<RenderType, BufferBuilder> builders = Maps.newHashMap();

        vertexProducer.accept(rt -> builders.computeIfAbsent(rt, (_rt) -> {
            BufferBuilder builder = new BufferBuilder(BUFFER_SIZE);
            builder.begin(_rt.getGlMode(), _rt.getVertexFormat());
            return builder;
        }));

        ImmutableMap.Builder<RenderType, BufferBuilder.State> sortCaches = ImmutableMap.builder();
        ImmutableMap.Builder<RenderType, VertexBuffer> buffers = ImmutableMap.builder();

        builders.forEach((rt, builder) -> {
            Objects.requireNonNull(rt);
            Objects.requireNonNull(builder);

            builder.finishDrawing();

            sortCaches.put(rt, builder.getVertexState());

            VertexFormat fmt = rt.getVertexFormat();
            VertexBuffer vbo = new VertexBuffer(fmt);

            vbo.upload(builder);

            buffers.put(rt, vbo);
        });
        return new MultiVBORenderer(buffers.build(), sortCaches.build());
    }

    private final ImmutableMap<RenderType, VertexBuffer> buffers;
    private final ImmutableMap<RenderType, BufferBuilder.State> sortCaches;

    protected MultiVBORenderer(ImmutableMap<RenderType, VertexBuffer> buffers, ImmutableMap<RenderType, BufferBuilder.State> sortCaches)
    {
        this.buffers = buffers;
        this.sortCaches = sortCaches;
    }

    public void sort(float x, float y, float z)
    {
        for (Map.Entry<RenderType, BufferBuilder.State> kv : sortCaches.entrySet())
        {
            RenderType rt = kv.getKey();
            BufferBuilder.State state = kv.getValue();
            BufferBuilder builder = new BufferBuilder(BUFFER_SIZE);
            builder.setVertexState(state);
            builder.sortVertexData(x, y, z);

            VertexBuffer vbo = buffers.get(rt);
            vbo.upload(builder);
        }
    }

    public void render(Matrix4f matrix)
    {
        buffers.entrySet().forEach(kv -> {
            RenderType rt = kv.getKey();
            VertexBuffer vbo = kv.getValue();
            VertexFormat fmt = rt.getVertexFormat();

            rt.enable();
            vbo.bindBuffer();
            fmt.setupBufferState(0L);
            vbo.draw(matrix, rt.getGlMode());
            VertexBuffer.unbindBuffer();
            fmt.clearBufferState();
            rt.disable();
        });
    }

    public void close()
    {
        buffers.values().forEach(VertexBuffer::close);
    }
}
