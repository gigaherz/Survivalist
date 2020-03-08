package gigaherz.survivalist.util;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.IVertexBuilder;
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
    private static final int BUFFER_SIZE = 2*1024*1024;

    public static MultiVBORenderer of(Consumer<IRenderTypeBuffer> vertexProducer)
    {
        final Map<RenderType, BufferBuilder> builders = Maps.newHashMap();

        vertexProducer.accept(rt -> builders.computeIfAbsent(rt, (_rt) -> {
            BufferBuilder builder = new BufferBuilder(BUFFER_SIZE);
            builder.begin(_rt.getGlMode(), _rt.getVertexFormat());
            return builder;
        }));

        Map<RenderType, VertexBuffer> buffers = Maps.transformEntries(builders, (rt, builder) -> {
            Objects.requireNonNull(rt);
            Objects.requireNonNull(builder);

            builder.finishDrawing();

            VertexFormat fmt = rt.getVertexFormat();
            VertexBuffer vbo = new VertexBuffer(fmt);

            // 1.14: vbo.bufferData(BUILDER.getByteBuffer());
            vbo.upload(builder);

            return vbo;
        });
        return new MultiVBORenderer(buffers);
    }

    private final Map<RenderType, VertexBuffer> buffers;

    public MultiVBORenderer(Map<RenderType, VertexBuffer> buffers)
    {
        this.buffers = buffers;
    }

    public void render(Matrix4f matrix)
    {
        // 1.14: vbo.drawArrays(glMode);
        buffers.entrySet().forEach(kv ->  {
            RenderType rt = kv.getKey();
            VertexBuffer vbo = kv.getValue();
            rt.enable();
            vbo.draw(matrix, rt.getGlMode());
            rt.disable();
        });
    }

    public void close()
    {
        //1.14: vbo.deleteGlBuffers();
        buffers.values().forEach(VertexBuffer::close);
    }
}
