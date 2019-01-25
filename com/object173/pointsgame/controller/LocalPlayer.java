package com.object173.pointsgame.controller;

import com.object173.pointsgame.Options;
import com.object173.pointsgame.events.EventAdapter;
import com.object173.pointsgame.events.EventProducer;
import com.object173.pointsgame.events.NetworkEventAdapter;
import com.object173.pointsgame.messages.FactoryMessage;
import com.object173.pointsgame.messages.Message;
import com.object173.pointsgame.messages.MessagePlayer;
import com.object173.pointsgame.messages.MessageStart;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Ярослав on 12.01.2017.
 */

//класс локального игрока (на сервере)
public class LocalPlayer extends Player {
    private boolean serverEnabled=false; //флаг активности сервера
    //конструктор, получаем имя и id
    public LocalPlayer(int id, String name)
    {
        this.id=id;
        this.name=Integer.toString(id)+"."+name; //добавляем к имени id
        serverEnabled=true; //выставляем флаг активности сервера
    }
    //совершение хода
    @Override
    public void makeMove(int x, int y) {
        //серез событие передаем серверу сообщение о совершении хода
        EventProducer.fireMessageToServer(FactoryMessage.createMessage(x,y,this.getId()));
    }
    //обновление поля
    @Override
    public void updateField(ArrayList<Point> updatePoints, int player, int[] score) {
        //через событие передаем серверу сообщение о обновлении поля
        EventProducer.fireMessageToServer(FactoryMessage.createMessage(updatePoints,player,score));
    }
    //подключение к серверу (не требуется)
    @Override
    public boolean connect(String ip, int port) {return false;}
    //отключение (не требуется)
    @Override
    public void disconnect() {
    }
    //выход игрока из игры
    @Override
    public void exitPlayer()
    {
        EventProducer.fireStopServer(); //останавливаем сервер
    }
    //отправка сообщения (передаем на обработку)
    @Override
    public void sendMessage(Message mes) {
        checkMessage(mes);
    }
}
