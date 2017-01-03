package weasel;
import java.util.concurrent.BlockingQueue;

/**
* I don't like the way java.util.concurrent.ThreadPoolExecutor works. This is much simpler.
* It just takes the task off the queue and runs it.  Not that hard.
*
* Threads are expensive to create, objects are cheap.  We don't want to keep making threads.
*/

public class Worker extends Thread {
	private static int nextWorker=0;
	private BlockingQueue queue;
	private int number;

	public Worker(BlockingQueue q) {
		queue=q;
		number=nextWorker++;
	}

	public void run() {
		while (true) {
			try {
			 	Runnable task=(Runnable)queue.take();
			 	task.run();
			} catch (Exception x) {
				System.out.println("worker #"+number+" complains: "+x.getMessage());
			}
		}
	}
}