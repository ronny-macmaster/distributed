package com.bloomberg.bach.agent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import org.jolokia.jvmagent.JolokiaServer;
import org.jolokia.jvmagent.JolokiaServerConfig;

/**
 * @author Ronald Macmaster
 * Jolokia Agent Server for reporting bach metrics.
 */
public class BachMetricsServer {
	
	private String host;
	private Integer port;
	
	private JolokiaServer server = null;
	
	/**
	 * Constructs a new BachMetricsServer that reports JSON metrics on a given host and port.
	 * defaults to localhost and an arbitrary port for bad parameters.
	 */
	public BachMetricsServer(String host, Integer port) {
		this.host = host != null ? host : "localhost";
		this.port = port != null ? port : getFreePort();
	}
	
	/**
	 * Configure and start the Jolokia server. <br>
	 * @throws IOException 
	 */
	public void start() throws IOException {
		// configure and start Jolokia server.
		Map<String, String> configMap = new HashMap<String, String>();
		configMap.put("port", port.toString());
		configMap.put("host", host);
		
		System.out.format("I> Now hosting jolokia json metrics on port: %s.%n", configMap.get("port"));
		JolokiaServerConfig config = new JolokiaServerConfig(configMap);
		this.server = new JolokiaServer(config, true);
		this.server.start();
	}
	
	/**
	 * Stops the Jolokia server. <br>
	 */
	public void stop() throws IOException {
		this.server.stop();
	}
	
	private synchronized static Integer getFreePort() throws IllegalStateException {
		final int MIN_PORT = 7777, MAX_PORT = 8888;
		
		for (int port = MIN_PORT; port < MAX_PORT; port++) {
			try { // attempt to bind the port.
				ServerSocket socket = new ServerSocket();
				socket.bind(new InetSocketAddress("localhost", port));
				socket.close();
				return port;
			} catch (IOException e) {
				System.err.format("Cannot bind to port %d, trying next port...%n", port);
			}
		}
		
		throw new IllegalStateException(String.format("I> No available port in range: <%d, %d>", MIN_PORT, MAX_PORT));
	}
	
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * @return the port
	 */
	public Integer getPort() {
		return port;
	}
	
}
