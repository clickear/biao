package com.biao.netty.handler;

import com.biao.constant.WebsocketConstant;
import com.biao.netty.NettyWebsocketHandler;
import com.biao.netty.utils.UrlUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  ""(Myth)
 */
@Component
public class UserOrderWebsocketHandler extends AbstractHandshakeHandler implements NettyWebsocketHandler {

    private static final Map<String, Channel> USER_ORDER_MAP = new ConcurrentHashMap<>();

    @Override
    public String path() {
        return WebsocketConstant.WS_USER_ORDER;
    }

    @Override
    public void remove(Channel channel) {
        final String key = channel.attr(WebsocketConstant.USER_ORDER_KEY).get();
        USER_ORDER_MAP.remove(key);
    }

    @Override
    public ChannelFuture handshake(ChannelHandlerContext ctx, FullHttpRequest req) {
        final ChannelFuture handshake = super.handshake(ctx, req, path());
        if (Objects.nonNull(handshake)) {
            final String param = UrlUtils.urlParam(req.uri());
            ctx.channel().attr(WebsocketConstant.USER_ORDER_KEY).set(param);
            USER_ORDER_MAP.put(param, ctx.channel());
        }
        return handshake;
    }

    @Override
    public void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //ctx.writeAndFlush(new TextWebSocketFrame("userOrder"));
    }

    public void sendUserOrder(String userId, String data) {
        if (StringUtils.isNoneBlank(data)) {
            Optional.ofNullable(USER_ORDER_MAP.get(userId)).ifPresent(channel ->
                    channel.writeAndFlush(new TextWebSocketFrame(data)));
        }
    }


}
