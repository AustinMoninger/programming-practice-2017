import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;
 
/**
* This program simulates a simple process scheduler
* for a system with a single processor. Each process
* has a start time and a pre-specified duration. The
* simulateProcessor method reads this data from an
* input file, and then writes an output file indicating
* when and for how long each process is using the
* processor.
*/
public class OtherScheduler {
 
        /**
        * This internal class represents a single process. A name
        * is used to identify the process, and a start time and duration
        * are also stored. The simulation time starts at 0 and advances
        * upward from there.
        */
        private static class Process {
               private String name;
               private int timeToCompletion;
               private int startTime;
 
               /**
               * Creates new process with specified
               * start time, execution duration, and name.
               *
                * @param startTime Non-negative simulation time.
               * @param executionTime Positive number of time units
               *                                           that process requires on processor.
               * @param name Name that identifies process in output file.
               */
               public Process(int startTime, int executionTime, String name) {
                       if(executionTime <= 0) 
                               throw new IllegalArgumentException("Execution time must be positive: " + executionTime);
                       if(startTime < 0) 
                               throw new IllegalArgumentException("Start times cannot be negative: " + startTime);
                       
                       this.name = name;
                       this.timeToCompletion = executionTime;
                       this.startTime = startTime;
               }
 
               /**
               * When the process should appear in the ready queue in terms of
               * simulation time (starts from 0 and goes up from there)
               * @return Integer start time of process.
               */
               public int getStartTime() {
                       return startTime;
               }
 
               /**
               * Get name identifying the process
               * @return String name of process
               */
               public String getName(){
                       return name;
               }
 
               /**
               * Returns remaining number of time units that the
               * process must spend on the processor in order to complete.
               * @return Remaining execution time
               */
               public int getTimeRemaining(){
                       return timeToCompletion;
               }
 
               /**
               * Executes the process for a given number of time units, which
               * simply means subtracting the given time from the remaining
                * timeToCompletion. The provided time must be positive and
               * cannot exceed the timeToCompletion without causing an exception.
               *
                * @param processorTime Time to run process for. Must be positive and
               *                      less than or equal to timeToCompletion.
               */
               public void executeForTime(int processorTime) {
                       if(processorTime <= 0)  
                               throw new IllegalArgumentException("Time spent on processor must be positive: " + processorTime);
                       else if(processorTime > timeToCompletion)  
                               throw new IllegalArgumentException("Cannot execute for more time than is remaining: " + processorTime + " > " + timeToCompletion);
                       
                       timeToCompletion -= processorTime;
               }
        }
 
        /**
        * Creates a Scanner for a specified input file name and an output stream for
        * a specified output file name, and runs the processor simulation with a
        * given timeout period using the two files. All resources are closed
        * at the completion of the method.
        *
        * @param inputFile Name of file containing correctly formatted input data (see below)
        * @param outputFile Name of file that will contain output data
        * @param timeout How long a process can be on the processor before being kicked off
        * @throws FileNotFoundException If either the input or output files are not found/created
        */
        public static void simulateProcessor(String inputFile, String outputFile, int timeout) throws FileNotFoundException {
               Scanner processList = new Scanner(new File(inputFile));
               PrintStream processHistory = new PrintStream(new File(outputFile));
 
               simulateProcessor(processList, processHistory, timeout);
 
               processList.close();
               processHistory.close();
        }
 
        /**
        * Takes input file Scanner and PrintStream to the output file from the simulateProcessor
        * method above, along with the timeout, and actually runs the simulation. The input file
        * contains one line per process to be loaded, and three columns. The first column is the
        * start time, the second column is the execution duration, and all remaining text makes
        * up the process name. Note that process names may consist of multiple string tokens
        * separated by whitespace. Note that the start times of sequential processes in the file
        * must be non-decreasing (times increase, but ties are allowed).
        *
        * @param processList Scanner that reads the input file
        * @param processHistory PrintStream to the output file
        * @param timeout How long a process can be on the processor before being kicked off
        */
        private static void simulateProcessor(Scanner processList, PrintStream processHistory, int timeout) {
               CircularArrayQueue<Process> incomingQueue = new CircularArrayQueue<>(); 
               CircularArrayQueue<Process> readyQueue = new CircularArrayQueue<>(); 
 
               while(processList.hasNextLine()) { 
                       String name = ""; 
                       String oneLine = processList.nextLine(); 
                       Scanner lineScanner = new Scanner(oneLine); 
                       int startTime = lineScanner.nextInt(); 
                       int runTime = lineScanner.nextInt(); 
                       name = lineScanner.next();  
                       
                       while (lineScanner.hasNext())  
                               name = name + " " + lineScanner.next();
                       
                       lineScanner.close();
                       Process p = new Process(startTime, runTime, name); 
                       incomingQueue.enqueue(p); 
               }
 
               int timeToPrint = 0; 
               while(!incomingQueue.isEmpty() || !readyQueue.isEmpty()) { 
                       if(readyQueue.isEmpty()) { // will queue all of the steps and add idle time to processHistory
 
                               int originalTime = timeToPrint; 
                               readyQueue.enqueue(incomingQueue.dequeue()); 
                               while(!incomingQueue.isEmpty() && (incomingQueue.getFront()).getStartTime() == (readyQueue.getFront()).getStartTime()) 
                                       readyQueue.enqueue(incomingQueue.dequeue());
                               
                               Process temp = readyQueue.getFront();
                               int timeStart = temp.getStartTime();
                               timeToPrint = timeStart;
                               if (timeStart != 0) {
                                       System.out.println("CPU idle from time " + originalTime + " until time " + timeToPrint); // debug statement
                                       processHistory.println("CPU idle from time " + originalTime + " until time " + timeToPrint); // prints origalTime then time it will jump to (Idle time)
                               }
                               
                       } else { // if readyQueue is not empty - and a process needs to be run
                               Process toRun = readyQueue.dequeue(); 
                               int timeRemaining = toRun.getTimeRemaining(); 
                               
                               if(timeRemaining <= timeout) {
                                       toRun.executeForTime(timeRemaining); 
                                       processHistory.println("At time " + timeToPrint+ ": run \"" + toRun.getName() + "\" " + "for " + timeRemaining + " time units");
                                       timeToPrint += timeRemaining;
                                       while(!incomingQueue.isEmpty() && incomingQueue.getFront().getStartTime() < timeToPrint ) 
                                               readyQueue.enqueue(incomingQueue.dequeue());
                               } else {
                                       toRun.executeForTime(timeout); 
                                       processHistory.println("At time " + timeToPrint+ ": run \""+toRun.getName() + "\" " + "for " + timeout + " time units"); 
                                       timeToPrint += timeout; 
                                       while(!incomingQueue.isEmpty() && incomingQueue.getFront().getStartTime() < timeToPrint)  
                                               readyQueue.enqueue(incomingQueue.dequeue()); 
                                       readyQueue.enqueue(toRun); 
                               }
                       }
               }
        }
}
