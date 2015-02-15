// Copyright (C) 2013 by Yan Huang <yhuang@cs.umd.edu>
// 					 and Xiao Shaun Wang <wangxiao@cs.umd.edu>

package flexsc;

import util.StopWatch;

public class Flag {
	public static boolean CountTime = true;
	public static StopWatch sw = new StopWatch(CountTime);
	public static boolean countIO = false;
	public static OTMODE otMode = OTMODE.EXTENSIONOT;
	public static Mode mode = Mode.OFFLINEPREPARE;
	public static boolean THREADEDIO = true;
	

	public Flag() {
		// TODO Auto-generated constructor stub
	}
}
