package com.object173.pointsgame.controller;

import com.object173.pointsgame.events.EventProducer;
import com.object173.pointsgame.messages.FactoryMessage;
import com.object173.pointsgame.messages.Message;
import com.object173.pointsgame.messages.MessagePlayer;

import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Ярослав on 02.10.2016.
 */

//класс соединения с игроком на сервере
public class ServerReceiver extends Player implements Runnable{
    private Socket socket; //сокет
    private ObjectInputStream objIn = null; //потоки ввода/вывода
    private ObjectOutputStream objOut = null;

    private Thread thread; //рабочий поток
    private boolean alive=false; //флаг активности потока
    //конструктор (id игрока и сокет для соединения)
    public ServerReceiver(int id, Socket soc)
    {
        this.id=id;
        //создаем поток
        thread=new Thread(this,"client");
        thread.setDaemon(true);
        connect(soc); //подключемся
    }
    //подключение
    public void connect(Socket soc)
    {
        socket=soc;
        alive=true;
        thread.start();
    }
    //подключение к серверу (не доступно)
    @Override
    public boolean connect(String ip, int port) {return false;}
    //цикл потока
    public void run()
    {
        try
        {   //устанавливаем потоки ввода/вывода
            objOut = new ObjectOutputStream(socket.getOutputStream());
            objIn = new ObjectInputStream(socket.getInputStream());

            name=(String)objIn.readObject(); //получаем имя игрока
            objOut.writeObject(Integer.toString(id)); //передаем id игрока
            name=Integer.toString(id)+"."+name; //добавляем и имени id
            EventProducer.fireAddPlayer(name,getId()); //вызываем событие добавления игрока

            while(alive) //пока выставлен флаг активности
            {
                Message code=(Message)objIn.readObject(); //получаем сообщение
                if (code==null) //если поток закрыт, отключаемся
                {
                    close();
                    break;
                }
                checkMessage(code); //обрабатываем сообщение
            }
        }
        catch(Exception ex)
        {
            System.out.println("Receiver exception "+ex);
            close();
        }
    }
    //обработка сообщения
    @Override
    protected void checkMessage(Message mes) {
        EventProducer.fireMessageToServer(mes); //через событие передаем сообщение серверу
    }
    //закрываем соединение
    @Override
    public void disconnect()
    {
        try {
            alive=false;
            socket.close();
            objIn.close();
            objOut.close();
        }
        catch(Exception ex) {System.out.println("fail disconnect");}
    }
    //выход игрока (не доступно)
    @Override
    public void exitPlayer() {
    }
    //отключение
    public void close()
    {   //событие отклчения игрока
        EventProducer.fireRemovePlayer(this);
        disconnect();
    }
    //отправка сообщения
    @Override
    public void sendMessage(Message mes)
    {
        if (objOut==null) return;
        try
        {
            objOut.writeObject(mes);
        }
        catch (Exception ex) { close();}
    }
    //совершение хода (не доступно)
    @Override
    public void makeMove(int x, int y) {
    }
    //обновление поля (не доступно)
    @Override
    public void updateField(ArrayList<Point> updatePoints, int player, int[] score) {
    }
}
