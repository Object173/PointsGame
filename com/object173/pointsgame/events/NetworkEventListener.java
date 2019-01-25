package com.object173.pointsgame.events;

import com.object173.pointsgame.controller.Player;
import com.object173.pointsgame.messages.Message;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Ярослав on 12.01.2017.
 */

//слушатель сетевых событий
public interface NetworkEventListener {
    //от представления
    public void startServer(String name, int port); //запуск сервера
    public void stopServer(); //остановка сервера
    public void connectPlayer(String name, String ip, int port); //подключение к серверу
    public void disconnectPlayer(); //отключение от сервера
    public void signalStartGame(); //сигнал начала игры

    //от контроллера сервера
    public void removePlayer(Player player); //удаление игрока
    public void setServerEnabled(boolean f); //статус активности сервера
    public void addPlayer(String name); //добавление игрока
    public void disconnectPlayer(String name); //отклчение игрока
    public void setConnectPlayerStatus(boolean f); //статус подключения игрока
    public void startGame(int width, int height, String[] players);//запуск игры
    public void startGame(String[] players);//запуск игры (для представления)
    public void messageToServer(Message message); //сообщение на сервер
    public void updateClientField(ArrayList<Point> updatePoints, int player, int[] score); //обновление поля у клиентов
}
