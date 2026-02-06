package com.server.websocket.adapter;

import lombok.Builder;

import java.util.Set;

@Builder
public class BroadcastOptions {
    Set<String> rooms;
    Set<String> exceptRooms;
    Set<String> exceptSids;
}
