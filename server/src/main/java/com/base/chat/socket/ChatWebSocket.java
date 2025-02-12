package com.base.chat.socket;

import com.base.dao.MessageDAO;
import com.base.dao.UserDAO;
import com.base.db.DataBaseConnection;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket endpoint for handling chat messages and notifications.
 */
@ServerEndpoint("/{userId}")
public class ChatWebSocket {

    // Map to store active user sessions
    private static final Map<String, Session> activeUsers = new ConcurrentHashMap<>();

    /**
     * Sends a friend request notification to the specified user.
     *
     * @param userId the ID of the user sending the friend request
     * @param friendId the ID of the user receiving the friend request
     * @param name the name of the user sending the friend request
     * @throws IOException if an I/O error occurs
     */
    public static void sendFriendRequestNotification(String userId, String friendId, String name) throws IOException {
        if(activeUsers.containsKey(friendId)){
            activeUsers.get(friendId).getBasicRemote().sendText("friend request:" + userId + ":" + name);
        }
    }

    /**
     * Sends an add contact notification to the specified user.
     *
     * @param userId the ID of the user adding the contact
     * @param friendId the ID of the user being added as a contact
     * @param name the name of the user adding the contact
     * @throws IOException if an I/O error occurs
     */
    public static void addContact(String userId, String friendId, String name) throws IOException {
        if(activeUsers.containsKey(friendId)){
            activeUsers.get(friendId).getBasicRemote().sendText("add contact:" + userId + ":" + name);
        }
    }

    /**
     * Called when a new WebSocket connection is opened.
     *
     * @param userId the ID of the user connecting
     * @param session the WebSocket session
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session){
        activeUsers.put(userId, session);
        try {
            UserDAO userDAO = new UserDAO(DataBaseConnection.getConnection());
            userDAO.setOnlineStatus(userId, true);
            MessageDAO messageDAO = new MessageDAO(DataBaseConnection.getConnection());
            messageDAO.setMessageDelivered(userId);
        } catch (Exception e) {
            System.out.println("onOpen exception : " + e.getMessage());
        }
        System.out.println("user " + userId + " connected...");
    }

    /**
     * Called when a message is received from a client.
     *
     * @param session the WebSocket session
     * @param message the message received
     * @throws IOException if an I/O error occurs
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        String[] msg = message.split(":");
        String senderId = msg[0];
        String receiverId = msg[1];
        String text = msg[2];
        try {
            MessageDAO messageDAO = new MessageDAO(DataBaseConnection.getConnection());
            if(activeUsers.containsKey(receiverId)){
                messageDAO.storeMessage(senderId, receiverId, text, "delivered");
                Session receiverSession = activeUsers.get(receiverId);
                receiverSession.getBasicRemote().sendText(message);
            } else {
                messageDAO.storeMessage(senderId, receiverId, text, "sent");
                System.out.println("receiver is offline...");
            }
        } catch (Exception e) {
            System.out.println("onMessage exception : " + e.getMessage());
        }
    }

    /**
     * Called when a WebSocket connection is closed.
     *
     * @param userId the ID of the user disconnecting
     * @param session the WebSocket session
     */
    @OnClose
    public void onClose(@PathParam("userId") String userId, Session session){
        activeUsers.remove(userId, session);
        try {
            UserDAO userDAO = new UserDAO(DataBaseConnection.getConnection());
            userDAO.setOnlineStatus(userId, false);
        } catch (Exception e) {
            System.out.println("onClose exception : " + e.getMessage());
        }
        System.out.println("user " + userId + " disconnected...");
    }

    /**
     * Called when an error occurs on the WebSocket connection.
     *
     * @param session the WebSocket session
     * @param throwable the error that occurred
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

}