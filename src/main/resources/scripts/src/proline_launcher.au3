#cs ----------------------------------------------------------------------------

 AutoIt Version: 3.3.14.2
 Author:         C. Bruley

 Script Function:
	Starts Proline Server and GUI and stop it when GUI closes.

#ce ----------------------------------------------------------------------------


#include <MsgBoxConstants.au3>
#include <FileConstants.au3>
#include <AutoItConstants.au3>
#include <WinAPIFiles.au3>
#include <File.au3>

opt("ExpandVarStrings",1)

Local $cortex_version = IniRead("proline_launcher.ini", "Version", "cortex_version", "")
Local $hornetq_version = IniRead("proline_launcher.ini", "Version", "hornetq_version", "")
Local $studio_version = IniRead("proline_launcher.ini", "Version", "studio_version", "")
Local $server_max_memory = IniRead("proline_launcher.ini", "Proline", "server_max_memory", "4G")
Local $datastore = IniRead("proline_launcher.ini", "Proline", "datastore", "")
Local $java_home = IniRead("proline_launcher.ini", "Path", "java_home", ".\ProlineStudio-$studio_version$\jre")
Local $postgresql_port = IniRead("proline_launcher.ini", "PostgreSQL", "postgresql_port", "5432")

Local $pgport_config_regexp = "port\s*=\s*""(\d+)"""

If (StringLeft($java_home, 1) == "." ) Then
   $java_home = @ScriptDir & "\" & $java_home
EndIf

Local $log = @ScriptDir & "\" & "proline_launcher.log"
SplashTextOn("Proline Launcher", "Starting Proline", 450, 100, -1, -1,  $DLG_CENTERONTOP + $DLG_TEXTVCENTER , "", 11)

If FileExists("pid.txt") Then
   _Print("A Proline instance is already running. Trying to stop this instance before starting a new one.")
   killThemAll()
EndIf

;~ Check if datastore exists, if not setup Proline

Local $hSearch = FileFindFirstFile(".\data\databases\*")
Local $initDataStoreNeeded = ($hSearch == -1)
Local $pidFile = FileOpen("pid.txt", $FO_APPEND)

;- Start & Initialize (if needed) Proline databases
If ($datastore = "H2") Then
   startH2Datastore($initDataStoreNeeded)
Else
   If (checkPGPort()) Then
	  startPGDatastore($initDataStoreNeeded)
   EndIf
EndIf

;- Start HornetQ and store HORNETQ PID in pid.txt file

_Print("Starting HORNETQ process from Proline-Cortex-$cortex_version$\hornetq_light-$hornetq_version$\bin ...")

FileChangeDir( "Proline-Cortex-$cortex_version$\hornetq_light-$hornetq_version$\bin")
EnvSet("HORNETQ_HOME", "..")
Local $CONFIG_DIR="..\config\stand-alone\non-clustered"
Local $CLASSPATH = $CONFIG_DIR & ";..\schemas\;..\lib\*"
Local $CLUSTER_PROPS =" -Djnp.port=1099 -Djnp.rmiPort=1098 -Djnp.host=localhost -Dhornetq.remoting.netty.host=0.0.0.0 -Dhornetq.remoting.netty.port=5445"
Local $JVM_ARGS= $CLUSTER_PROPS & " -XX:+UseParallelGC  -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -Xms512M -Xmx1024M -Dhornetq.config.dir=$CONFIG_DIR$ -Djava.util.logging.manager=org.jboss.logmanager.LogManager -Djava.util.logging.config.file=$CONFIG_DIR$\logging.properties -Djava.library.path=."
Local $hornetQCmd = "$java_home$\bin\java $JVM_ARGS$ -classpath $CLASSPATH$ org.hornetq.integration.bootstrap.HornetQBootstrapServer hornetq-beans.xml"
_FileWriteLog($log, " Running HornetQ: " & $hornetQCmd )
Local $hqPID = Run($hornetQCmd, "", @SW_HIDE , $STDOUT_CHILD)
Local $sOutput = ""

;- Wait for HornetQ by looking for "Server is now live" on HQ process output

 While 1
        $sOutput = StdoutRead($hqPID, True)
        If @error Then ; Exit the loop if the process closes or StdoutRead returns an error.
		   _Print("ERROR reading output ")
           ExitLoop
        Else
		   If ($sOutput <> "") Then
			  ;_Print( "Stdout Read:" & $sOutput & @CRLF)
			  If (StringInStr($sOutput, "Server is now live") <> 0) Then
				 ExitLoop
			   EndIf
			EndIf
		EndIf
 WEnd

if ($hqPID == 0) Then
   MsgBox($MB_SYSTEMMODAL, "", "HornetQ Cannot be started. Abort Proline launcher")
   Exit(1)
