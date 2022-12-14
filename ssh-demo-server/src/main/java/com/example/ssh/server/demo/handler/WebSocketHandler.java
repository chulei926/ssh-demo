package com.example.ssh.server.demo.handler;

import com.alibaba.fastjson.JSONObject;
import com.example.ssh.server.demo.common.AppCtxWrapper;
import com.example.ssh.server.demo.common.BizException;
import com.example.ssh.server.demo.model.Msg;
import com.example.ssh.server.demo.service.CmdExecService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {

	public static final Map<String, WebSocketSession> sessionMap = Maps.newConcurrentMap();


	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		if (StringUtils.equals(payload, "hb")) {
			reply2User(session.getId(), "hb_ok");
			return;
		}
		log.info("收到信息: {}", payload);

		JSONObject obj = JSONObject.parseObject(payload);
		Msg msg = Msg.builder()
				.sshSid(obj.getString("sid"))
				.wsSid(session.getId())
				.command(obj.getString("command"))
				.build();
		CmdExecService cmdExecService = AppCtxWrapper.getBean(CmdExecService.class);
		cmdExecService.send(msg);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("Connected ... {}", session.getId());
		sessionMap.put(session.getId(), session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		if (session.isOpen()) {
			session.close();
		}
		sessionMap.remove(session.getId());
		log.info("Session {} closed because of {}", session.getId(), status.getReason());
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
		log.error("error occured at sender {}", session, throwable);
	}

	public void reply2User(String wsSid, String message) {
		try {
			if (!sessionMap.containsKey(wsSid)) {
				throw new BizException("没有找到会话信息");
			}
			TextMessage msg = new TextMessage(message);
			WebSocketSession session = sessionMap.get(wsSid);
			if (session.isOpen()) {
				session.sendMessage(msg);
				log.info("发送定向消息成功...{}", msg.getPayload());
			}
		} catch (Exception e) {
			log.error("发送定向消息异常！", e);
		}
	}


}
