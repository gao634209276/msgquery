package com.sinova.monitor.elasticsearch;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * spring管理TransportClient对象
 */
@Configuration
public class ESConfig {

	@Value("#{common['es.cluster.nodes']}")
	private String nodes;

	@Bean(name = "client")
	public TransportClient getClient() {
		Settings settings = Settings.settingsBuilder()
				.put("client.transport.sniff", true)
				//.put("cluster.name",util.getProperty("es.cluster.name"))
				.put("client.transport.ignore_cluster_name", true)
				.build();
		String[] ips = nodes.split(",");
		TransportAddress[] addressArray = new InetSocketTransportAddress[ips.length];
		for (int i = 0; i < ips.length; i++) {
			try {
				addressArray[i] = new InetSocketTransportAddress(InetAddress.getByName(ips[i]), 9300);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return TransportClient.builder()
				.settings(settings).build()
				.addTransportAddresses(addressArray);
	}
}
