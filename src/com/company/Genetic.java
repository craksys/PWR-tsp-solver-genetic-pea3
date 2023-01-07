package com.company;

import java.util.Arrays;
import java.util.Random;

public class Genetic {
    Random rand = new Random();
    Graph graph;
    int populationSize = 30;
    double crossRate = 0.8;
    double mutationRate = 0.01;
    int bestSolution = Integer.MAX_VALUE;
    int[] bestPath;
    long millisActualTime;
    long executionTime, bestSolutionTime;
    long timeLimit = 30000;


    public Genetic(Graph graph) {
        this.graph = graph;
    }

    public void solve() {
        int[][] population, nextPopulation;
        int[] ratedPopulation, permutation;
        int tournamentSize = 5;
        int index, p1, p2, p3;

        population = createFilledDoubleTab(populationSize, graph.size-1);
        nextPopulation = createFilledDoubleTab(populationSize, graph.size-1);
        permutation = createFilledTab(graph.size-1);

        for (int i = 0; i < populationSize; i++) {
            population[i] = generateRandomPath();
        }
        ratedPopulation = createFilledTab(populationSize);
        for (int i = 0; i < population.length; i++) {
            ratedPopulation[i] = calculatePathCost(population[i]);
        }

        if (ratedPopulation[findIndexOfMinElementFromTab(ratedPopulation)] < bestSolution) {
            bestSolutionTime = System.currentTimeMillis() - millisActualTime;
            bestSolution = ratedPopulation[findIndexOfMinElementFromTab(ratedPopulation)];
            bestPath = population[findIndexOfMinElementFromTab(ratedPopulation)].clone();
        }

        millisActualTime = System.currentTimeMillis();
        // Kolejne iteracje algorytmu
        while (true) {//zegar
            ratedPopulation = createFilledTab(populationSize);

            // Ocena jakości osobników
            for (int i = 0; i < population.length; i++) {
                ratedPopulation[i] = calculatePathCost(population[i]);
            }

            // Tworzenie nowej populacji na drodze selekcji
            for (int j = 0; j < populationSize; j++) {
                int result = Integer.MAX_VALUE;


                // Wybór najlepszego osobnika z turnieju
                for (int k = 0; k < tournamentSize; k++) {
                    index = rand.nextInt(populationSize);
                    if (ratedPopulation[index] < result) {
                        result = ratedPopulation[index];
                        permutation = population[index].clone();
                    }
                }
                nextPopulation[selectLastUnfilled(nextPopulation)] = permutation.clone();
            }

            // Wymiana pokoleń
            population = nextPopulation;
            nextPopulation = createFilledDoubleTab(populationSize, graph.size-1);//nextPopulation = new int[populationSize][graph.size];


            int rotate = populationSize - (int) (crossRate * (float) populationSize);
            rotate = rand.nextInt(rotate);
            // Krzyżowanie osobników
            for (int j = rotate; j < (int) (crossRate * (float) populationSize) + rotate; j += 2) { //do poprawy
                population[j] = orderCrossover(population[j], population[j + 1]);
                population[j + 1] = orderCrossover(population[j + 1], population[j]);
            }

            // Mutacje osobników
            for (int j = 0; j < (int) (mutationRate * (float) populationSize) + 1; j++) {
                do {
                    p1 = rand.nextInt(graph.size-1);
                    p2 = rand.nextInt(graph.size-1);
                    p3 = rand.nextInt(populationSize);
                } while (p1 == p2);
                swap(p1, p2, population[p3]);
            }

            for (int i = 0; i < population.length; i++) { //liczenie na nowo wartości
                ratedPopulation[i] = calculatePathCost(population[i]);
            }

            if (ratedPopulation[findIndexOfMinElementFromTab(ratedPopulation)] < bestSolution) {
                bestSolutionTime = System.currentTimeMillis() - millisActualTime;
                bestSolution = ratedPopulation[findIndexOfMinElementFromTab(ratedPopulation)];
                bestPath = population[findIndexOfMinElementFromTab(ratedPopulation)].clone();
            }

            executionTime = System.currentTimeMillis() - millisActualTime;
            if (executionTime > timeLimit) {
                System.out.println(bestSolution);
                System.out.print("0 ");
                for (int j : bestPath) {
                    System.out.print(j + " ");
                }
                System.out.println("0");
                System.out.println("Najlepsze rozwiązanie znaleziono w: " + bestSolutionTime + " ms");
                return;
            }
        }
    }


    private int calculatePathCost(int[] path) {
        int cost = 0;
        for (int i = 0; i < path.length - 1; i++) {
            cost += graph.matrix[path[i]][path[i + 1]];
        }
        cost += graph.matrix[0][path[0]];
        cost += graph.matrix[(path[(path.length - 1)])][0];
        return cost;
    }

