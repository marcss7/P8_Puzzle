package edu.uoc.resolvers;

public class BBDDEstructure {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private BBDDEstructure() {}

    public static final String TABLE_NAME = "scores";
    public static final String _ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_POINTS = "points";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + BBDDEstructure.TABLE_NAME + " (" +
                    BBDDEstructure._ID + " INTEGER PRIMARY KEY," +
                    BBDDEstructure.COLUMN_DATE + " DATETIME PRIMARY," +
                    BBDDEstructure.COLUMN_LEVEL + " INT," +
                    BBDDEstructure.COLUMN_POINTS + " INT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
