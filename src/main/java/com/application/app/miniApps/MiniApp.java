package com.application.app.miniApps;

import java.util.Map;

import com.application.app.entities.ObjectEntity;

import reactor.core.publisher.Flux;

public interface MiniApp{
	Flux<Object> invokeCommand(String command, String userId, ObjectEntity object, Map<String,Object> commandAttributes);
}
