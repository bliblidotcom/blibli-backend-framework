package com.blibli.oss.backend.apiclient.customizer;

import reactor.netty.tcp.TcpClient;

public interface ApiClientTcpClientCustomizer {

  TcpClient customize(TcpClient tcpClient);
}
