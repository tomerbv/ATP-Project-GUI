package Model;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.util.Observer;

public interface IModel {
    void generateMaze(int rows, int cols);
    Maze getMaze();
    void updatePlayerLocation(Direction direction);
    int getPlayerRow();
    int getPlayerCol();
    void setObserver(Observer o);
    void solveMaze();
    Solution getSolution();
}
