This application is intended to compare performance times between chess simulations.
One examines the performance of a serial algorithm, the other looks at a concurrent implementation.

The build-chess script compiles all required source code and creates an executable jar file in the artifacts directory.

To run the single-threaded implementation, execute run-chess-serial. To see a significant performance increase, execute run-chess-concurrent. Execution permissions may need to be applied to any scripts:

`chmod +x <script-name>`

To run the project on the Bridges cluster, the source repository may be cloned into a directory of your choosing from your user account. Navigate to the directory you wish to clone to and

`git clone https://github.com/Sedins4L/distro-project.git`

The repository already contains the executable jar file. You may run the program directly with either of the run configurations, or build it yourself.
