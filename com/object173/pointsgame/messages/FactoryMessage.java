package com.object173.pointsgame.messages;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Ярослав on 26.09.2016.
 */

//фабрика сообщений
public class FactoryMessage { //фабрика сообщений

    public static Message createMessage(String message) {return new Message(message);}
    public static Message createMessage(String message, String player,int id) {return new MessagePlayer(message,player,id);}
    public static Message createMessage(int width, int height, String[] players) {return new MessageStart(width,height,players);}
    public static Message createMessage(int x, int y, int id) {return new MessagePoint(x,y,id);}
    public static Message createMessage(ArrayList<Point> updatePoints, int player, int[] score) {
        return new MessageUpdate(updatePoints,player,score);}
    public static Message createMessage(int[] score) {return new MessageWinner(score);}
}
