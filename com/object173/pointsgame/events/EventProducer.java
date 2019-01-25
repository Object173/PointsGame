package com.object173.pointsgame.events;

import com.object173.pointsgame.controller.Player;
import com.object173.pointsgame.messages.Message;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Ярослав on 02.10.2016.
 */
//класс для управления событиями
public class EventProducer {
    static private ArrayList<EventListener> listeners = new ArrayList<EventListener>(); //список слушателей локальных событий
    static synchronized public void addListener(EventListener listener) //добавить слушателя
    {
        listeners.add(listener);
    }
    //получить список слушателей
    static public EventListener[] getMyListeners()
    {
        return listeners.toArray(new EventListener[listeners.size()]);
    }
    //удалить слуателя
    static public void removeMyListener(EventListener listener)
    {
        listeners.remove(listener);
    }

    static private ArrayList<NetworkEventListener> netListeners = new ArrayList<>(); //список слушателей сетевых событий
    static synchronized public void addListener(NetworkEventListener listener) //добавить слушателя
    {
        netListeners.add(listener);
    }
    //получить список слушателей
    static public NetworkEventListener[] getNetListeners()
    {
        return netListeners.toArray(new NetworkEventListener[netListeners.size()]);
    }
    //удалить слуателя
    static public void removeListener(NetworkEventListener listener)
    {
        netListeners.remove(listener);
    }
    //методы вызова событий
    static public void firePlayerRequestMove(int x, int y)
    {
        for(EventListener listener : listeners)
            listener.playerRequestMove(x,y);
    }

    static public void fireSetOptions(int width, int height, int maxPlayers, int singlePlayer)
    {
        for(EventListener listener : listeners)
            listener.setOptions(width,height,maxPlayers,singlePlayer);
    }

    static public void fireUpdateField(ArrayList<Point> updatePoints, int player,int[] score)
    {
        for(EventListener listener : listeners)
            listener.updateField(updatePoints,player,score);
    }

    static public void fireSetCurrentPlayer(String name,boolean block)
    {
        for(EventListener listener : listeners)
            listener.setCurrentPlayer(name,block);
    }

    static public void fireSetWinner(int[] score)
    {
        for(EventListener listener : listeners)
            listener.setWinner(score);
    }

    static public void fireAllPlayersDisconnect()
    {
        for(EventListener listener : listeners)
            listener.allPlayersDisconnect();
    }

    //события для сетевой игры
    static public void fireRemovePlayer(Player player)
    {
        for(int i=0;i<netListeners.size();i++)
            netListeners.get(i).removePlayer(player);
    }

    static public void fireStartServer(String name, int port)
    {
        netListeners.get(0).startServer(name,port);
    }

    static public void fireStopServer()
    {
        for(int i=0;i<netListeners.size();i++)
            netListeners.get(i).stopServer();
    }

    static public void fireSetServerEnabled(boolean f)
    {
        for(NetworkEventListener listener : netListeners)
            listener.setServerEnabled(f);
    }

    static public void fireAddPlayer(String name, int id)
    {
        for(NetworkEventListener listener : netListeners)
            listener.addPlayer(name);
    }

    static public void fireConnectPlayer(String name, String ip, int port)
    {
        for(NetworkEventListener listener : netListeners)
            listener.connectPlayer(name,ip,port);
    }

    static public void fireDisconnectPlayer()
    {
        for(int i=0;i<netListeners.size();i++)
            netListeners.get(i).disconnectPlayer();
    }

    static public void fireDisconnectPlayer(String name)
    {
        for(NetworkEventListener listener : netListeners)
            listener.disconnectPlayer(name);
    }

    static public void fireSetConnectPlayerStatus(boolean f)
    {
        for(NetworkEventListener listener : netListeners)
            listener.setConnectPlayerStatus(f);
    }

    static public void fireSignalStartGame()
    {
        for(int i=0;i<netListeners.size();i++)
            netListeners.get(i).signalStartGame();
    }

    static public void fireStartGame(int width, int height, String[] players)
    {
        for(int i=0;i<netListeners.size();i++)
            netListeners.get(i).startGame(width,height,players);
    }

    static public void fireStartGame(String[] players)
    {
        for(int i=0;i<netListeners.size();i++)
            netListeners.get(i).startGame(players);
    }

    static public void fireMessageToServer(Message message)
    {
        for(int i=0;i<netListeners.size();i++)
            netListeners.get(i).messageToServer(message);
    }

    static public void fireUpdateClientField(ArrayList<Point> updatePoints, int player, int[] score)
    {
        for(int i=0;i<netListeners.size();i++)
            netListeners.get(i).updateClientField(updatePoints, player, score);
    }
}
