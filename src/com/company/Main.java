package com.company;

import java.util.Scanner;

public class Main {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static void main(String[] args) {
    Graph graph = new Graph();
        Genetic genetic = new Genetic(graph);
        Scanner in = new Scanner(System.in);
        int menu = -1;
        while (menu != 0) {
            printOptions();
            menu = in.nextInt();
            switch (menu) { //menu
                case 1 -> {
                    graph = new Graph();
                    System.out.println("Podaj nazwę pliku: ");
                    String filename = in.next();
                    graph.copyFromFile(filename);
                    System.out.println(ANSI_RED + "Wczytano!" + ANSI_RESET);
                    genetic = new Genetic(graph);
                }
                case 2 -> {
                    System.out.println("Podaj limit czasowy w ms: ");
                    int timeLimit = in.nextInt();
                    genetic.timeLimit = timeLimit;
                }
                case 3 -> {
                    System.out.println("Wybierz rozmiar polulacji: ");
                    int populationSize = in.nextInt();
                    if (populationSize < 1) {
                        populationSize = 10;
                    }
                    genetic.populationSize = populationSize;
                }
                case 4 -> {
                    System.out.println("Wybierz współczynnik krzyżowania: ");
                    double crossRate = in.nextDouble();
                    if (crossRate > 1 || crossRate < 0) {
                        crossRate = 0.80;
                    }
                    genetic.crossRate = crossRate;
                }
                case 5 ->{
                    System.out.println("Wybierz współczynnik mutacji: ");
                    double mutationRate = in.nextDouble();
                    if (mutationRate > 1 || mutationRate < 0) {
                        mutationRate = 0.01;
                    }
                    genetic.mutationRate = mutationRate;
                }
                case 6 ->{
                            genetic.bestSolutionTime = Integer.MAX_VALUE;
                            genetic.bestSolution = Integer.MAX_VALUE;
                            genetic.solve();
                }
                default -> {
                    menu = 0;
                }
            }

        }
    }



    public static void printOptions() { //drukowanie opcji programu
        System.out.println("Wybierz opcję programu: ");
        System.out.println("1. Wczytaj graf z pliku");
        System.out.println("2. Kryterium stopu");
        System.out.println("3. Rozmiar populacji");
        System.out.println("4. Współczynnik krzyżowania");
        System.out.println("5. Współczynnik mutacji");
        System.out.println("6. Rozwiąż");
        System.out.println("7. Wyjdź");
    }
}
