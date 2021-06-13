package Model;

import Client.Client;
import IO.MyDecompressorInputStream;
import Server.Server;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import Client.IClientStrategy;
import Server.Configurations;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyModel extends Observable implements IModel{
    private Maze maze;
    private int characterRowPos, characterColPos;
    private Solution solution;
    private Server generateMazeServer, solveMazeServer;



    public MyModel() {
        characterRowPos = 1;
        characterColPos = 1;
        generateMazeServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveMazeServer = new Server(5401,1000, new ServerStrategySolveSearchProblem());
        generateMazeServer.start();
        solveMazeServer.start();

    }
    
    public void stopServers(){
        generateMazeServer.stop();
        solveMazeServer.stop();
    }

    public void startServers(){
        generateMazeServer.start();
        solveMazeServer.start();
    }

    @Override
    public String[] getConfigurations() {
        String[] configurations = new String[2];
        configurations[0] = String.valueOf(Configurations.getThreadPoolSize());
        configurations[1] = Configurations.getMazeSearchingAlgorithm().getName();
        return configurations;
    }



    @Override
    public void generateMaze(int rows, int cols) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[])fromServer.readObject();
                        InputStream inputstream = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[mazeDimensions[0]*mazeDimensions[1]+6];
                        inputstream.read(decompressedMaze);
                        setMaze(new Maze(decompressedMaze));
                        toServer.close();
                        fromServer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void LoadMaze(Maze maze){
        setMaze(maze);
    }

    private void setMaze(Maze temp) {
        this.maze = temp;
        characterRowPos = maze.getStartPosition().getRowIndex();
        characterColPos = maze.getStartPosition().getColumnIndex();
        this.solution = null;
        setChanged();
        notifyObservers("Generated");
        notifyObservers("Moved");
    }

    @Override
    public Maze getMaze() {
        return this.maze;
    }

    @Override
    public void updatePlayerLocation(Direction direction) {
        switch (direction) {
            case UP -> {
                if (characterRowPos > 0 && maze.GetPositionVal(getPlayerRow() - 1, getPlayerCol()) == 0)
                    movePlayer(characterRowPos - 1, characterColPos);
            }
            case DOWN -> {
                if (characterRowPos < maze.getRows() - 1 && maze.GetPositionVal(getPlayerRow() + 1, getPlayerCol()) == 0)
                    movePlayer(characterRowPos + 1, characterColPos);
            }
            case LEFT -> {
                if (characterColPos > 0 && maze.GetPositionVal(getPlayerRow() , getPlayerCol() - 1) == 0)
                    movePlayer(characterRowPos, characterColPos - 1 );
            }
            case RIGHT -> {
                if (characterColPos < maze.getColumns() - 1 && maze.GetPositionVal(getPlayerRow() , getPlayerCol() + 1) == 0)
                    movePlayer(characterRowPos, characterColPos + 1);
            }

            case UPRIGHT -> {
                if (characterRowPos > 0 && characterColPos < maze.getColumns() - 1 && maze.GetPositionVal(getPlayerRow() - 1, getPlayerCol() + 1) == 0 && (maze.GetPositionVal(getPlayerRow() - 1, getPlayerCol()) == 0 || maze.GetPositionVal(getPlayerRow() , getPlayerCol() + 1) == 0))
                    movePlayer(characterRowPos - 1, characterColPos + 1);
            }
            case DOWNRIGHT -> {
                if (characterRowPos < maze.getRows() - 1 && characterColPos < maze.getColumns() - 1 && maze.GetPositionVal(getPlayerRow() + 1, getPlayerCol() + 1) == 0 && (maze.GetPositionVal(getPlayerRow() + 1, getPlayerCol()) == 0 || maze.GetPositionVal(getPlayerRow() , getPlayerCol() + 1) == 0))
                    movePlayer(characterRowPos + 1, characterColPos + 1);
            }
            case UPLEFT -> {
                if (characterRowPos > 0 && characterColPos > 0 && maze.GetPositionVal(getPlayerRow() - 1 , getPlayerCol() - 1) == 0 && (maze.GetPositionVal(getPlayerRow() - 1, getPlayerCol()) == 0 || maze.GetPositionVal(getPlayerRow() , getPlayerCol() - 1) == 0))
                    movePlayer(characterRowPos - 1, characterColPos - 1 );
            }
            case DOWNLEFT -> {
                if (characterRowPos < maze.getRows() - 1 && characterColPos > 0 && maze.GetPositionVal(getPlayerRow() + 1 , getPlayerCol() - 1) == 0 && (maze.GetPositionVal(getPlayerRow() + 1, getPlayerCol()) == 0 || maze.GetPositionVal(getPlayerRow() , getPlayerCol() - 1) == 0))
                    movePlayer(characterRowPos + 1, characterColPos - 1);
            }
        }
//        if((characterRowPos==maze.getGoalPosition().getRowIndex()) && (characterColPos==maze.getGoalPosition().getRowIndex())){
//            setChanged();
//            notifyObservers("User Solved");
//
//        }

    }

    private void movePlayer(int row, int col){
        this.characterRowPos = row;
        this.characterColPos = col;
        setChanged();
        notifyObservers("Moved");
    }

    @Override
    public int getPlayerRow() {
        return characterRowPos;
    }

    @Override
    public int getPlayerCol() {
        return characterColPos;
    }


    @Override
    public void solveMaze() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze);
                        toServer.flush();
                        setSolution((Solution) fromServer.readObject());
                        toServer.close();
                        fromServer.close();
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void setSolution(Solution temp) {
        this.solution = temp;
        setChanged();
        notifyObservers("Solved");
    }

    @Override
    public Solution getSolution() {
        return solution;
    }



    @Override
    public void setObserver(Observer o) {
        this.addObserver(o);
    }

}
