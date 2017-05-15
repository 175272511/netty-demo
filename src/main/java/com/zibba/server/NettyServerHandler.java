package com.zibba.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liuhui on 2017/5/10.
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        byte[] result = new byte[in.readableBytes()];
        in.readBytes(result);
        in.release();
        logger.info("接收到数据:{}", result);
//        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
        //返回数据
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(result);
        ctx.write(buffer);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info("server channel read complete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        logger.error("server caught exception", cause);
        ctx.close();
    }

}
