// Copyright (C) 2013 by Yan Huang <yhuang@cs.umd.edu>
// 					 and Xiao Shaun Wang <wangxiao@cs.umd.edu>

package flexsc;

import util.StopWatch;

public class Flag {
	public static boolean CountTime = true;
	public static StopWatch sw = new StopWatch(CountTime);
	public static boolean countIO = false;
	public static boolean FakeOT = false;
	public static boolean PreProcessOT = true;
	public static boolean offline =true;
	public static Mode mode = Mode.OFFLINE;
	

	public Flag() {
		// TODO Auto-generated constructor stub
	}
}