    public int[] generateRandomPath() { //generowanie losowej ścieżki
        int[] randomPath = new int[graph.matrix.length-1];
        for (int i = 0; i < graph.matrix.length-1; i++) {
            randomPath[i] = i+1;
        }
        for (int i = 0; i < randomPath.length; i++) {//funkcja losująca kolejność
            int randomIndexToSwap = rand.nextInt(randomPath.length);//wszystkie oprocz 0
            int temp = randomPath[randomIndexToSwap];
            randomPath[randomIndexToSwap] = randomPath[i];
            randomPath[i] = temp;
        }

        return randomPath;
    }

    public int selectLastUnfilled(int[][] tab) {
        for (int i = 0; i < tab.length; i++) {
            if (tab[i][1] == -1) {
                return i;
            }
        }
        return -2;
    }

    public int[] createFilledTab(int tabSize) {
        int[] tab = new int[tabSize];
        Arrays.fill(tab, -1);
        return tab;
    }

    public int[][] createFilledDoubleTab(int populationSize, int graphSize) {
        int[][] tab = new int[populationSize][graphSize];
        for (int i = 0; i < populationSize; i++) {
            tab[i] = createFilledTab(graphSize);
        }
        return tab;
    }

    private void swap(int i, int j, int[] path) {
        int temp = path[i];
        path[i] = path[j];
        path[j] = temp;
    }


    public int findIndexOfMinElementFromTab(int[] tab) {
        int smallest = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < tab.length; i++) {
            if (tab[i] < smallest) {
                smallest = tab[i];
                index = i;
            }
        }
        return index;
    }

    public int[] myorderCrossover(int[] tab1, int[] tab2) {
        int startIndex = rand.nextInt(graph.size - 2);
        int endIndex = rand.nextInt(graph.size - 1);
        int actualChildIndex = 0;
        if (startIndex > endIndex) {
            int helper = endIndex;
            endIndex = startIndex;
            startIndex = helper;
        }
        int[] child = new int[graph.size-1];
        Arrays.fill(child, -1);

        for (int i = startIndex; i < endIndex + 1; i++) {
            child[i] = tab1[i];
        }

        actualChildIndex = endIndex+1;
        if(actualChildIndex > tab1.length-1){
            actualChildIndex =0;
        }

        for (int i = endIndex; i < tab2.length - 1; i++) {
            if(!isElementInTab(child, tab2[i])){
                child[actualChildIndex] = tab2[i];
                actualChildIndex++;
            }
            if(actualChildIndex > tab1.length-1){
                actualChildIndex = 0;
            }
        }
        for (int i = 0; isElementInTab(child,-1); i++) {
            if(!isElementInTab(child, tab2[i])){
                child[actualChildIndex] = tab2[i];
                actualChildIndex++;
                if(actualChildIndex > tab1.length-1){
                    actualChildIndex = 0;
                }
            }
        }

        /*for (int i = (endIndex + 1) % (graph.size-1); i < (endIndex + 1) % (graph.size-1) + (graph.size-1); i++) {
            int nextNumber = i;
            if (!isElementInTab(child, -1)) {
                break;
            }
            while (isElementInTab(child, tab2[nextNumber % (graph.size-1)])) {
                nextNumber++;
            }
            child[i % (graph.size-1)] = tab2[nextNumber % (graph.size-1)];
        }*/
        return child;
    }

    public int[] orderCrossover(int[] tab1, int[] tab2) {
        int startIndex = rand.nextInt(graph.size - 2);
        int endIndex = rand.nextInt(graph.size-1);
        if (startIndex > endIndex) {
            int helper = endIndex;
            endIndex = startIndex;
            startIndex = helper;
        }
        int[] child = new int[graph.size-1];
        Arrays.fill(child, -1);

        for (int i = startIndex; i < endIndex + 1; i++) {
            child[i] = tab1[i];
        }


        for (int i = (endIndex + 1) % (graph.size-1); i < (endIndex + 1) % (graph.size-1) + (graph.size-1); i++) {
            int nextNumber = i;
            if (!isElementInTab(child, -1)) {
                break;
            }
            while (isElementInTab(child, tab2[nextNumber % (graph.size-1)])) {
                nextNumber++;
            }
            child[i % (graph.size-1)] = tab2[nextNumber % (graph.size-1)];
        }
        return child;
    }
    public boolean isElementInTab(int[] tab, int element) {
        for (int i : tab) {
            if (i == element) {
                return true;
            }
        }
        return false;
    }



}
