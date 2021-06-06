package ViewModel;

import Model.Direction;
import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

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
            case UP, NUMPAD8 -> direction = Direction.UP;
            case DOWN, NUMPAD2 -> direction = Direction.DOWN;
            case LEFT, NUMPAD4 -> direction = Direction.LEFT;
            case RIGHT, NUMPAD6 -> direction = Direction.RIGHT;
            case NUMPAD9 -> direction = Direction.UPRIGHT;
            case NUMPAD7 -> direction = Direction.UPLEFT;
            case NUMPAD1 -> direction = Direction.DOWNLEFT;
            case NUMPAD3 -> direction = Direction.DOWNRIGHT;
            default -> {
                // no need to move the player...
                return;
            }
        }
        model.updatePlayerLocation(direction);
    }

    public void movePlayer(MouseEvent mouseEvent) {
        Direction direction;
    }

    public void solveMaze(){
        model.solveMaze();
    }


}
