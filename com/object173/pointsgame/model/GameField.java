package com.object173.pointsgame.model;

import com.object173.pointsgame.Options;
import com.object173.pointsgame.events.EventProducer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ярослав on 12.01.2017.
 */

//класс игрового поля
public class GameField {
    private int width,height; //размеры поля
    private int[][] field; //массив клеток
    private ArrayList<Integer> pointsPlayer; //коллекция баллов игроков

    private Options options = Options.getInstance(); //ссылка на настройки
    //конструктор с параметрами из options
    public GameField()
    {
        Options options = Options.getInstance();
        width=options.getWidth();
        height=options.getHeight();
        pointsPlayer=new ArrayList<>();
        for(int i=0;i<options.getCountPlayer();i++) this.pointsPlayer.add(0);

        initField(); //инициализация поля
    }
    //конструктор с четко заданными параметрами
    public GameField(int width, int height, int countPlayer)
    {
        this.width=width;
        this.height=height;
        this.pointsPlayer=new ArrayList<>();
        for(int i=0;i<countPlayer;i++) this.pointsPlayer.add(0);
        initField();
    }
    //удаление игрока
    public void removePlayer(int player)
    {
        if(player>=pointsPlayer.size()) return; //если номер игрока выходит за пределы
        //все клетки игрока очищаем и заночим в коллекцию измененных клеток
        ArrayList<Point> points = new ArrayList<>();
        for(int x=0;x<width;x++)
            for(int y=0;y<height;y++)
                if(field[x][y]==options.getPlayerTag(player))
                {
                    field[x][y]=0;
                    points.add(new Point(x,y));
                }
        pointsPlayer.set(player,0);
        //вызываем событие обновления поля
        EventProducer.fireUpdateField(points,-1,getScore());
    }
    //инициализация поля
    private void initField()
    {
        field=new int[width][height];
        clear();
    }
    //очистка поля
    public void clear()
    {
        for(int i=0;i<width;i++)
            for(int j=0;j<height;j++)
                field[i][j]=0;
        //сбрасываем баллы игроков
        for (int p:pointsPlayer) p=0;
    }
    //проверка поля на заполненность
    public boolean isFill()
    {
        int count=0;
        for(int player:pointsPlayer)
            count+=player;
        return count>=width*height;
    }
    //возвращаем массив баллов игроков
    public int[] getScore()
    {
        int[] score=new int[pointsPlayer.size()];
        for(int i=0;i<pointsPlayer.size();i++) score[i]=pointsPlayer.get(i);
        return score;
    }
    //установка тега игрока в клетку (координаты клетки и метка игрока)
    private boolean setSquere(int x, int y, int player)
    {   //проверяем входные данные на корректность
        if(x<0 || x>=width || y<0 || y>=height) return false;
        if(player>=pointsPlayer.size()) return false;
        //если клетка принадлежит игроку, отнимаем у него балл
        if(field[x][y]>0) pointsPlayer.set(options.getPlayerForTag(field[x][y]),
                pointsPlayer.get(options.getPlayerForTag(field[x][y]))-1);
        field[x][y]=options.getPlayerTag(player); //устанавливаем метку в клетку
        pointsPlayer.set(player,pointsPlayer.get(player)+1); //прибавляем балл игроку

        return true;
    }
    //совершение хода (координаты клетки и метка игрока)
    public boolean putPoint(int x, int y, int player)
    {   //проверка корректности входных данных
        if(x<0 || x>=width || y<0 || y>=height) return false;
        if(player<0) return false;
        if(field[x][y]!=0) return false;
        if(!setSquere(x,y,player)) return false;
        //создаем коллекцию измененных клеток
        ArrayList<Point> updatePoints = new ArrayList<>();
        updatePoints.add(new Point(x,y));
        //поиск замкнутого контура и его заполнение
        fillCircuit(updatePoints,x,y,player);
        //вызываем событие обновление поля
        EventProducer.fireUpdateField(updatePoints,player,getScore());

        return true;
    }
    //заполнение замкнутого контура
    private void fillCircuit(List updatePoints, int x, int y, int player)
    {   //перебираем всех соседей заданной клетки
        for(int n=0;n<8;n++) {
            Point curPoint = nextPoint(new Point(x,y),n); //получаем соседнюю клетку
            if(!validPoint(curPoint,player)) continue; //если не прошла валидацию, переходим к следующей
            //коллекция клеток контура
            ArrayList<Point> points = new ArrayList<>();
            //ищем замкнутый контур
            if (!checkCircuit(points, new ArrayList<Point>(), curPoint, curPoint, 0)) return;
            //заливка контура, проходим по всем клеткам контура
            while (points.size() > 1) {
                Point point = points.get(0);
                int i; //ищем клетку над текущей
                for (i = 1; i < points.size(); i++)
                    if (points.get(i).y == point.y) break;
                //если не нашли, переходим к следующей
                if (i >= points.size()) {
                    points.remove(point);
                    continue;
                }
                Point point1 = points.get(i);
                //проверяем, что расстояние между ними больше 1
                if (Math.abs(point.x - point1.x) > 1) {
                    int y0 = point.y; //устанавливаем координаты
                    int x0 = point.x, x1 = point1.x;
                    if (x1 < x0) {
                        int k = x0;
                        x0 = x1;
                        x1 = k;
                    }
                    //проходим от нижней клетки к верхней и заполняем
                    for (i = x0; i < x1; i++) {
                        setSquere(i,y0,player);
                        updatePoints.add(new Point(i,y0));
                    }
                    //удалем клетку из списка
                    points.remove(point1);
                }
                points.remove(point);
            }
        }
    }
    //валидация клетки
    private boolean validPoint(Point test, int player){
        return test.x>=0 && test.x < width && test.y >= 0 && test.y < height &&
                field[test.x][test.y]==options.getPlayerTag(player);
    }
    //валидация клетки
    private boolean validPoint(Point test, Point player){
        return test.x>=0 && test.x < width && test.y >= 0 && test.y < height &&
                field[test.x][test.y]==field[player.x][player.y];
    }
    //получение соседа клетки (dir номер направления)
    private Point nextPoint(Point cur,int dir){
        switch (dir){
            case 0: return new Point(cur.x + 1, cur.y);
            case 1: return new Point(cur.x + 1, cur.y +1);
            case 2: return new Point(cur.x, cur.y + 1);
            case 3: return new Point(cur.x-1, cur.y + 1);
            case 4: return new Point(cur.x -1, cur.y);
            case 5: return new Point(cur.x - 1, cur.y -1);
            case 6: return new Point(cur.x, cur.y - 1);
            case 7: return new Point(cur.x+1, cur.y - 1);
            default: return null;
        }
    }
    //поиск замкнутого контура (список клеток, список использованных клеток, текущая клетка, искомая клетка, номер итерации)
    private boolean checkCircuit(List points, List usedPoints, Point cur, Point goal, int countStep){
        countStep++;
        usedPoints.add(cur); //добавляем текущую клетку в использованные
        for (int i=0;i<8;i++) //проходим по всем соседям
        {
            Point newPoint = nextPoint(cur,i); //получаем соседнюю клетку
            if (!validPoint(newPoint,goal)) //валидируем
                continue;
            if (newPoint.equals(goal) && countStep>2) //если достигли искомой клетки и итерация больше 2
            {
                points.add(newPoint); //добавляем текущую клетку в список
                return true;
            }
            if (usedPoints.indexOf(newPoint)>-1) //если текущая клетка есть в списке использованных
                continue;
            if (checkCircuit(points,usedPoints,newPoint,goal,countStep)) //рекурсивно вызываем для соседа
            {
                points.add(newPoint);
                return true;
            }
        }
        return false;
    }
}
