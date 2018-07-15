# Network Hoeffding Tree

Implementation of Hoeffding Tree in Java which runs on a network. This Hoeffding Tree reads data from network stream instead of files. This is a modification of Hoeffding Tree obtained from [Weka](https://github.com/Waikato/moa).

## Getting Started

These instructions will help you install and setup Network Hoeffding Tree into your local machine for development and testing purposes.

### Prerequisites

Network Hoeffding Tree is a maven application. This application has two dependencies:
- moa
- weka

The dependency is already specified in `pom.xml` file. These two dependencies are also provided in the `dependencies` folder.

### Installing

Simply clone this repository for installing it into your local computer.

```
git clone https://github.com/stainleebakhla/network-hoeffding-tree.git
```

## Compiling Network Hoeffding Tree

Since this is a maven application, the following command will compile the files for us

```
mvn clean install
```

The following command can also be used to compile the files together with all dependencies into a single jar file

```
mvn clean compile assembly:single
```

The `network-hoeffding-tree-1.0-SNAPSHOT-jar-with-dependencies.jar` file present in the repository is already compiled using the above command and can be used for running the application.

The `compile_and_run.sh` script is a bash script which automates compiling and running of the application.

## Running Network Hoeffding Tree

For starting the application, we need to specify the dependencies and the name of the main class. The `in.ac.iitkgp.stan.NetworkHoeffdingTree` class is the main class which contains the logic for starting the application. The following command runs the application

```
java -cp target/network-hoeffding-tree-1.0-SNAPSHOT.jar:/<dependency_path>/moa-2017.06.jar:/<dependency_path>/weka-dev-3.7.12.jar in.ac.iitkgp.stan.NetworkHoeffdingTree
```

If the dependency is already included in the jar package while compiling then the following command can be used for running the application

```
java -cp network-hoeffding-tree-1.0-SNAPSHOT-jar-with-dependencies.jar in.ac.iitkgp.stan.NetworkHoeffdingTree
```

The `compile_and_run.sh` script is a bash script which automates compiling and running of the application.

When the program starts, it will prompt for two port numbers as inputs for starting the Training Stream and the Testing Stream. Enter two port numbers and the program will wait for connection to be made on those two ports. Once connection is established, it will start the Hoeffding Tree with inputs from the Training and Testing Streams. Any input received from the Training Stream will be used to train the tree, and its result will be sent back through the same stream. And input received through the Testing Stream will be used for testing the data and its corresponding result will be sent back through the same stream.
