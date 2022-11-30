package com.example.ssh.server.demo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Builder
@Getter
@ToString
public class Msg implements Serializable {

	private String sshSid;
	private String wsSid;
	private String command;
}
