package net.galaxygaming.dispenser.task;

import net.galaxygaming.dispenser.GameDispenser;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * This class is provided as an easy way to handle scheduling tasks.
 */
public abstract class GameRunnable implements Runnable {
    private int taskId = -1;

    /**
     * Attempts to cancel this task.
     *
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized void cancel() throws IllegalStateException {
        Bukkit.getScheduler().cancelTask(getTaskId());
    }

    /**
     * Schedules this in the Bukkit scheduler to run on next tick.
     *
     * @return a BukkitTask that contains the id number
     * @throws IllegalStateException if this was already scheduled
     */
    public synchronized BukkitTask runTask() throws IllegalArgumentException, IllegalStateException {
        checkState();
        return setupId(Bukkit.getScheduler().runTask(GameDispenser.getInstance(), this));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this in the Bukkit scheduler to run asynchronously.
     *
     * @return a BukkitTask that contains the id number
     * @throws IllegalStateException if this was already scheduled
     */
    public synchronized BukkitTask runTaskAsynchronously() throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Bukkit.getScheduler().runTaskAsynchronously(GameDispenser.getInstance(), this));
    }

    /**
     * Schedules this to run after the specified number of server ticks.
     *
     * @param delay the ticks to wait before running the task
     * @return a BukkitTask that contains the id number
     * @throws IllegalStateException if this was already scheduled
     */
    public synchronized BukkitTask runTaskLater(long delay) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Bukkit.getScheduler().runTaskLater(GameDispenser.getInstance(), this, delay));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this to run asynchronously after the specified number of
     * server ticks.
     *
     * @param delay the ticks to wait before running the task
     * @return a BukkitTask that contains the id number
     * @throws IllegalStateException if this was already scheduled
     */
    public synchronized BukkitTask runTaskLaterAsynchronously(long delay) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Bukkit.getScheduler().runTaskLaterAsynchronously(GameDispenser.getInstance(), this, delay));
    }

    /**
     * Schedules this to repeatedly run until cancelled, starting after the
     * specified number of server ticks.
     *
     * @param delay the ticks to wait before running the task
     * @param period the ticks to wait between runs
     * @return a BukkitTask that contains the id number
     * @throws IllegalStateException if this was already scheduled
     */
    public synchronized BukkitTask runTaskTimer(long delay, long period) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Bukkit.getScheduler().runTaskTimer(GameDispenser.getInstance(), this, delay, period));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Bukkit. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this to repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param delay the ticks to wait before running the task for the first
     *     time
     * @param period the ticks to wait between runs
     * @return a BukkitTask that contains the id number
     * @throws IllegalStateException if this was already scheduled
     */
    public synchronized BukkitTask runTaskTimerAsynchronously(long delay, long period) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(Bukkit.getScheduler().runTaskTimerAsynchronously(GameDispenser.getInstance(), this, delay, period));
    }

    /**
     * Gets the task id for this runnable.
     *
     * @return the task id that this runnable was scheduled as
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized int getTaskId() throws IllegalStateException {
        final int id = taskId;
        if (id == -1) {
            throw new IllegalStateException("Not scheduled yet");
        }
        return id;
    }

    private void checkState() {
        if (taskId != -1) {
            throw new IllegalStateException("Already scheduled as " + taskId);
        }
    }

    private BukkitTask setupId(final BukkitTask task) {
        this.taskId = task.getTaskId();
        return task;
    }
}