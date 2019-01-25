package com.object173.pointsgame.controller;

import com.object173.pointsgame.Options;
import com.object173.pointsgame.events.EventAdapter;
import com.object173.pointsgame.events.EventListener;
import com.object173.pointsgame.events.EventProducer;
import com.object173.pointsgame.events.NetworkEventListener;
import com.object173.pointsgame.messages.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Ярослав on 02.10.2016.
 */

//базовый класс для объектов игроков
public abstract class Player {
    protected int id; public int getId() {return id;} //id игрока
    protected String name="no"; public String getName() {return name;} //имя игрока
    protected boolean gameFlag=false; public boolean isGame() {return gameFlag;} //флаг активности игры

    protected int tag; //метка игрока

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        System.out.println("ser tag "+getName()+" "+tag);
        this.tag = tag;
    }

    public abstract boolean connect(String ip, int port); //подключение игрока к серверу
    public abstract void disconnect(); //отключение игрока
    public abstract void exitPlayer(); //выход игрока из игры
    public abstract void sendMessage(Message mes); //отправка сообщения
    public abstract void makeMove(int x, int y); //совершение хода
    public abstract void updateField(ArrayList<Point> updatePoints, int player, int[] score); //обновление игрового поля
    //обработка полученного сообщения
    protected void checkMessage(Message message) {
        if(message instanceof MessageStart) //если сообщение старта игры
        {
            MessageStart mes = (MessageStart)message;
            gameFlag=true; //выставляем флаг активности игры
            EventProducer.fireStartGame(mes.getWidth(),mes.getHeight(),mes.getPlayers()); //вызываем событие начала игры
            return;
        }
        if(message instanceof MessagePlayer) //если сообщение с информацией о игроке
        {
            MessagePlayer mp = (MessagePlayer)message;
            if (message.getMessage().equals(Options.MESSAGE_DISCONNECT)) //отключение игрока
            {
                EventProducer.fireDisconnectPlayer(mp.getPlayer()); //событие отклчения игрока
                return; //выходим, так как сообщение обработано
            }
            if (message.getMessage().equals(Options.MESSAGE_MOVE))  //установка текущего игрока (который ходит)
            {
                if(mp.getPlayer().equals(this.getName())) //сверяем имя текущего игрока со своим
                    //если являемся текущим игроком вызываем событие и разблокируем поле
                    EventProducer.fireSetCurrentPlayer(mp.getPlayer(),true);
                else
                    EventProducer.fireSetCurrentPlayer(mp.getPlayer(),false); //если нет, то блокируем поле
                return;
            }
        }
        if(message instanceof MessageUpdate) //сообщение обновления поля
        {
            MessageUpdate mu = (MessageUpdate)message;
            //вызываем событие обновления игрового поля
            EventProducer.fireUpdateClientField(mu.getUpdatePoints(),mu.getPlayer(),mu.getUpdateScore());
            return;
        }
        if(message instanceof MessageWinner) //сообщение конца игры
        {
            MessageWinner mw = (MessageWinner)message;
            EventProducer.fireSetWinner(mw.getScore()); //вызываем событие конца игры
            return;
        }
    }
}
