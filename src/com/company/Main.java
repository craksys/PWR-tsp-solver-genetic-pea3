package com.company;

public class Main {

    public static void main(String[] args) {
    Graph graph = new Graph();
    graph.copyFromFile("ftv47.atsp");
    Genetic genetic = new Genetic(graph);
    int best = Integer.MAX_VALUE;
    int temp;
    for(int i =0 ; i<1000; i++) {
        temp = genetic.apply();
        if(temp < best){
            best = temp;
        }
    }
        System.out.println(best);
    }
}
