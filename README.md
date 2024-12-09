# Proline Zero

ProlineZero is an all-in-one solution running on a workstation or a laptop for a single user. This mode is based on a zip file to extract and doesn’t require any modification of the computer’s configuration nor any administrative rights (zero installation & zero footprint on the computer configuration).

The launcher automatically starts all the required processes in the correct order, and also shuts down everything properly when it is being closed.
Please keep in mind that Proline is supposed to run on a high performance server. Therefore, do not expect Proline Zero to have the same performances if your computer is running with low amount memory (8GB). Also, you will need a large amount of disk space to store the database and the input files.

## Prerequisites

Proline Zero is a stand-alone application available for Windows and Linux operating systems. Your computer should have at least 8GB of RAM to work well (especially for XIC quantitation), however, more memory is recommended, at least 2GB of disk space or more.
Everything is packaged inside Proline Zero so you should not need any prerequisites.

### Limitations

No 32 bits version is available.
The Linux version does not include R, which means that the ProstaR macro in the Data Analyzer will not work.

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

At startup a configuration window is displayed to allow user fe settings : memory usage, result files or mzdb files folders and so on 
These settings are stored in a configuration file to be used fir future executions.  

The configuration file, proline_launcher.config, contains parameter used by Proline Zero. These settings should not be changed in the file unless you are an advanced user.
Nevertheless, the **show_config_dialog** may be modified if you do not have access to the configuration window. For other parameters, see the decription is the file if needed. 
* show_config_dialog = on : specify if configuration window should be displayed at startup or not.

# License

This project is licensed under the [CeCILL License V2.1](http://www.cecill.info/licences/Licence_CeCILL_V2.1-en.html)

# Proline Web Site

 Visit http://www.profiproteomics.fr/proline for Proline Suite documentation and downloads.
