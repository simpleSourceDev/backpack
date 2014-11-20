package hr.simplesource.backpack;

import android.os.AsyncTask;

public class Task extends AsyncTask<Void, Void, Void> {

    private final Work mBeforeWork;
    private final Work mBackgroundWork;
    private final Work mAfterWork;
    private final Task mThenTask;

    private Task(Work before, Work background, Work after, Task thenTask) {
        this.mBeforeWork = before;
        this.mBackgroundWork = background;
        this.mAfterWork = after;
        this.mThenTask = thenTask;
    }

    @Override
    protected void onPreExecute() {
        if (mBeforeWork != null) mBeforeWork.work();
    }

    @Override
    protected Void doInBackground(Void... params) {
        mBackgroundWork.work();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mAfterWork != null) mAfterWork.work();
        if (mThenTask != null) mThenTask.execute();
    }

    public static class Builder {
        private Work beforeWork;
        private Work backgroundWork;
        private Work afterWork;
        private Task thenTask;

        public Builder(Work backgroundWork) {
            if (backgroundWork == null) {
                throw new NullPointerException("Background work must not be null");
            }

            this.backgroundWork = backgroundWork;
        }

        public Builder before(Work beforeWork) {
            if (beforeWork == null) {
                throw new IllegalArgumentException("BeforeWork cannot be null when trying to set the Work object");
            }

            this.beforeWork = beforeWork;
            return this;
        }

        public Builder after(Work afterWork) {
            if (afterWork == null) {
                throw new IllegalArgumentException("AfterWork cannot be null when trying to set the Work object");
            }

            this.afterWork = afterWork;
            return this;
        }

        public Builder then(Task task) {
            if (task == null) {
                throw new IllegalArgumentException("Task cannot be null when trying to set the Task object");
            }

            this.thenTask = task;
            return this;
        }

        public Task build() {
            return new Task(beforeWork, backgroundWork, afterWork, thenTask);
        }
    }
}
