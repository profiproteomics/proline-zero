package fr.proline.zero.util;

public class SettingsConstant {

	public final static String JMS_PORT = "jms_server_port";
	public static int DEFAULT_POSTGRESQL_PORT = 5433;
	public static int DEFAULT_H2_PORT = 9092;
	public static int DEFAULT_JMS_PORT = 5445;
	public static int DEFAULT_JMS_BATCH_PORT = 5455;
	public static int DEFAULT_JMS_JNP_PORT = 1099;
	public static int DEFAULT_JMS_JNP_RMI_PORT = 1098;
	public final static String DEFAULT_TOTAL_MEMORY = "6G";
	public final static String DEFAULT_STUDIO_MEMORY = "1G";
	public final static String DEFAULT_SERVER_TOTAL_MEMORY = "5G";
	public final static String DEFAULT_SEQREP_MEMORY = "1G";
	public final static String DEFAULT_DATASTORE_MEMORY = "1G";
	public final static String DEFAULT_CORTEX_MEMORY = "2G";
	public final static String DEFAULT_JMS_MEMORY = "1G";
	public final static String SEQREP_MODULES_TOOLTIP = "Start Sequence Repository : Enable or disable Sequence repository. If disabled, Protein sequences will not be available";
	public final static String STUDIO_MODULES_TOOLTIP = "Start Proline Studio : if Disabled, Proline Zero will run in server mode. Icon in System tray shoud be used to close Proline Zero";
	public final static String HIDEDIALOG_TOOLTIP = "Do not show again : If checked, this windows will not be displayed at next start. See config file, to view it again";
	public final static String MEMORY_HELP_PANE = "<br>"
			+ "Specify the desired memory allocation mode and the amount of memory. <br>"
			+ "- In Automatic mode, only the Total memory should be specified.<br>&nbsp;&nbsp;&nbsp;&nbsp;The system will define memory for each component.<br>"
			+ "- In Semi-automatic mode, memory amount for Proline Studio can be specified as well as Server memory<br>&nbsp;&nbsp;&nbsp;&nbsp;to dispatch between other components.<br>"
			+ "- Finally, in manual mode, the amount of memory for each component should be specified.";
	public final static String FOLDERS_HELP_PANE = "\n"
			+ "Specify folders, identified by a label, containing : <br>" +
			" - identification result files (for mascot, omassa...), <br>" +
			" - quantitation files (in mzdb format) <br>" +
			" - fasta files (used by sequence repository) <br>" +
			"<br> You can also specify a maximum size for temp folder, in order to be asked to clear it if it's size is above specified one.";
	public final static String FOLDER_MAX_SIZE_TOOLTIP = "Maximum size for temp folder :  If temporary folder size exceed specified one, you will be asked to empty this folder";
	public final static String SERVER_HELP_PANE = "\n" + "Specify datastore (Postgresql or H2) port to be used.<br>"
			+ "WARNING : once datastore has been initialized this SHOULD NOT be changed.<br>"
			+ "It may cause Proline Zero to be unstable. This may be done only in some special cases";
	public final static String ADVANCED_HELP_PANE = "<br>"
			+ "Specify different ports to be used by JMS Server (this is usefull when 2 ProlineZero may run at the same time).<br>"
			+ "Other server specific settings are also available such as \"Server Thread pool size\" which specify the number of process running at the same time on server side. <br>"
			+ "Note: This parameter depends on computer configuration, if too high the computer may stop responding.";
	public final static String SERVER_TIMEOUT_TOOLTIP = "Server default timeout : Time to wait before considering a component as unavailable";
	public final static String JVM_PATH_TOOLTIP = "JVM Path : Path to a Java Runtime to be used by ProlineZero components.";
	public final static String FORCE_DATASTORE_UPDATE_TOOLTIP = "Force Datastore update : Force \"run Proline Admin update\". This may be necessary when only some list data have been updated (such as quantitation methods...)";
	public final static String PARSING_RULES_HELP_PANE ="\n"+
			" Specify parsing rules used by Sequence Repository to retrieve <br> " +
			" Protein accession from fasta entry. <br>"
			+ "A complete description \nis given in Proline Admin documentation <br>" +
			" or on Forum \"Parsing rules for Sequence Repository\" topic";

	public final static  String PARSING_RULES_HELP_TEST_DIALOG="""
                This dialog displays the results of the test applied
                to all the fasta files contained inside fasta directories.\s
                For each file, Proline determines the appropriate parsing rule.\s
                If none matches, the default regular expression will be used.""";

	public static boolean isBooleanTrue(String booleanValue) {
		if (booleanValue != null
				&& (booleanValue.equals("on") || booleanValue.equals("true") || booleanValue.equals("yes"))) {
			return true;
		}
		return false;
	}

	public static String booleanToString(boolean b) {
		if (b) {
			return "on";
		}
		return "off";
	}



}
