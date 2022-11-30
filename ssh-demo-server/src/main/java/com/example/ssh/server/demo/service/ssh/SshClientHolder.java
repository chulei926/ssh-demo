package com.example.ssh.server.demo.service.ssh;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.ClientBuilder;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.common.cipher.BuiltinCiphers;
import org.apache.sshd.common.kex.BuiltinDHFactories;
import org.apache.sshd.common.kex.KeyExchangeFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SSH客户端持有者.
 * <p>
 * 通过单例模式，保证全局只能有一个 SshClient。
 * </p>
 *
 * @author chul 2022/3/9.
 */
@Slf4j
public class SshClientHolder {

	private static volatile SshClientHolder instance;
	/**
	 * 全局只能有一个Client。
	 */
	private static SshClient client;

	static {
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new Thread(SshClientHolder::close));
	}

	private SshClientHolder() {
		try {
			client = SshClient.setUpDefaultClient();
			List<KeyExchangeFactory> keyExchangeFactories = new ArrayList<>();
			for (BuiltinDHFactories dhFactories : BuiltinDHFactories.values()) {
				keyExchangeFactories.add(ClientBuilder.DH2KEX.apply(dhFactories));
			}
			client.setKeyExchangeFactories(keyExchangeFactories);
			List<String> cipherFactoriesNames = Arrays.stream(BuiltinCiphers.values())
					.map(BuiltinCiphers::getName).collect(Collectors.toList());
			client.setCipherFactoriesNames(cipherFactoriesNames);
			client.start();
			log.info("SSH client started ......");
		} catch (Exception e) {
			close();
			throw new RuntimeException("SSH Client Create Error ！", e);
		}
	}

	public static SshClientHolder getInstance() {
		if (instance == null) {
			synchronized (SshClientHolder.class) {
				if (instance == null) {
					instance = new SshClientHolder();
				}
			}
		}
		return instance;
	}

	public static void close() {
		try {
			if (null != client) {
				client.stop();
				client.close();
			}
			log.info("SSH Client closed ......");
		} catch (Exception e) {
			log.error("SSH关闭客户端异常！", e);
		}
	}

	public SshClient getSshClient() {
		return client;
	}


}
