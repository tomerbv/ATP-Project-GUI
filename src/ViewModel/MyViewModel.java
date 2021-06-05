package ViewModel;

import Model.Direction;
import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
        this.model.setObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    public Maze getMaze(){
        return model.getMaze();
    }

    public int getPlayerRow(){
        return model.getPlayerRow();
    }

    public int getPlayerCol(){
        return model.getPlayerCol();
    }

    public Solution getSolution(){
        return model.getSolution();
    }

    public void generateMaze(int rows, int cols){
        model.generateMaze(rows, cols);
    }

    public void movePlayer(KeyEvent keyEvent){
        Direction direction;
        switch (keyEvent.getCode()){
            case UP -> direction = Direction.UP;
            case DOWN -> direction = Direction.DOWN;
            case LEFT -> direction = Direction.LEFT;
            case RIGHT -> direction = Direction.RIGHT;
            default -> {
                // no need to move the player...
                return;
            }
        }
        model.updatePlayerLocation(direction);
    }

    public void solveMaze(){
        model.solveMaze();
    }
}
