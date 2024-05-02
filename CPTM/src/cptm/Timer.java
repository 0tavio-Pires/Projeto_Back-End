package cptm;

import java.util.Timer;
import java.util.TimerTask;

public class Timer {
	
	private Estacao estacao;
	
    public void startTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            	estacao.isOcupadoLadoA() = !estacao.isOcupadoLadoA();
                System.out.println("Status de ocupação: " + ocupadoLadoA);
            }
        }, 0, 3000); // Schedule the task to run every 3 seconds
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.startTimer(); // Start the timer
    }
}
