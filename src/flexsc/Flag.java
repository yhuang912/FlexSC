// Copyright (C) 2013 by Yan Huang <yhuang@cs.umd.edu>
// 					 and Xiao Shaun Wang <wangxiao@cs.umd.edu>

package flexsc;

import util.StopWatch;

public class Flag {
	public static boolean CountTime = true;
	public static StopWatch sw = new StopWatch(CountTime);

	public static OTMODE otMode = OTMODE.FAKEOT;
	public static Mode mode = Mode.REAL;
	public static boolean THREADEDIO = false;
	

	//preprocessot
	static public int PreProcessOTbufferSize = 1024*1024*2;
	static public int PreProcessOTRefillLength = 300000;

	//network
	public static boolean countIO = false;
	public static int NetowrkBufferSize = 1024*1024;
	public static int NetworkThreadedQueueSize = 1024*1024*10;


	public Flag() {
		// TODO Auto-generated constructor stub
	}
}
