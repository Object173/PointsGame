package com.object173.pointsgame.events;

import com.object173.pointsgame.controller.Player;
import com.object173.pointsgame.messages.Message;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Ярослав on 12.01.2017.
 */

//адаптер сетевых событий, чтобы не обязательно было все методы реализовывать
public class NetworkEventAdapter implements NetworkEventListener {
    @Override
    public void removePlayer(Player player) {}
    @Override
    public void startServer(String name, int port){}
    @Override
    public void stopServer() {}
    @Override
    public void setServerEnabled(boolean f) {}
    @Override
    public void addPlayer(String name) {}
    @Override
    public void connectPlayer(String name, String ip, int port) {}
    @Override
    public void disconnectPlayer() {}
    @Override
    public void disconnectPlayer(String name) {}
    @Override
    public void setConnectPlayerStatus(boolean f) {}
    @Override
    public void signalStartGame() {}
    @Override
    public void startGame(int width, int height, String[] players) {}
    @Override
    public void startGame(String[] players) {}
    @Override
    public void messageToServer(Message message) {}
    @Override
    public void updateClientField(ArrayList<Point> updatePoints, int player, int[] score) {}
}