Else
   FileWriteLine($pidFile, "HORNETQ = " & $hqPID)
   _Print("HORNETQ started with PID = " & $hqPID)
EndIf
FileChangeDir( "..\.." )

;- Start Cortex and store CORTEX PID in pid.txt file

Local $sFilename = "config\application.conf"
Local $fileContent=FileRead($sFilename,FileGetSize($sFilename))
Local $aArray = StringRegExp($fileContent, $pgport_config_regexp, $STR_RegExpArrayMatch)
If IsArray($aArray) Then
   $config_pg_port = $aArray[0]
   If ($config_pg_port <> $postgresql_port) Then
	  _Print("Wrong PG port configuration : found $postgresql_port$ in .ini file and $config_pg_port$ in Cortex application.conf")
	  MsgBox($MB_SYSTEMMODAL, "", "Wrong Postregsql configuration : port in .ini and application.conf are not matching. Abort Proline launcher")
	  KillThemAll()
	  Exit(1)
   EndIf
EndIf

_Print("Starting CORTEX process from Proline-Cortex-$cortex_version$ ...")
Local $cortexCmd = "$java_home$\bin\java -Xmx$server_max_memory$ -XX:+UseG1GC -XX:+UseStringDeduplication -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=30 -cp ""config;Proline-Cortex-$cortex_version$.jar;lib/*"" -Dlogback.configurationFile=config/logback.xml fr.proline.cortex.ProcessingNode"
_FileWriteLog($log, " Running Cortex: " & $cortexCmd )

Local $cortexPID = Run($cortexCmd, "", @SW_HIDE)
if ($cortexPID == 0) Then
   MsgBox($MB_SYSTEMMODAL, "", "CORTEX Cannot be started. Abort Proline launcher")
   Exit(1)
Else
   FileWriteLine($pidFile, "CORTEX = " & $cortexPID)
   _Print("CORTEX started with PID = " & $cortexPID)
EndIf

;~ TODO : wait for Cortex starting

FileChangeDir( ".." )
_Print("PROLINE Server started successfully")
FileClose($pidFile)

SplashOff()

;~ Start ProlineStudio
RunWait("ProlineStudio-$studio_version$\bin\prolinestudio.exe --console suppress")

If ($datastore = "H2") Then
   KillThemAll()
Else
   KillThemAll()
   RunWait("pgsql\bin\pg_ctl -D .\data\databases\pg -l pgsql.log stop", "", @SW_HIDE)
EndIf


_Print("PROLINE stopped successfully")


#cs ----------------------------------------------------------------------------

  Start H2 database and store H2 PID in pid.txt file. Finally, if needed init ProlineDataStore

#ce ----------------------------------------------------------------------------

Func startH2Datastore($initDataStore)
_Print("Starting H2 process ...")
Local $h2PID = Run("$java_home$\bin\java -cp ""Proline-Cortex-$cortex_version$/lib/*"" org.h2.tools.Console -tcp -pg", "", @SW_HIDE)

If ($h2PID == 0) Then
   MsgBox($MB_SYSTEMMODAL, "", "H2 Cannot be started. Abort Proline launcher")
   Exit(1)
Else
   FileWriteLine($pidFile, "H2 = " & $h2PID)
   _Print("H2 started with PID = " & $h2PID)
   If ($initDataStore) Then
	  initProlineDataStore()
   EndIf
EndIf
EndFunc

#cs ----------------------------------------------------------------------------

  Start PG database and initialize PG & Proline Datastore depending on the
  $initDataStore boolean parameter.

#ce ----------------------------------------------------------------------------

Func startPGDatastore($initDataStore)
; If database folder is empty, init PG data folder
If ($initDataStore) Then
   _Print("Init PG data folder")
    If FileExists("postgres.passwd") Then FileDelete("postgres.passwd") EndIf
    FileWrite("postgres.passwd", "proline" )
    RunWait("pgsql\bin\initdb.exe -U proline -A password -E utf8 --pwfile=postgres.passwd -D .\data\databases\pg", "", @SW_HIDE)
	FileDelete("postgres.passwd")
EndIf

_Print("Starting PG process ...")
Local $pgCode = RunWait("pgsql\bin\pg_ctl -w -D .\data\databases\pg -l pgsql.log -o ""-p $postgresql_port$"" start", "", @SW_HIDE)
if ($pgCode == 0) Then
   _Print("PG started")
   If ($initDataStore) Then
	  initProlineDataStore()
   EndIf
Else
   MsgBox($MB_SYSTEMMODAL, "", "PG Cannot be started. Abort Proline launcher")
   Exit(1)
EndIf
EndFunc


#cs ----------------------------------------------------------------------------

  Test if postgreSQL or any other process is listening on $postgresql_port

#ce ----------------------------------------------------------------------------

