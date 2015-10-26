package org.jupiter.transport.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.udt.nio.NioUdtProvider;
import org.jupiter.rpc.UnresolvedAddress;
import org.jupiter.transport.JConnection;
import org.jupiter.transport.JConfig;

import java.util.concurrent.ThreadFactory;

/**
 * jupiter
 * org.jupiter.transport.netty
 *
 * @author jiachun.fjc
 */
public abstract class NettyUdtConnector extends NettyConnector {

    private final NettyConfig.NettyUDTConfigGroup.ChildConfig childConfig = new NettyConfig.NettyUDTConfigGroup.ChildConfig();

    public NettyUdtConnector() {
        super(Protocol.UDT);
        init();
    }

    public NettyUdtConnector(int nWorkers) {
        super(Protocol.UDT, nWorkers);
        init();
    }

    @Override
    protected void setOptions() {
        super.setOptions();

        Bootstrap boot = bootstrap();

        NettyConfig.NettyUDTConfigGroup.ChildConfig child = childConfig;

        // child options
        boot.option(ChannelOption.SO_REUSEADDR, child.isReuseAddress());
        if (child.getRcvBuf() > 0) {
            boot.option(ChannelOption.SO_RCVBUF, child.getRcvBuf());
        }
        if (child.getSndBuf() > 0) {
            boot.option(ChannelOption.SO_SNDBUF, child.getSndBuf());
        }
        if (child.getLinger() > 0) {
            boot.option(ChannelOption.SO_LINGER, child.getLinger());
        }
        if (child.getConnectTimeoutMillis() > 0) {
            boot.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, child.getConnectTimeoutMillis());
        }
    }

    @Override
    public JConnection connect(UnresolvedAddress remoteAddress) {
        return connect(remoteAddress, false);
    }

    @Override
    public JConfig config() {
        return childConfig;
    }

    @Override
    public void setIoRatio(int workerIoRatio) {
        ((NioEventLoopGroup) worker()).setIoRatio(workerIoRatio);
    }

    @Override
    protected EventLoopGroup initEventLoopGroup(int nThreads, ThreadFactory tFactory) {
        return new NioEventLoopGroup(nThreads, tFactory, NioUdtProvider.BYTE_PROVIDER);
    }
}