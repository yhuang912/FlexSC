package test;

public class StopWatch {
    public static long ands = 0;
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
long counter = 0;
    public void startOT() {
        startTimeOT = System.nanoTime();
    }
    
    public void stopOT() {
         stopTimeOT = System.nanoTime();
         elapsedTimeOT += stopTimeOT - startTimeOT;
   }
    public void startOTIO() {
        startTimeOTIO = System.nanoTime();
    }
    
    public void stopOTIO() {
         stopTimeOTIO = System.nanoTime();
         elapsedTimeOTIO += stopTimeOTIO - startTimeOTIO;
   }
    public void startGC() {
        startTimeGC = System.nanoTime();
    }
    
    public void stopGC() {
         stopTimeGC = System.nanoTime();
         elapsedTimeGC += stopTimeGC - startTimeGC;
   }

    public void startGCIO() {
        startTimeGCIO = System.nanoTime();
    }
    
    public void stopGCIO() {
         stopTimeGCIO = System.nanoTime();
         elapsedTimeGCIO += stopTimeGCIO - startTimeGCIO;
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
    
    public void print(){
    	System.out.println("*************************\n"+
    elapsedTimeTotal/1000000000.0/counter+" "+
    (elapsedTimeGC-elapsedTimeGCIO)/1000000000.0/counter+" "+
    elapsedTimeGCIO/1000000000.0/counter+" "+
    (elapsedTimeOT-elapsedTimeOTIO)/1000000000.0/counter+" "+
    elapsedTimeOTIO/1000000000.0/counter+"\n");
    }
}
