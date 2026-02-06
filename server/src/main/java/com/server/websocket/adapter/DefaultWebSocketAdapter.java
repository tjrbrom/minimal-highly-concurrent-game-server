package com.server.websocket.adapter;

import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class DefaultWebSocketAdapter implements IWebSocketAdapter {

    @Override
    public Future<Void> init() {
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> close() {
        return Future.succeededFuture();
    }

    @Override
    public Future<Integer> serverCount() {
        return Future.succeededFuture(0);
    }

    @Override
    public Future<Void> addAll(String id, Set<String> rooms) {
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> del(String id, String room) {
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> deleteRoom(String room) {
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> delAll(String id) {
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> delStartsWith(String id, String str) {
        return Future.succeededFuture();
    }

    @Override
    public void send(String event, String uid, Object packet) {

    }

    @Override
    public void sendRegisteredEvent(String event, String uid, Object packet) {

    }

    @Override
    public void broadcast(String event, Object packet, BroadcastOptions opts) {

    }

    @Override
    public void broadcastWithAck(Object packet, BroadcastOptions opts, Handler<Integer> clientCountCallback, Handler<List<Object>> ack) {

    }

    @Override
    public Future<Set<String>> sockets(Set<String> rooms) {
        return Future.succeededFuture(Collections.emptySet());
    }

    @Override
    public void addSockets(BroadcastOptions opts, Collection<String> rooms) {

    }

    @Override
    public void delSockets(BroadcastOptions opts, Collection<String> rooms) {

    }

    @Override
    public void disconnectSockets(BroadcastOptions opts, short statusCode) {

    }

    @Override
    public void disconnectSockets(BroadcastOptions opts, short statusCode, String reason) {

    }

    @Override
    public void disconnectSockets(BroadcastOptions opts) {

    }

    @Override
    public void serverSideEmit(List<Object> packet) {

    }

}
