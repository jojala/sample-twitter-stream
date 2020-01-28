package com.jeffreyojala.twitterstream.statcontainers;

import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.OptionalDouble;

public class MovingAverage implements StatContainer {

    // storage for calculating moving averages over second, minute and hour using "shift" method
    // NOTE: Using classic synchronization over AtomicIntegerArray because I want to be able to lock
    //       the entire register, not just the individual writes. Individual locks give me more performance
    //       as the registers can lock independently but at the cost of a little more verbosity in code
    private Object subsecondLock = new Object();
    private Object secondLock = new Object();
    private Object minuteLock = new Object();
    private static final int SUB_SECOND_RESOLUTION = 10;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;
    private int[] countsBySubSecond = new int[SUB_SECOND_RESOLUTION];
    private int[] countsBySecond = new int[SECONDS_PER_MINUTE];
    private int[] countsByMinute = new int[MINUTES_PER_HOUR];

    private void threadSafeRegisterShift(Object lock, int[] register, int size) {
        synchronized (lock) {
            for (int s = size - 1; s > 0; s--) {
                register[s] = register[s - 1];
            }
            register[0] = 0;
        }
    }

    public void increment() {
        threadSafeRegisterIncrement(subsecondLock, countsBySubSecond);
        threadSafeRegisterIncrement(secondLock, countsBySecond);
        threadSafeRegisterIncrement(minuteLock, countsByMinute);
    }

    // NOTE: I am just shifting the registers on a period and calculating the average on request.
    //       I "could" also use a timer to have the averages pre-calculated. Its a trade off
    //       of response time (precalculated) vs average CPU load. I am assuming the volume of requests
    //       for average second, minute, hour tweets etc will be low and that the response time for calculating
    //       the average on the fly is "good enough"

    @Scheduled(fixedRate = 1000 / SUB_SECOND_RESOLUTION)
    private void shiftSubSecondRegisters() {
        threadSafeRegisterShift(subsecondLock, countsBySubSecond, SUB_SECOND_RESOLUTION);
    }

    @Scheduled(fixedRate = 1000)
    private void shiftSecondRegisters() {
        threadSafeRegisterShift(secondLock, countsBySecond, SECONDS_PER_MINUTE);
    }

    @Scheduled(fixedRate = 60 * 1000)
    private void shiftMinuteRegisters() {
        threadSafeRegisterShift(minuteLock, countsByMinute, MINUTES_PER_HOUR);
    }


    private double threadSafeAverage(Object lock, int[] register) {
        OptionalDouble avg;
        synchronized (lock) {
            avg = Arrays.stream(register)
                    .average();
        }

        if (!avg.isPresent()) {
            return 0;
        }
        return avg.getAsDouble();
    }

    private void threadSafeRegisterIncrement(Object lock, int[] register) {
        synchronized (lock) {
            register[0] = register[0] + 1;
        }
    }

    public double getAvgPerSecond() {
        return threadSafeAverage(subsecondLock, countsBySubSecond);
    }

    public double getAvgPerMinute() {
        return threadSafeAverage(secondLock, countsBySecond);
    }

    public double getAvgPerHour() {
        return threadSafeAverage(minuteLock, countsByMinute);
    }

    public void reset() {
        synchronized (subsecondLock) {
            countsBySubSecond = new int[SUB_SECOND_RESOLUTION];
        }
        synchronized (secondLock) {
            countsBySecond = new int[SECONDS_PER_MINUTE];
        }
        synchronized (minuteLock) {
            countsByMinute = new int[MINUTES_PER_HOUR];
        }
    }
}
