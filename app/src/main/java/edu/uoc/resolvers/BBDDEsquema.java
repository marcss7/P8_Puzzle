package edu.uoc.resolvers;

/*
    Esta clase define el esquema de la base de datos.
 */
public class BBDDEsquema {

    private BBDDEsquema() {}

    public static final String NOMBRE_TABLA = "puntuaciones";
    public static final String _ID = "id";
    public static final String COLUMNA_FECHA = "fecha";
    public static final String COLUMNA_NIVEL = "nivel";
    public static final String COLUMNA_PUNTOS = "puntos";

    public static final String SQL_CREAR_ENTRADAS =
            "CREATE TABLE " + BBDDEsquema.NOMBRE_TABLA + " (" +
                    BBDDEsquema._ID + " INTEGER PRIMARY KEY," +
                    BBDDEsquema.COLUMNA_FECHA + " TEXT," +
                    BBDDEsquema.COLUMNA_NIVEL + " INT," +
                    BBDDEsquema.COLUMNA_PUNTOS + " FLOAT)";

    public static final String SQL_BORRAR_ENTRADAS =
            "DROP TABLE IF EXISTS " + NOMBRE_TABLA;
}
