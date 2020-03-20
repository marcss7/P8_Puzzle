package edu.uoc.resolvers;

/*
   Esta clase representa cada una de las piezas en las que se divide
   el puzzle y que es necesario recolocar para resolverlo.
 */

public class Pieza {

    public int posicion;
    public int pvertical;
    public int phorizontal;

    public Pieza(int posicion, int pvertical, int phorizontal){
        this.posicion = posicion;
        this.pvertical = pvertical;
        this.phorizontal = phorizontal;
    }

}
