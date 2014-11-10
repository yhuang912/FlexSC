package test;

import flexsc.Party;

public class StopWatch {
    public long ands = 0;
    double startTimeOT = 0;
    double stopTimeOT = 0;
    double elapsedTimeOT = 0;

    double startTimeGC = 0;
    double stopTimeGC = 0;
    double elapsedTimeGC = 0;

    double startTimeTotal = 0;
    double stopTimeTotal = 0;
    double elapsedTimeTotal = 0;

    double startTimeOTIO = 0;
    double stopTimeOTIO = 0;
    double elapsedTimeOTIO = 0;

    double startTimeGCIO = 0;
    double stopTimeGCIO = 0;
    double elapsedTimeGCIO = 0;
    boolean countTime = true;//Flag.CountTime;
    public long counter = 0;
    public void startOT() {
    	if(countTime) {
//    		System.out.println("startOT");
    		startTimeOT = System.nanoTime();
    	}
    }
    
    public void stopOT() {
    	if(countTime) {
//    		System.out.println("stopOT");
	         stopTimeOT = System.nanoTime();
	         elapsedTimeOT += (stopTimeOT - startTimeOT);
    	}
   }
    public void startOTIO() {
    	if(countTime) {
//    		System.out.println("startOTIO");
    		startTimeOTIO = System.nanoTime();
    	}
    }
    
    public void stopOTIO() {
    	if(countTime) {
//    		System.out.println("stopOTIO");
	         stopTimeOTIO = System.nanoTime();
	         elapsedTimeOTIO += (stopTimeOTIO - startTimeOTIO);
    	}
   }
    public void startGC() {
    	if(countTime) {
    		startTimeGC = System.nanoTime();
    	}
    }
    
    public void stopGC() {
    	if(countTime) {
	         stopTimeGC = System.nanoTime();
	         elapsedTimeGC += (stopTimeGC - startTimeGC);
    	}
   }

    public void startGCIO() {
    	if(countTime) {
    		startTimeGCIO = System.nanoTime();
    	}
    }
    
    public void stopGCIO() {
    	if(countTime) {
	         stopTimeGCIO = System.nanoTime();
	         elapsedTimeGCIO += (stopTimeGCIO - startTimeGCIO);
    	}
   }
    public void startTotal() {
        startTimeTotal = System.nanoTime();
    }
    
    public double stopTotal() {
    	stopTimeTotal = System.nanoTime();
         elapsedTimeTotal += stopTimeTotal - startTimeTotal;
         return stopTimeTotal - startTimeTotal;
   }
    
    public void addCounter(){
    	++counter;
    }
    
    public void flush() {
        ands = 0;
        startTimeOT = 0;
        stopTimeOT = 0;
        elapsedTimeOT = 0;

        startTimeGC = 0;
        stopTimeGC = 0;
        elapsedTimeGC = 0;

        startTimeTotal = 0;
        stopTimeTotal = 0;
        elapsedTimeTotal = 0;

        startTimeOTIO = 0;
        stopTimeOTIO = 0;
        elapsedTimeOTIO = 0;

        startTimeGCIO = 0;
        stopTimeGCIO = 0;
        elapsedTimeGCIO = 0;
        counter = 0;

    }
    
    public void print(){
    	System.out.println("Total Time \t GC CPU Time\t GCIO Time\t OTCPU Time\t OTIO Time\n");
    	System.out.println(elapsedTimeTotal/1000000000.0+"\t"+
    (elapsedTimeGC-elapsedTimeGCIO)/1000000000.0+"\t"+
    elapsedTimeGCIO/1000000000.0+" "+
    (elapsedTimeOT-elapsedTimeOTIO)/1000000000.0+"\t"+
    elapsedTimeOTIO/1000000000.0+"\n");
    }

    public void printGC(int machineId, int totalMachines, int inputLength, long commTime, Party party) {
    	System.out.println(machineId + "," + totalMachines + ","  + inputLength + "," + (elapsedTimeGC-elapsedTimeGCIO)/1000000000.0 + ",GC CPU," + party);
    	System.out.println(machineId + "," + totalMachines + ","  + inputLength + "," + elapsedTimeGCIO/1000000000.0 + ",GE IO," + party);
    	System.out.println(machineId + "," + totalMachines + ","  + inputLength + "," + commTime/1000000000.0 + ",GG IO," + party);
    }

    public void printOT(int machineId, int totalMachines, int inputLength, Party party) {
    	System.out.println(machineId + "," + totalMachines + ","  + inputLength + "," + (elapsedTimeOT-elapsedTimeOTIO)/1000000000.0 + ",OT CPU," + party);
    	System.out.println(machineId + "," + totalMachines + ","  + inputLength + "," + elapsedTimeOTIO/1000000000.0 + ",OT IO," + party);
    }
}
