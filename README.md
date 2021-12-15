# ATP-Project-GUI
Implementation of the GUI part of the Maze Game for ATP course in SISE department of BGU University.

[General Information]
This final part of the project is to train my skills in creating a fully working game with GUI using JavaFX and an MVVM architecture to separate the view from the business logic.

Business logic for creating and solving mazes using a multi-threaded version of client-server is implemented here: @ATP-Project

Technologies
The project was written using:

Java JDK-15
JavaFX 16
SceneBuilder
Algorithms used
Maze Generating:
You can choose which maze generating algorithm to use. Choose between:

Simple Maze Generator - Randomly places walls to form a maze. It produces simple and not interesting mazes
My Maze Generator - Uses Prim's Algorithm to generate interesting mazes with dead ends.
Maze Solving:
You can choose which maze solving algorithm to use. Choose between:

DFS - Depth First Search
BFS - Breadth First Search
Best First Search - Variation of the BFS algorithm that chooses the next cell to go to by calculating the cheapest path (a diagonal step costs more than a regular step)
Download and run the Game
First make sure you have an updated version of JavaFX installed on your computer. The following steps are required before launching the game for the first time.

Download the entire project
Open it from your favorite IDE (Intellij recommended)
If you're using Intellij - make sure to follow these steps to configure the JavaFX with Intellij: how to configure JavaFX with Intellij
Run and enjoy!
