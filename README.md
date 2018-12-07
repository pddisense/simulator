# simulator

This repository contains the source code for a simulator, used to reproduce the behaviour of the Private Data Donor platform at large scales.
The core is written in a mix of Java and Scala, along with some scripts for pre-processing written in Python.

You will need Python (either Python 2 and 3 should be supported) and pip to install the requirements from the requirements.txt file.
You will also need Java JDK 8 to compile and run the simulator.

## Running the simulator

The simulator can be compiled into so-called "fat JAR":

```bash
./sbt assembly
```

You can then launch it:

```bash
java -jar target/scala-2.12/simulator.jar
```

If running large-scale analyses, you may want to adjust the available memory, for example:

```bash
java -Xmx20G -Xms20G -jar target/scala-2.12/simulator.jar
```

Several options can be used to control the behaviour of the simulator, all of them passed as `key=value` arguments.
Available options are:
  * *delay*: number of days to wait for the results to be available
  * *days*: number of days to simulate
  * *users*: number of users to simulate
  * *queries*: source of the queries to simulate (either "yahoo" or "google", the "google" datasource requiring an additional resource file that is not made available in this repository for legal reasons).
  * *groups*: strategy to organise users into groups (either "naive" or "prune").
  * *gs*: group size.

Results are dumped into a directory such as `lumosresults/QZnAPNAogTi6UHdmRvNhai`, where each run is given a different unique identifier.
Each run directory contains two files `stdout` and `stderr`, capturing the content of the standard output and error streams.
Some special lines of the standard error stream starting with "LUMOS " are especially relevant, as they contain information about parameters, metrics or outputs.
An `artifacts` directory is also created, containing one sub-directory per step in the simulation workflow.
Those directories store the raw outputs of each step, such as the simulated activity or searches.
Please note that this requires some space, tens of gigabytes can be generated by large-scale runs (e.g., spanning a million users over several years).

## Post-processing

Once the simulator completes, we still have to process the output to obtain summarised results.
You can use a helper script to parse the stderr of every run and extract the relevant information from them:

```bash
./scripts/analyse.py /path/to/lumosresults
```

## Pre-processing activity

[A dataset coming from Mozilla](https://web.archive.org/web/20110711092459/https://testpilot.mozillalabs.com/testcases/a-week-life/aggregated-data.html) is used to simulate users' activity, i.e., how often they interact with their browser.
After downloading the dataset, you can start the pre-processing with the following command:

```bash
./scripts/process_mozilla.py /path/to/week-life-stats-70.csv
```

This will output to the console two parameters a and b that parametrise a beta distribution characterising the users' activity.
An `activity.csv` file is dumped as well, containing the activity simulated from the dataset.
Please note that the outcome of this pre-processing is already included (hard-coded) into the simulator.

## Pre-processing searches

[A dataset from Yahoo (L18)](https://webscope.sandbox.yahoo.com/catalog.php?datatype=l#toggle50) is used to simulate users' Web searches.
After downloading the dataset, you can start the pre-processing with the following command:

```bash
./scripts/process_yahoo.py /path/to/search_logs-v1.txt
```

This will output to the console two parameters mu and var that parametrise a normal distribution characterising the number of searches performed every day by a user.
A `queries.csv` file is created as well, containing the frequency of each query in the dataset.
Because the dataset is anonymised, meaning that we do not have access to the actual content of the query but only an identifier for it.
Please note that the outcome of this pre-processing is already included (hard-coded) into the simulator.
