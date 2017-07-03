/**
 * Created by z on 26.06.2017.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.io.File;
import java.awt.image.BufferedImage;

import static javax.imageio.ImageIO.read;

public class Game {

    final String TITTLE_OF_PROGRAM = "Snake";
    final String GAME_OVER_MSG = "Game over";
    final int POINT_RADIUS = 20;
    final static int HEIGHTS = 25;
    final static int WIDTH = 30;
    final int FIELD_DX = 6;
    final int FIELD_DY = 28;
    final int START_LOCATION = 200;
    final int START_SIZE = 6;
    final int START_X = 10;
    final int START_Y = 10;
    final int SHOW_DEALAY = 300;
    final int LEFT = 37;
    final int RIGHT = 39;
    final int UP = 38;
    final int DOWN = 40;
    final int START_DIRECTION = RIGHT;
    final Color DEFAULT_COLOR = Color.black;
    final Color FOOD_COLOR = Color.green;
    Snake snake;
    Food food;
    Poison poison;
    JFrame frame;
    Canvas canvasPanel;
    Random random = new Random();
    boolean gameOver = false;


    public static void main(String[] args) {
        new Game().go();
    }

    ;

    void go() {
        frame = new JFrame(TITTLE_OF_PROGRAM + ":" + START_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH * POINT_RADIUS + FIELD_DX, HEIGHTS * POINT_RADIUS + FIELD_DY);
        frame.setLocation(START_LOCATION, START_LOCATION);
        frame.setResizable(false);
        canvasPanel = new Canvas();
        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                snake.setDirection(e.getKeyCode());

            }
        });
        frame.setVisible(true);


        snake = new Snake(START_X, START_Y, START_SIZE, START_DIRECTION);
        food = new Food();
        poison = new Poison();


        while (!gameOver) {

            snake.move();

            if (food.isEaten()) {
                food.next();
                poison.next();

            }
            canvasPanel.repaint();
            try {
                Thread.sleep(SHOW_DEALAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ;
        }

    }

    class Snake {
        ArrayList<Point> snake = new ArrayList<Point>();
        int direction;

        public Snake(int x, int y, int length, int direction) {
            for (int i = 0; i < length; i++) {
                Point point = new Point(x - i, y);
                snake.add(point);
                this.direction = direction;
            }

        }

        boolean ifFood(Point food) {
            return ((snake.get(0).getX() == food.getX()) && (snake.get(0).getY() == food.getY()));
        }

        boolean isInsideSnake(int x, int y) {
            for (Point point : snake) {
                if ((point.getX() == x) && (point.getY() == y))
                    return true;
            }
            return false;
        }

        boolean isPoison(int x, int y) {
            for (Point point : snake) {
                if ((point.getX() == poison.getX()) && (point.getY() == poison.getY()))
                    return true;
            }
            return false;
        }


        void move() {
            int x = snake.get(0).getX();
            int y = snake.get(0).getY();
            if (direction == LEFT) x--;
            if (direction == RIGHT) x++;
            if (direction == UP) y--;
            if (direction == DOWN) y++;

            if (x > WIDTH - 1) gameOver = true;
            if (x < 0) gameOver = true;
            if (y > HEIGHTS - 1) gameOver = true;
            if (y < 0) gameOver = true;
            if (isInsideSnake(x, y)) gameOver = true;
            if (isPoison(x, y)) gameOver = true;

            snake.add(0, new Point(x, y));
            if (ifFood(food)) {
                food.eat();
                frame.setTitle(TITTLE_OF_PROGRAM + ":" + snake.size());
            } else {
                snake.remove(snake.size() - 1);
            }
        }

        void setDirection(int direction) {
            if ((direction >= LEFT) && (direction <= DOWN)) {
                if (Math.abs(this.direction - direction) != 2) {
                    this.direction = direction;
                }
            }
        }


        void paint(Graphics g) {
            for (Point point : snake) {
                point.paint(g);
            }
        }


    }

    class Point {
        int x, y;
        Color color = DEFAULT_COLOR;

        public Point(int x, int y) {
            this.setXY(x, y);

        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        void setXY(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void paint(Graphics g) {
            g.setColor(color);
            g.fillOval(x * POINT_RADIUS, y * POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
        }
    }

    class Food extends Point {
        public Food() {
            super(-1, -1);
            this.color = FOOD_COLOR;
        }

        void eat() {
            this.setXY(-1, -1);
        }

        boolean isEaten() {
            return this.getX() == -1;
        }

        void next() {
            int x, y;
            do {
                x = random.nextInt(WIDTH);
                y = random.nextInt(HEIGHTS);
            } while (snake.isInsideSnake(x, y));
            this.setXY(x, y);
        }
    }


    class Poison extends Point {
        public Poison() {
            super(-2, -2);
            this.color = Color.magenta;
        }

        void next() {
            int x, y;
            do {
                x = random.nextInt(WIDTH);
                y = random.nextInt(HEIGHTS);
            } while (snake.isInsideSnake(x, y));
            this.setXY(x, y);
        }

    }


    public class Canvas extends JPanel {
        @Override
        public void paint(Graphics g) {

            super.paint(g);
            snake.paint(g);
            food.paint(g);
            poison.paint(g);
            if (gameOver) {
                g.setColor(Color.red);
                g.setFont(new Font("Times New Roman", Font.BOLD, 80));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(GAME_OVER_MSG, (WIDTH * POINT_RADIUS + FIELD_DX - fm.stringWidth(
                        GAME_OVER_MSG)) / 1000 + 100, (HEIGHTS * POINT_RADIUS + FIELD_DY) / 2);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            BufferedImage image = null;
            try {
                image = read(new File("C:\\Users\\z\\IdeaProjects\\Game\\src\\Lawn-HD-pictures-52697.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            g.drawImage(image, 0, 0, this);
        }
    }


}