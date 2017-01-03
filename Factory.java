package weasel;
import java.util.concurrent.*;

/**
* I don't like the way java.util.concurrent.ExecutorService works. This is much simpler.
* It hires workers and executes tasks by putting them on the queue.
* The next available worker does it.
*
* I call it Factory because it does stuff with multiple Workers.
*/

public class Factory implements Executor {
	private BlockingQueue queue;

	public Factory(int numWorkers) {
		//start up the queue
		queue=new LinkedBlockingQueue(100);

		//hire workers
		for (int i=0;i<numWorkers;i++) {
			hire();
		}
	}

	public void execute(Runnable task) {
		try {
			queue.put(task);
		} catch (InterruptedException x) {
			System.out.println(x.getMessage());
		}
	}

	//hire a new worker
	public void hire() {
		Worker w=new Worker(queue);
		w.start();
	}
}