package com.object173.pointsgame.controller;

import com.object173.pointsgame.Options;
import com.object173.pointsgame.events.EventAdapter;
import com.object173.pointsgame.events.EventProducer;
import com.object173.pointsgame.messages.FactoryMessage;
import com.object173.pointsgame.messages.Message;
import com.object173.pointsgame.messages.MessageStart;
import com.sun.media.sound.SoftMixingMainMixer;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Ярослав on 02.10.2016.
 */

//класс игрока клиента (по сети)
public class NetworkPlayer extends Player implements Runnable {
    private Socket ss; //сокет
    private ObjectInputStream objIn; //потоки для общения с сервером
    private ObjectOutputStream objOut;

    private Thread thread; //рабочий поток
    private boolean alive=true; //флаг активности (работы потока)
    //конструктор (получает имя игрока
    public NetworkPlayer(String name)
    {
        this.name=name;
        ss=null;
        objIn=null;
        objOut=null;
    }
    //подключение к серверу (ip сервера и порт)
    @Override
    public boolean connect(String ip,int port)
    {
        try {
            ss = new Socket(ip, port); //открываем соединение
            thread = new Thread(this, "Client"); //создаем рабочий поток
            thread.setDaemon(true); //поток демон
            thread.start(); //запускаем поток
        }
        catch(Exception ex) {return false;}
        return true;
    }
    //отправка сообщения серверу
    @Override
    public void sendMessage(Message mes)
    {
        if (objOut==null) return;
        try
        {
            objOut.writeObject(mes);
        }
        catch (Exception ex) { }
    }
    //совершение хода, отправляем серверу сообщение с координатами и своим id
    @Override
    public void makeMove(int x, int y) {
        sendMessage(FactoryMessage.createMessage(x,y,this.getId()));
    }
    //обконление поля, отправляем сообщение (недоступно клиенту)
    @Override
    public void updateField(ArrayList<Point> updatePoints, int player, int[] score) {
    }
    //рабочий метод потока
    public void run()
    {
        try
        {   //устанавливаем потоки ввода/вывода
            objOut = new ObjectOutputStream(ss.getOutputStream());
            objIn = new ObjectInputStream(ss.getInputStream());

            objOut.writeObject(name); //отправляем серверу имя игрока
            String id = (String)objIn.readObject(); //полуаем от сервера id
            name=id+"."+name; //добавляем к имени игрока id
            this.id=Integer.parseInt(id);

            EventProducer.fireSetConnectPlayerStatus(true); //вызываем событие активности игрока

            while(alive) //пока выставлен флаг активности
            {
                Message code=(Message)objIn.readObject(); //считываем сообщение
                if (code==null) //если поток закрыт
                {
                    disconnect(); //отключаемся
                    break;
                }
                checkMessage(code); //обрабатываем сообщение
            }
        }
        catch(Throwable ex)
        {
            System.out.println(""+ex);
            disconnect(); //отключаемся от сервера
        }
    }
    //отключение от сервера
    @Override
    public void disconnect()
    {
        if(!alive) return; //если поток завершен, то выходим

        EventProducer.fireSetConnectPlayerStatus(false); //вызываем событие статуча подключения игрока
        try {
            alive=false; //сбрасываем флаг активности потока
            objIn.close(); //закрываем потоки ввода/вывода
            objOut.close();
            ss.close(); //закрываем сокет
        }
        catch(Exception ex) {System.out.println("fail disconnect");}
    }
    //выход игрока
    @Override
    public void exitPlayer()
    {
        disconnect(); //отключаемся
    }
}
