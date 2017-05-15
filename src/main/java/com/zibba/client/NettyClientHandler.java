package com.zibba.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by liuhui on 2017/5/10.
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ByteBuf firstMessage;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //建立新的链接
        logger.info("client channel active");
        // Send the message to Server
        logger.info("client send req...");
        byte[] data = {0x55, 0x08, 0x00, 0x00, 0x00, 0x01, 0x09, (byte) 0xBB};
        firstMessage = Unpooled.buffer(data.length);
        firstMessage.writeBytes(data);
        ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        logger.info("client channel read msg:{}", req);
//        ctx.close();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        logger.error("client caught exception", cause);
        ctx.close();
    }
}
