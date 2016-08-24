
package shine.tran.tubeview.businessobjects;

import android.os.AsyncTask;

/**
 * Can run multiple {@link AsyncTask}s in parallel.
 *
 * <p>To run in parallel, call the method {@link #executeInParallel(Object[])}.</p>
 */
public abstract class AsyncTaskParallel<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	/**
	 * @see #executeInParallel(Object[])
	 */
	public void executeInParallel() {
		executeInParallel(null);
	}


	/**
	 * Execute this task.  If other tasks are running, try to run this task in parallel.
	 *
	 * @param params	Parameters.
	 */
	public void executeInParallel(Params... params) {
		this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
	}

}
