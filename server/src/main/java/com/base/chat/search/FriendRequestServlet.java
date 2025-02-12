package com.base.chat.search;

import com.base.chat.socket.ChatWebSocket;
import com.base.dao.FriendsDAO;
import com.base.dao.UserDAO;
import com.base.db.DataBaseConnection;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/friend-request")
public class FriendRequestServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (method.equalsIgnoreCase("POST")) {
            doPost(req, resp);
        } else if (method.equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            resp.getWriter().write("{\"message\":\"Method not allowed\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuilder data = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        }

        JSONObject requestJson = new JSONObject(data.toString());
        String userId = requestJson.getString("userId");
        String friendId = requestJson.getString("friendId");

        try {
            FriendsDAO friendsDAO = new FriendsDAO(DataBaseConnection.getConnection());
            friendsDAO.addFriendRequest(userId, friendId);
            ChatWebSocket.sendFriendRequestNotification(userId, friendId, new UserDAO(DataBaseConnection.getConnection()).getUserById(userId).get().getUserName());
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\":\"Request send failed\"}");
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder data = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        }

        JSONObject requestJson = new JSONObject(data.toString());
        String userId = requestJson.getString("userId");
        String friendId = requestJson.getString("friendId");
        String action = requestJson.getString("action");

        JSONObject responseJson = new JSONObject();

        if (action.equals("accepted") || action.equals("rejected")) {
            try {
                FriendsDAO friendsDAO = new FriendsDAO(DataBaseConnection.getConnection());
                friendsDAO.updateFriendRequest(userId, friendId, action);
                resp.setStatus(HttpServletResponse.SC_OK);
                if (action.equals("accepted")) {
                    ChatWebSocket.addContact(userId, friendId, new UserDAO(DataBaseConnection.getConnection()).getUserById(userId).get().getUserName());
                    responseJson.put("message", "Request accepted successfully");
                } else {
                    responseJson.put("message", "Request rejected successfully");
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseJson.put("message", "Request failed");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseJson.put("message", "Invalid action");
        }

        resp.getWriter().write(responseJson.toString());
    }
}
