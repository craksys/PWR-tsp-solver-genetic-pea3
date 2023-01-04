package com.company;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Genetic {
    Random rand = new Random();
    Graph graph;
    int size;
    int populationSize = 10;
    int crossRate = 1;
    int mutationRate = 1;
    int result;


    public Genetic(Graph graph){
        this.graph = graph;
    }

    public int apply() {
        int xd = 0;
        int[][] population, nextPopulation;
        int[] fitness, permutation;
        int tournamentSize = 5;
        int index, p1, p2;

        population = createFilledDoubleTab(populationSize, graph.size);
        nextPopulation = createFilledDoubleTab(populationSize, graph.size);
        permutation = createFilledTab(graph.size);
        fitness = createFilledTab(populationSize);

        for (int i = 0; i < populationSize; i++) {
            population[i] = generateRandomPath();
        }


        // Kolejne iteracje algorytmu
        while (xd < 1000) {//zegar
            fitness = createFilledTab(populationSize);

            // Ocena jakości osobników
            for(int i =0; i<population.length; i++){
                fitness[i] = calculatePathCost(population[i]);
            }


            // Tworzenie nowej populacji na drodze selekcji
            for (int j = 0; j < populationSize; j++) {
                result = Integer.MAX_VALUE;

                // Wybór najlepszego osobnika z turnieju
                for (int k = 0; k < tournamentSize; k++) {
                    index = rand.nextInt(populationSize);

                    if (fitness[index] < result) {
                        result = fitness[index];
                        permutation = createFilledTab(graph.size);
                        permutation = population[index];
                    }
                }
                nextPopulation[selectLastUnfilled(nextPopulation)] = permutation.clone();
            }

            // Wymiana pokoleń
            population = nextPopulation;
            nextPopulation = createFilledDoubleTab(populationSize, graph.size);//nextPopulation = new int[populationSize][graph.size];


            // Krzyżowanie osobników
            for (int j = 0; j < (int) (crossRate * (float) populationSize); j += 2) {
                /*do {
                    p1 = rand.nextInt(populationSize);
                    p2 = rand.nextInt(populationSize);
                } while (p1 == p2); //!(p1 - p2) było
                */ //dla mnie nie ma sensu ta część

                partiallyCrossoverGTP(population[j], population[j+1]);
            }

            // Mutacje osobników
            for (int j = 0; j < (int) (mutationRate * (float) populationSize); j++) {
                do {
                    p1 = rand.nextInt(graph.size);
                    p2 = rand.nextInt(graph.size);
                } while (p1 == p2);
                swap(p1,p2,population[j]);
            }
            xd++;
        }
        result = fitness[findIndexOfMinElementFromTab(fitness)];
        return result;
    }

/*
    public void partiallyCrossover(int[] parent1, int[] parent2) {
        int[] desc1 = new int[size];
        int[] desc2 = new int[size];
        int[] map1 = new int[size];
        int[] map2 = new int[size];
        int begin, end, temp;

        do {
            begin = rand.nextInt(size);
            end = rand.nextInt(size);
        } while ((0 >= (end - begin)) || !begin || !(end - (size - 1)));

        for (int i = begin; i <= end; i++) {
            desc1[i] = parent1[i];
            desc2[i] = parent2[i];
            map1[parent1[i]] = parent2[i];
            map2[parent2[i]] = parent1[i];
        }

        for (int i = 0; i < size; i++) {
            if (desc1[i] == -1) {
                if (!isInPath(parent2[i], begin, end, desc1))
                    desc1[i] = parent2[i];
                else {
                    temp = parent2[i];

                    do {
                        temp = map1[temp];
                    } while (isInPath(temp, begin, end, desc1));

                    desc1[i] = temp;
                }
            }
        }

        for (int i = 0; i < size; i++) {
            if (desc2[i] == -1) {
                if (!isInPath(parent1[i], begin, end, desc2))
                    desc2[i] = parent1[i];
                else {
                    temp = parent1[i];

                    do {
                        temp = map2[temp];
                    } while (isInPath(temp, begin, end, desc2));

                    desc2[i] = temp;
                }
            }
        }

        parent1 = new int[size];
        parent2 = new int[size];
        parent1 = desc1;
        parent2 = desc2;
    }*/
    public boolean isInPath(int element, int begin, int end, int[] path) {
        for (int i = begin; i <= end; i++) {
            if (element == path[i])
                return true;
        }
        return false;
    }

    private int calculatePathCost(int[] path) {
        int cost = 0;
        for (int i = 0; i < path.length - 1; i++) {
            cost += graph.matrix[path[i]][path[i + 1]];
        }
        cost += graph.matrix[(path[(path.length - 1)])][path[0]];
        return cost;
    }

    public int[] generateRandomPath() { //generowanie losowej ścieżki
        int[] randomPath = new int[graph.matrix.length];
        for (int i = 0; i < graph.matrix.length; i++) {
            randomPath[i] = i;
        }
        for (int i = 1; i < randomPath.length; i++) {//funkcja losująca kolejność
            int randomIndexToSwap = rand.nextInt(randomPath.length - 1) + 1;//wszystkie oprocz 0
            int temp = randomPath[randomIndexToSwap];
            randomPath[randomIndexToSwap] = randomPath[i];
            randomPath[i] = temp;
        }

        return randomPath;
    }

    public int selectLastUnfilled(int[][] tab){
        for(int i = 0; i < tab.length; i++){
            if(tab[i][1] == -1){
                return i;
            }
        }
        return -2;
    }

    public int[] createFilledTab(int tabSize){
        int[] tab = new int[tabSize];
        Arrays.fill(tab, -1);
        return tab;
    }

    public int[][] createFilledDoubleTab(int populationSize, int graphSize){
        int[][] tab = new int[populationSize][graphSize];
        for(int i = 0; i < populationSize; i++){
            tab[i] = createFilledTab(graphSize);
        }
        return tab;
    }

    private void swap(int i, int j, int[] path) {
        int temp = path[i];
        path[i] = path[j];
        path[j] = temp;
    }

    public void partiallyCrossoverGTP(int[] tab1, int[] tab2) {
        int[][] crossedTabs = new int[2][tab1.length];

        // Choose a random crossover point
        int crossoverPoint = ThreadLocalRandom.current().nextInt(tab1.length);

        // Copy the first half of the first array and the second half of the second array
        for (int i = 0; i < crossoverPoint; i++) {
            crossedTabs[0][i] = tab1[i];
        }
        for (int i = crossoverPoint; i < tab1.length; i++) {
            crossedTabs[0][i] = tab2[i];
        }

        // Copy the first half of the second array and the second half of the first array
        for (int i = 0; i < crossoverPoint; i++) {
            crossedTabs[1][i] = tab2[i];
        }
        for (int i = crossoverPoint; i < tab1.length; i++) {
            crossedTabs[1][i] = tab1[i];
        }
    }

    public int findIndexOfMinElementFromTab(int[] tab){
        int smallest = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < tab.length; i++) {
            if (tab[i] < smallest){
                smallest = tab[i];
                index = i;
            }
        }
        return index;
    }
}
