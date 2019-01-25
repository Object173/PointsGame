package com.object173.pointsgame;

import com.object173.pointsgame.controller.*;
import com.object173.pointsgame.view.*;

/**
 * Created by Ярослав on 30.12.2016.
 */

//класс с точкой входа в программу
public class Main {
    //метод начала программы
    public static void main(String args[])
    {
        System.out.println("Start game");
        GameController controller = new GameController(); //создаем контроллер
        MainMenuForm view = new MainMenuForm(); //создаем представление
    }
}
