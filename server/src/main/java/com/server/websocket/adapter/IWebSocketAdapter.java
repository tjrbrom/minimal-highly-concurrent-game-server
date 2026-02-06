package com.server.websocket.adapter;

import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface IWebSocketAdapter {

    /**
     * To be overridden
     */
    Future<Void> init();

    /**
     * To be overridden
     */
    Future<Void> close();

    /**
     * Returns the number of WebSocket servers in the cluster
     */
    Future<Integer> serverCount();


    /**
     * Adds a socket to a list of room.
     *
     * @param id    the socket id
     * @param rooms a set of rooms
     */
    Future<Void> addAll(String id, Set<String> rooms);

    /**
     * Removes a socket from a room.
     *
     * @param id   the socket id
     * @param room the room name
     */
    Future<Void> del(String id, String room);

    /**
     * Deletes the entire room and any socket id connected.
     *
     * @param room room name
     */
    Future<Void> deleteRoom(String room);

    /**
     * Removes a socket from all rooms it's joined.
     *
     * @param id the socket id
     */
    Future<Void> delAll(String id);

    /**
     * Removes a socket from all rooms it's joined which start with parameter str
     *
     * @param id  the socket id
     * @param str the string to check if a room starts with this
     */
    Future<Void> delStartsWith(String id, String str);

    void send(String event, String uid, Object packet);

    void sendRegisteredEvent(String event, String uid, Object packet);

    /**
     * Broadcasts a packet.
     * <p>
     * Options:
     * - `flags` {BroadcastFlags} flags for this packet
     * - `except` {Set<String>} sids that should be excluded
     * - `rooms` {Set<String>} list of rooms to broadcast to
     *
     * @param event
     * @param packet the packet object
     * @param opts   the options
     */
    void broadcast(String event, Object packet, BroadcastOptions opts);

    /**
     * Broadcasts a packet and expects multiple acknowledgements.
     * <p>
     * Options:
     * - `flags` {Object} flags for this packet
     * - `except` {Array} sids that should be excluded
     * - `rooms` {Array} list of rooms to broadcast to
     *
     * @param {Object}            packet   the packet object
     * @param {Object}            opts     the options
     * @param clientCountCallback - the number of clients that received the packet
     * @param ack                 - the callback that will be called for each client response
     */
    void broadcastWithAck(Object packet, BroadcastOptions opts, Handler<Integer> clientCountCallback, Handler<List<Object>> ack);

    /**
     * Gets a list of sockets by sid.
     *
     * @param {Set<Room>} rooms   the explicit set of rooms to check.
     */
    Future<Set<String>> sockets(Set<String> rooms);

    /**
     * Makes the matching socket instances join the specified rooms
     *
     * @param opts  - the filters to apply
     * @param rooms - the rooms to join
     */
    void addSockets(BroadcastOptions opts, Collection<String> rooms);

    /**
     * Makes the matching socket instances leave the specified rooms
     *
     * @param opts  - the filters to apply
     * @param rooms - the rooms to leave
     */
    void delSockets(BroadcastOptions opts, Collection<String> rooms);

    /**
     * Makes the matching socket instances disconnect
     *
     * @param opts       - the filters to apply
     * @param statusCode status code to send to the client
     *
     */
    void disconnectSockets(BroadcastOptions opts, short statusCode);

    /**
     * Makes the matching socket instances disconnect
     *
     * @param opts       - the filters to apply
     * @param statusCode status code to send to the client
     * @param reason     reason to send to the client
     *
     */
    void disconnectSockets(BroadcastOptions opts, short statusCode, String reason);

    /**
     * Makes the matching socket instances disconnect
     *
     * @param opts - the filters to apply
     *
     */
    void disconnectSockets(BroadcastOptions opts);

    /**
     * Send a packet to the other Socket.IO servers in the cluster
     *
     * @param packet - an array of arguments, which may include an acknowledgement callback at the end
     */
    void serverSideEmit(List<Object> packet);
}
