# Proline Zero

ProlineZero is an all-in-one solution running on a workstation or a laptop for a single user. This mode is based on a zip file to extract and doesn’t require any modification of the computer’s configuration nor any administrative rights (zero installation & zero footprint on the computer configuration).

The launcher automatically starts all the required processes in the correct order, and also shuts down everything properly when it is being closed.
Please keep in mind that Proline is supposed to run on a high performance server. Therefore, do not expect Proline Zero to have the same performances if your computer is running with less than 8GB of memory. Also, you will need a large amount of disk space to store the database and the input files.

## Prerequisites

Proline Zero is a stand-alone application available for Windows and Linux operating systems. Your computer should have at least 8GB of RAM to work well (especially for XIC quantitation) and at least 2GB or disk space or more.
Everything is packaged inside Proline Zero so you should not need any prerequisites.

### Limitations

No 32 bits version is available.
The Linux version does not include R, which means that the Prostar macro in the Data Analyzer will not work.

## How to use ?

Windows users just have to double-click on the file Proline-Zero.exe
Linux users first need to grant execution privilege to the file Proline-Zero.sh and then execute it

```
> chmod +x Proline-Zero.sh
> ./Proline-Zero.sh
```

When running Proline-Zero, you will be asked to connect to the server. The information should already be filled but if not, you can use these information:
```
Server host: localhost
User: proline
Password: proline
```

### Input data

The default behavior of Proline is to load data from predefined directories. These directories are
```
./data/fasta: copy your fasta files here
./data/mascot: copy your identification files here (Mascot *.dat files, X!Tandem *.xml files, OMSSA *.omx files)
./data/mzdb: copy (or generate) your mzDB files here
```

### Configuration

A configuration dialog is displayed at start up. It allow user to modify main settings. The associated configuration file contains all settings for the launcher. These settings should not be changed unless you are an advanced user.

* datastore: the type of database you want to use. Only PostgreSQL is available at the moment.
* memory settings, including allocation mode (automatic, semi Automatic, manual) and amount of memory allocated to each module.
* server_default_timeout: the time in second you give for each sub-process to start. If the process does not start after this number of seconds, the launcher will quit in error.
* log_debug: the debug mode provides more information and disables the memory check.
* sequence_repository_active: if you are a bit low on memory and if you do not need protein sequences, you may want to disable it to gain some memory.
* datastore_port: the port used by PostgreSQL. Warning: you should not change it if the datastore have already been initialized (at first launch).
* java_home: you can define your own JRE, but by default we use the one package with Proline Studio. Proline has been tested with Java8.
* cortex_version: the version number of Proline Cortex
* hornetq_version: the version number of HornetQ
* seqrepo_version: the version number of the Sequence Repository module
* studio_version: the version number of Proline Studio

# License

This project is licensed under the [CeCILL License V2.1](http://www.cecill.info/licences/Licence_CeCILL_V2.1-en.html)

# Proline Web Site

 Visit http://www.profiproteomics.fr/proline for Proline Suite documentation and downloads.
