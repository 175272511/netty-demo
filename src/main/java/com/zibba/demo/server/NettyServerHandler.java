package com.zibba.demo.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liuhui on 2017/5/10.
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

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

        //测试发送数据
        ChannelId channelId = ctx.channel().id();
        CHANNEL_GROUP.add(ctx.channel());
        final Channel channel = CHANNEL_GROUP.find(channelId);
        new Thread(){
            public void run(){
                try {
                    System.out.println("准备发送数据");
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ByteBuf buffer = Unpooled.buffer();
                buffer.writeBytes("测试发送".getBytes());
                channel.writeAndFlush(buffer);
                System.out.println("数据已发送");
            }
        }.start();
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
