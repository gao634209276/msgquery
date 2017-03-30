package com.sinova.monitor.service;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * spring管理TransportClient对象
 * 启动容器之加载1次
 */
@Configuration
public class ESConfig {

	/*@Value("#{common}")
	private Properties common;*/

	@Value("#{common['es.cluster.nodes']}")
	private String nodes;


	@Bean(name = "client")
	public TransportClient getClient() {
		Settings settings = Settings.settingsBuilder()
				.put("client.transport.sniff", true)
				//.put("cluster.name",util.getProperty("es.cluster.name"))
				.put("client.transport.ignore_cluster_name", true)
				.build();
		TransportClient client = null;
		String[] ips = nodes.split(",");
		InetSocketTransportAddress[] addressArray = new InetSocketTransportAddress[ips.length];
		for (int i = 0; i < ips.length; i++) {
			try {
				addressArray[i] = new InetSocketTransportAddress(InetAddress.getByName(ips[i]), 9300);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		client = TransportClient.builder()
				.settings(settings).build()
				.addTransportAddresses(addressArray);
		return client;
	}
}
