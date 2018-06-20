# this is for when dependencies are available in the local machine
# mvn clean install
# java -cp target/network-hoeffding-tree-1.0-SNAPSHOT.jar:/home/stainlee/.m2/repository/nz/ac/waikato/cms/moa/moa/2017.06/moa-2017.06.jar:/home/stainlee/.m2/repository/nz/ac/waikato/cms/weka/weka-dev/3.7.12/weka-dev-3.7.12.jar in.ac.iitkgp.stan.NetworkHoeffdingTree

# this is to include all dependencies within a single jar file
# so other machines need not have the dependencies installed
mvn clean compile assembly:single
mv target/network-hoeffding-tree-1.0-SNAPSHOT-jar-with-dependencies.jar network-hoeffding-tree-1.0-SNAPSHOT-jar-with-dependencies.jar
java -cp network-hoeffding-tree-1.0-SNAPSHOT-jar-with-dependencies.jar in.ac.iitkgp.stan.NetworkHoeffdingTree
