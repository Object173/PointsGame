package com.object173.pointsgame.events;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.net.InetAddress;

/**
 * Created by Ярослав on 02.10.2016.
 */

//слушатель локальных событий
public interface EventListener {
    //от представления
    public void playerRequestMove(int x, int y); //запрос на совершение хода
    public void setOptions(int width, int height,int maxPlayers, int singlePlayer); //установка настроек

    //от модели
    public void updateField(ArrayList<Point> updatePoints, int player, int[] score); //обновление поля

    //от контроллера
    public void setCurrentPlayer(String name, boolean block); //установка текущего игрока
    public void setWinner(int[] score); //конец игры
    public void allPlayersDisconnect(); //все игроки отсоединились (игроку серверу)
}
