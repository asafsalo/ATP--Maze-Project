package Model;

import IO.MyDecompressorInputStream;
import Server.Server;
import Client.Client;
import Client.IClientStrategy;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.*;
import algorithms.search.*;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MyModel implements IModel {

    private Server mazeGenerateServer;
    private Server solveMazeServer;
    private Maze myMaze;
    private int CharacterPosRow = 1;
    private int CharacterPosCol = 1;
    private Position Goal;
    private boolean finished = false;
    private Solution mySolution;

    public void StartServers() {
        mazeGenerateServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveMazeServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        mazeGenerateServer.start();
        solveMazeServer.start();
    }
    public void stopServers() {
        mazeGenerateServer.stop();
        solveMazeServer.stop();
    }

    @Override
    public int[][] getMaze() {
        return myMaze.getMazeArray();
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public void solveMaze(){
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        Maze maze = myMaze;
                        toServer.writeObject(maze);
                        toServer.flush();
                        mySolution = (Solution) fromServer.readObject();
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
    public void generateMaze(int width, int height) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{width, height};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[])fromServer.readObject();
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[width*height+20 /**CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/];
                        is.read(decompressedMaze);
                        myMaze = new Maze(decompressedMaze);
                        CharacterPosCol = myMaze.getStartPosition().getColumnIndex();
                        CharacterPosRow = myMaze.getStartPosition().getRowIndex();
                        Goal = myMaze.getGoalPosition();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void MoveCharacter(KeyCode movement){
        switch (movement.getName()){
            case "8":
            case "Up":
                if(CharacterPosRow-1>=0){
                    if(!myMaze.isWall(CharacterPosRow-1,CharacterPosCol)) CharacterPosRow--;
                }
                break;

            case "2":
            case "Down":
                if (CharacterPosRow+1 < myMaze.getMazeArray().length){
                    if(!myMaze.isWall(CharacterPosRow+1,CharacterPosCol)) CharacterPosRow++;
                }
                break;

            case "6":
            case "Right":
                if (CharacterPosCol+1 < myMaze.getMazeArray()[0].length){
                    if(!myMaze.isWall(CharacterPosRow,CharacterPosCol+1)) CharacterPosCol++;
                }
                break;

            case "4":
            case "Left":
                if (CharacterPosCol-1 >=0){
                    if(!myMaze.isWall(CharacterPosRow,CharacterPosCol-1)) CharacterPosCol--;
                }
                break;

            case "9":
                if(CharacterPosRow-1>=0 && CharacterPosCol+1 < myMaze.getMazeArray()[0].length){
                    if(!(myMaze.isWall(CharacterPosRow-1,CharacterPosCol+1))){
                        CharacterPosCol++;
                        CharacterPosRow--;
                    }
                }
                break;

            case "7":
                if(CharacterPosRow-1>=0 && CharacterPosCol-1>=0){
                    if(!(myMaze.isWall(CharacterPosRow-1,CharacterPosCol-1))){
                        CharacterPosCol--;
                        CharacterPosRow--;
                    }
                }
                break;

            case "3":
                if(CharacterPosRow+1 < myMaze.getMazeArray().length && CharacterPosCol+1 < myMaze.getMazeArray()[0].length){
                    if(!(myMaze.isWall(CharacterPosRow+1,CharacterPosCol+1))){
                        CharacterPosCol++;
                        CharacterPosRow++;
                    }
                }
                break;

            case "1":
                if(CharacterPosRow+1 < myMaze.getMazeArray().length && CharacterPosCol-1 >=0){
                    if(!(myMaze.isWall(CharacterPosRow+1,CharacterPosCol-1))){
                        CharacterPosCol--;
                        CharacterPosRow++;
                    }
                }
                break;
        }
        if(CharacterPosCol==Goal.getColumnIndex() && CharacterPosRow==Goal.getRowIndex()){
            finished=true;
        }
    }

    @Override
    public int GetCharacterRowPos(){ return CharacterPosRow;}
    @Override
    public int GetCharacterColPos(){ return CharacterPosCol;}

    @Override
    public void SaveMaze(File file) {

    }

    @Override
    public void LoadMaze(File file) {

    }


}