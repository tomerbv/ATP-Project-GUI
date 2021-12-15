# ATP-Project-GUI
Implementation of the GUI part of the Maze Game for ATP course in SISE department of BGU University.

## General Information
In this final part of the project I have implemented a GUI using JavaFX.
With a MVVM desighn pattern that separates the view from the backend logic and multithreading this this game allows the user to play a dynamic game with sound.

The backend logic for creating and solving mazes using a multi-threaded version of client-server is a .jar file in this project.
It's intire implementation can be found here:  [ATP-Project](https://github.com/tomerbv/ATP-Project)

## Technologies
The project was written using:

- Java JDK-15
- JavaFX 16
- SceneBuilder

## Features
As a user in this can you can do the following:

- Generate a maze row and column sizes of your liking.
- Solve that maze and be presented with the solution from the starting position.
- Save and load mazes ro and from the disk, if you wish to save the challenge for another time.
- Change cofiguration of: active thread number in the thread-pool, search algorithm for the maze solver.
- Initiate and stop music

## Download and run the Game
First make sure you have an updated version of JavaFX installed on your computer. The following steps are required before launching the game for the first time.

- Download the entire project
- Open it from your favorite IDE (Intellij recommended)
- If you're using Intellij - make sure to follow these steps to configure the JavaFX with Intellij: [how to configure JavaFX with Intellij](https://www.jetbrains.com/help/idea/javafx.html#download-javafx)
- Run and enjoy!

ScreenShots
- Game Initialization
![Screenshot (28)](https://user-images.githubusercontent.com/81749152/146232256-a586c1fb-119b-460e-b696-dc77ce40b358.png)

- Maze Generated
![Screenshot (29)](https://user-images.githubusercontent.com/81749152/146232397-e6856ea8-136e-45e8-a78c-183a55aab3af.png)

- Maze Solved
![Screenshot (31)](https://user-images.githubusercontent.com/81749152/146232403-130a9d5c-f955-48c1-87ba-33b78bec392d.png)

- Configurations
![Screenshot (30)](https://user-images.githubusercontent.com/81749152/146232390-dd4d847e-d6fa-4e33-8c4b-f75a0d3eb0a2.png)
