package com.example.ssh.server.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.example.ssh.server.demo.common.BizException;
import com.example.ssh.server.demo.service.ssh.SshAuthParam;
import com.example.ssh.server.demo.service.ssh.SshChannelHolder;
import com.example.ssh.server.demo.service.ssh.SshSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("sshService")
public class SshService {

	public JSONObject auth(SshAuthParam authParam) {
		log.info(">>> auth: {}", authParam);
		String sid = SshSessionHolder.getInstance().createSession(authParam);
		return new JSONObject().fluentPut("sid", sid);
	}

	public String exec(String sid, String command) {
		ClientChannel channel = SshChannelHolder.getInstance().getChannel(sid);
		String res;
		try {
			res = exec(channel, command);
		} catch (Exception e) {
			throw new BizException("命令执行出错", e);
		}
		return res;
	}

	private String exec(ClientChannel channel, String command) throws Exception {
		StringBuffer result = new StringBuffer();
		OutputStream stdIn;
		InputStream stdOut, stdErr;
		// 切换到输入命令模式，执行命令
		stdIn = channel.getInvertedIn();
		stdIn.write((command).getBytes());
		stdIn.flush();
		channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(1));
		// 获取命令执行结果
		byte[] resBytes, errBytes;
		// 切换到输出模式，获取命令执行的结果
		stdOut = channel.getInvertedOut();
		resBytes = readInputStream(stdOut);
		result.append(new String(resBytes, StandardCharsets.UTF_8));
		stdErr = channel.getInvertedErr();
		errBytes = readInputStream(stdErr);
		result.append(new String(errBytes, StandardCharsets.UTF_8));
		return StringUtils.removeStart(result.toString(), command.replaceAll("\n$", ""));
	}

	private byte[] readInputStream(InputStream inputStream) throws IOException {
		int available = inputStream.available();
		int readCount = 0;
		byte[] res = new byte[available];
		while (readCount < available) {
			readCount += inputStream.read(res, readCount, available - readCount);
		}
		return res;
	}

}
