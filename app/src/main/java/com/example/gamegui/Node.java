package com.example.gamegui;

import java.util.Arrays;

public class Node {
    //public static final char FOLD = 'f', RAISE = 'r', CALL = 'c', DOUBLE = 'h', NUM_ACTIONS = 4;
    public static final char FOLD = 'f', RAISE = 'r', CALL = 'c', DOUBLE = 'h', NUM_ACTIONS = 3;
    StringBuilder infoSet;
    double[] regretSum = new double[NUM_ACTIONS], strategy = new double[NUM_ACTIONS],
            strategySum = new double[NUM_ACTIONS];

    public double[] getStrategy(double realizationWeight) {
        double normalizingSum = 0;
        for (int a = 0; a < NUM_ACTIONS; a++) {
            strategy[a] = regretSum[a] > 0 ? regretSum[a] : 0;
            normalizingSum += strategy[a];
        }
        for (int a = 0; a < NUM_ACTIONS; a++) {
            if (normalizingSum > 0)
                strategy[a] /= normalizingSum;
            else
                strategy[a] = 1.0 / NUM_ACTIONS;
            strategySum[a] += realizationWeight * strategy[a];
        }
        return strategy;
    }

    public double[] getAverageStrategy() {
        double[] avgStrategy = new double[NUM_ACTIONS];
        double normalizingSum = 0;
        for (int a = 0; a < NUM_ACTIONS; a++)
            normalizingSum += strategySum[a];
        for (int a = 0; a < NUM_ACTIONS; a++)
            if (normalizingSum > 0)
                avgStrategy[a] = strategySum[a] / normalizingSum;
            else
                avgStrategy[a] = 1.0 / NUM_ACTIONS;
        return avgStrategy;
    }

    public String toString() {
        return String.format("\n" + infoSet + Arrays.toString(getAverageStrategy()));
    }
}
