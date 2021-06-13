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

    public void movePlayer(Direction direction){
        model.updatePlayerLocation(direction);
    }

    public void movePlayer(MouseEvent mouseEvent) {
        Direction direction;

    }

    public void solveMaze(){
        model.solveMaze();
    }


    public void LoadMaze(Maze maze) {
        model.LoadMaze(maze);
    }

    public void stopServers() {
        model.stopServers();
    }

    public void load(Maze loadedMaze) {
        model.LoadMaze(loadedMaze);
    }



    public String[] getConfigurations() {
        return model.getConfigurations();
    }

    public void startServers() {
        model.startServers();
    }
}
