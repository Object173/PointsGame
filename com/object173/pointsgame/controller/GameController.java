package com.object173.pointsgame.controller;

import com.object173.pointsgame.Options;
import com.object173.pointsgame.events.EventAdapter;
import com.object173.pointsgame.events.EventProducer;
import com.object173.pointsgame.events.NetworkEventAdapter;
import com.object173.pointsgame.model.GameField;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Ярослав on 12.01.2017.
 */

//игровой контроллер (общий для клиентов и сервера
public class GameController {
    private ServerController server; //контроллер сервера
    private Player player; //игрок

    private Options options=Options.getInstance(); //ссылка на настройки
    //конструктор
    public GameController()
    {
        //добавляем слушателей событий
        EventProducer.addListener(new EventAdapter() {
            @Override
            public void playerRequestMove(int x, int y) //сообщение от игрока о совершении хода
            {
                EventProducer.fireSetCurrentPlayer(player.getName(),false);
                GameController.this.playerRequestMove(x, y);
            }
            @Override
            public void updateField(ArrayList<Point> updatePoints, int player, int[] score) //обновление поля
            {
                GameController.this.updateField(updatePoints,player,score);
            }
            @Override
            public void setOptions(int width, int height,int maxPlayers, int singlePlayer) //установка настроек
            {
                GameController.this.setOptions(width,height,maxPlayers,singlePlayer);
            }
        });
        //слушатель событий сетевой чатси
        EventProducer.addListener(new NetworkEventAdapter(){
            @Override
            public void startServer(String name, int port) //запуск сервера
            {
                GameController.this.startServer(name,port);
            }
            @Override
            public void stopServer() //остановка сервера
            {
                GameController.this.stopServer();
            }
            @Override
            public void connectPlayer(String name, String ip, int port) //подключение игрока
            {
                GameController.this.connectPlayer(name,ip,port);
            }
            @Override
            public void disconnectPlayer() //отклчение игрока
            {
                GameController.this.disconnectPlayer();
            }
            @Override
            public void signalStartGame() //сигнал к началу игры
            {
                GameController.this.signalStartGame();
            }
            @Override
            public void startGame(int width, int height, String[] players) //начало игры
            {
                GameController.this.startGame(width,height,players);
            }
        });
    }
    //запрос на совершение хода
    private boolean playerRequestMove(int x, int y)
    {
        if(player==null) return false; //проверяем создан ли игрок
        player.makeMove(x,y); //запрашиваем совершение хода
        return true;
    }
    //обновление поля
    private void updateField(ArrayList<Point> updatePoints, int player, int[] score)
    {
        if(this.player==null) return; //проверяем создан ли игрок
        this.player.updateField(updatePoints,player,score); //передаем игроку запрос на обновления поля
    }
    //сигнал к началу игры (от представления)
    private void signalStartGame()
    {
        if(server==null) return; //проверяем создан ли сервер
        server.startGame(); //вызываем зыпуск игры на сервере
    }
    //начало игры
    private void startGame(int width, int height, String[] players)
    {   //устанавливаем полученные настройки
        options.setWidth(width);
        options.setHeight(height);
        options.setCountPlayer(players.length);

        EventProducer.fireStartGame(players); //вызываем событие начала игры
    }
    //запуск сервера
    private void startServer(String name, int port)
    {
        if(server!=null) server.disconnect(); //если есть сервер, отключаем его

        server=new ServerController(name); //создаем новый сервер
        player=server.getPlayer(0); //получаем локального игрока

        EventProducer.fireSetServerEnabled(server.connect(port)); //вызываем событие установки активности сервера
    }
    //остановка сервера
    private void stopServer()
    {
        if(server!=null) server.disconnect(); //если есть сервер, отключаем
        server=null;
    }
    //подключение к серверу
    private void connectPlayer(String name, String ip, int port)
    {
        player=new NetworkPlayer(name); //создаем игрока - клиента
        player.connect(ip,port); //подключем его к серверу
    }
    //отклчюение игрока
    private void disconnectPlayer()
    {
        if(player==null) return; //если игрока нет, выходим
        player.exitPlayer(); //вызываем выход игрока у игрока
        server=null;
    }
    //установка настроек
    private void setOptions(int width, int height,int maxPlayers, int singlePlayer)
    {
        //валидируем настроки
        if(width<options.getMIN_SIZE() || width>options.getMAX_SIZE()) return;
        if(height<options.getMIN_SIZE() || height>options.getMAX_SIZE()) return;
        if(maxPlayers<options.getMIN_PLAYERS() || maxPlayers>options.getMAX_PLAYERS()) return;
        if(singlePlayer<0 || singlePlayer>maxPlayers) return;
        //выставляем новыенастройки
        options.setWidth(width);
        options.setHeight(height);
        options.setCountPlayer(maxPlayers);
        options.setSinglePlayer(singlePlayer);
    }
}