Func checkPGPort()
   TCPStartup()
   _Print("Testing PG port $postgresql_port$ ")
   Local $iSocket = TCPConnect("127.0.0.1", $postgresql_port)
    ; Si une erreur s'est produite, affiche le code d'erreur et retourne True.
    If @error Then
		TCPShutdown()
        Return True
    Else
        MsgBox($MB_SYSTEMMODAL, "", "PG Cannot be started because port $postgresql_port$ is already in use. Stop the already running PostgreSQL server or choose another port in proline_launcher.ini file. Abort Proline launcher")*
		TCPCloseSocket($iSocket)
		TCPShutdown()
		Exit(1)
    EndIf
EndFunc

#cs ----------------------------------------------------------------------------

  Initialize Proline Datastore using Priline Admin command. Is datastore is PostgreSQ based
  this function first ensure that Cortex config is using the postgresql port defined in .ini
  file

#ce ----------------------------------------------------------------------------

Func initProlineDataStore()

;~ TODO : Add a MsgBox with OK/CANCEL options to ask for datastore initialization confirmation

If ($datastore = "postgresql") Then
   Local $sFilename = "Proline-Cortex-$cortex_version$\config\application.conf"
   ReplaceInFile($sFilename, $pgport_config_regexp, "port = ""$postgresql_port$""")
EndIf

FileChangeDir("Proline-Cortex-$cortex_version$")
_Print("First use: Initializing Proline Datastore ...")
RunWait("$java_home$\bin\java -Xmx1024m -cp ""lib/*;config"" fr.proline.admin.RunCommand setup", "", @SW_HIDE)

_Print("First use: Creating a default user proline with password proline ...")
RunWait("$java_home$\bin\java -Xmx1024m -cp ""lib/*;config"" fr.proline.admin.RunCommand create_user -l proline -p proline", "", @SW_HIDE)

_Print("First use: Creating a default project ...")
RunWait("$java_home$\bin\java -Xmx1024m -cp ""lib/*;config"" fr.proline.admin.RunCommand create_project -oid 2 -n ""Proline_Project"" -desc ""Proline default Project"" ", "", @SW_HIDE)

FileChangeDir( ".." )
EndFunc

#cs ----------------------------------------------------------------------------

  Replace text in a File based on a regular expression

#ce ----------------------------------------------------------------------------

Func ReplaceInFile($file, $find, $replace)
	$fileContent=FileRead($file,FileGetSize($file))
	$fileContent=StringRegExpReplace($fileContent,$find,$replace)
	FileDelete($file)
	FileWrite($file,$fileContent)
EndFunc

#cs ----------------------------------------------------------------------------

  Kill all process whose ID is in the pid.txt file

#ce ----------------------------------------------------------------------------

Func killThemAll()
   Local $allKilled = True
   Local $pidFile = FileOpen("pid.txt", $FO_READ)
   For $i = _FileCountLines("pid.txt") to 1 Step -1
     Local $line = FileReadLine($pidFile, $i)
	 Local $aArray = StringSplit($line, "=")
	 Local $pid = StringStripWS($aArray[2], $STR_STRIPLEADING + $STR_STRIPTRAILING )
	 Local $process = StringStripWS($aArray[1], $STR_STRIPLEADING + $STR_STRIPTRAILING )
	 If ($process <> "H2") Then
	   Local $msg = "Try to stop " & $process & " with PID = " & $pid & " : "
	   If ProcessExists($pid) Then
		 If ProcessClose($pid) Then
		   _Print($msg & "successfully stopped");
		 Else
		   _Print($msg & "fail to stop. Please close this process manually then remove pid.txt file")
		   $allKilled = False
	     EndIf
	   Else
		_Print($msg & "process not found. Seems to be already stopped")
	   EndIf
	  Else
	     FileWriteLine("rpid.txt", $process & " = " & $pid)
	  EndIf

   Next
   FileClose($pidFile)
   If $allKilled Then
	  FileDelete("pid.txt")
	  If FileExists("rpid.txt") Then FileMove("rpid.txt", "pid.txt")
   Else
	  If FileExists("rpid.txt") Then FileDelete("rpid.txt")
   EndIf
EndFunc

#cs ----------------------------------------------------------------------------

  Print a text on Log file, console and SplashText at the same time

#ce ----------------------------------------------------------------------------

Func _Print($sMsgString)
    _FileWriteLog($log, $sMsgString)
    ConsoleWrite(@MON & "/" & @MDAY & "/" & @YEAR & " " & @HOUR & ":" & @MIN & ":" & @SEC & " " & $sMsgString & @CRLF)
	SplashTextOn("Proline Launcher", $sMsgString, 450, 100, -1, -1,  $DLG_CENTERONTOP + $DLG_TEXTVCENTER , "", 11)
EndFunc
