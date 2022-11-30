package com.example.ssh.server.demo.service;

import com.example.ssh.server.demo.common.AppCtxWrapper;
import com.example.ssh.server.demo.handler.WebSocketHandler;
import com.example.ssh.server.demo.model.Msg;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service("cmdExecService")
public class CmdExecService implements IObserver {

	@Resource
	private SshService sshService;

	@Subscribe
	public void handleEvent(Msg msg) {
		log.info("获取到EventBus:{}", msg);
		String res = sshService.exec(msg.getSshSid(), msg.getCommand());
		WebSocketHandler socketHandler = AppCtxWrapper.getBean(WebSocketHandler.class);
		socketHandler.reply2User(msg.getWsSid(), res);
	}

	public void send(Msg msg) {
		EventBus eventBus = AppCtxWrapper.getBean(EventBus.class);
		eventBus.post(msg);
		log.info("消息已通过EventBus发送：{}", msg);
	}
}
