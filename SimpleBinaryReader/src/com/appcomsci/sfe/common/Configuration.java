/**
 * 
 */
package com.appcomsci.sfe.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Allen McIntosh
 *
 */
public class Configuration {
	
	public static final String PROPERTY_FILE = "emulator.properties";
	private static final String DEFAULT_PROPERTY_FILE = "emulator.properties";
	
	public static final String READER_PATH = "binary.reader.path";
	public static final String DEFAULT_READER_PATH = "/opt/mipsel/usr/bin/mipsel-linux-gnu-llvm-objdump";
	
	public static final String BINARY_FILE_NAME_PROPERTY = "emulator.binary";
	
	public static final String BINARY_EVA_FILE_NAME_PROPERTY = "eva.binary";
	
	public static final String ENTRY_POINT_PROPERTY = "entry.point";
	public static final String DEFAULT_ENTRY_POINT = "sfe_main";
	
	public static final String EMULATOR_CLIENT_DIR_PROPERTY = "emulator.client.dir";
	public static final String DEFAULT_EMULATOR_CLIENT_DIR = ".";
	
	public static final String EMULATOR_SERVER_DIR_PROPERTY = "emulator.server.dir";
	public static final String DEFAULT_EMULATOR_SERVER_DIR = ".";
	
	public static final String FUNCTION_LOAD_LIST_PROPERTY = "function.load.list";
	
	public static final String SERVER_NAME_PROPERTY = "server.name";
	public static final String DEFAULT_SERVER_NAME = "localhost";
	
	public static final String MAX_PROGRAM_STEPS_PROPERTY = "max.program.steps";
	public static final int DEFAULT_MAX_PROGRAM_STEPS = 1000;

	
	private String binaryFileName;
	private String binaryEvaFileName;
	private String entryPoint;
	private String emulatorClientDir;
	private String emulatorServerDir;
	private String serverName;
	private String binaryReaderPath;
	private int maxProgramSteps;
	
	private List<String> functionLoadList;
	
	/** Standard constructor.
	 * Initializes from a property file specified via -D, or from the default
	 * property file if no property file was given.
	 * @throws IOException
	 */
	public Configuration() throws IOException {
		String props = System.getProperty(PROPERTY_FILE, DEFAULT_PROPERTY_FILE);
		InputStream resourceStream = null;	
		SfeProperties properties = null;
		try {
			System.out.println("Working Directory = " +
		              System.getProperty("user.dir"));
			resourceStream = new FileInputStream(props);			
		} catch(FileNotFoundException e) {
			resourceStream =  Configuration.class.getClassLoader().getResourceAsStream(props);
		}
		if(resourceStream == null) {
			properties = new SfeProperties();
		} else {
			properties = new SfeProperties(resourceStream);
			resourceStream.close();
		}
		
		binaryFileName = properties.getProperty(BINARY_FILE_NAME_PROPERTY);
		binaryEvaFileName = properties.getProperty(BINARY_EVA_FILE_NAME_PROPERTY);
		entryPoint = properties.getProperty(ENTRY_POINT_PROPERTY, DEFAULT_ENTRY_POINT);
		emulatorClientDir = properties.getProperty(EMULATOR_CLIENT_DIR_PROPERTY, DEFAULT_EMULATOR_CLIENT_DIR);
		emulatorServerDir = properties.getProperty(EMULATOR_SERVER_DIR_PROPERTY, DEFAULT_EMULATOR_SERVER_DIR);
		serverName = properties.getProperty(SERVER_NAME_PROPERTY, DEFAULT_SERVER_NAME);
		binaryReaderPath = properties.getProperty(READER_PATH, DEFAULT_READER_PATH);
		maxProgramSteps = properties.getProperty(MAX_PROGRAM_STEPS_PROPERTY, DEFAULT_MAX_PROGRAM_STEPS);
		initFunctionLoadList(properties);	
	}
	
	public Configuration(Configuration that) {
		this.setBinaryFileName(that.getBinaryFileName());
		this.setBinaryEvaFileName(that.getBinaryEvaFileName());
		this.setEntryPoint(that.getEntryPoint());
		this.setEmulatorClientDir(that.getEmulatorClientDir());
		this.setEmulatorServerDir(that.getEmulatorServerDir());
		this.setServerName(that.getServerName());
		this.setBinaryReaderPath(that.getBinaryReaderPath());
		this.setMaxProgramSteps(that.getMaxProgramSteps());
		functionLoadList = new ArrayList<String>();
		for(String s:that.getFunctionLoadList()) {
			functionLoadList.add(s);
		}
	}
	
	private void initFunctionLoadList(SfeProperties properties) {
		String s = properties.getProperty(FUNCTION_LOAD_LIST_PROPERTY);
		if(s == null) {
			// Default to entry point if no load list given
			functionLoadList = new ArrayList<String>();
			functionLoadList.add(entryPoint);
		} else {
			setFunctionLoadList(s);
		}
	}
	
	public String getBinaryEvaFileName() {
		if(binaryEvaFileName == null) {
			System.err.println("No binary file given for Eva");
			System.exit(1);
		}
		return binaryEvaFileName;
	}
	
	public void setBinaryEvaFileName(String binaryEvaFileName) {
		this.binaryEvaFileName = binaryEvaFileName;
	}
	
	public String getBinaryFileName() {
		if(binaryFileName == null) {
			System.err.println("No binary file given");
			System.exit(1);
		}
		return binaryFileName;
	}
	
	public void setBinaryFileName(String binaryFileName) {
		this.binaryFileName = binaryFileName;
	}
	
	public String getEntryPoint() {
		return entryPoint;
	}
	
	public void setEntryPoint(String entryPoint) {
		this.entryPoint = entryPoint;
	}
	
	public String getBinaryReaderPath() {
		return binaryReaderPath;
	}
	
	public void setBinaryReaderPath(String binaryReaderPath) {
		this.binaryReaderPath = binaryReaderPath;
	}
	
	public List<String> getFunctionLoadList() {
		return functionLoadList;
	}
	
	public void setFunctionLoadList(List<String>newlist) {
		this.functionLoadList = newlist;
	}
	
	public void setFunctionLoadList(String list) {
			String fcns[] = list.split("[, ]+", 0);
			functionLoadList = Arrays.asList(fcns);
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getEmulatorClientDir() {
		return emulatorClientDir;
	}

	public void setEmulatorClientDir(String emulatorClientDir) {
		this.emulatorClientDir = emulatorClientDir;
	}

	public String getEmulatorServerDir() {
		return emulatorServerDir;
	}

	public void setEmulatorServerDir(String emulatorServerDir) {
		this.emulatorServerDir = emulatorServerDir;
	}

	/**
	 * @return the maxProgramSteps
	 */
	public int getMaxProgramSteps() {
		return maxProgramSteps;
	}

	/**
	 * @param maxProgramSteps the maxProgramSteps to set
	 */
	public void setMaxProgramSteps(int maxProgramSteps) {
		this.maxProgramSteps = maxProgramSteps;
	}

}
