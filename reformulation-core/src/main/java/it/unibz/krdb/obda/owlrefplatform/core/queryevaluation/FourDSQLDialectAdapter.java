package it.unibz.krdb.obda.owlrefplatform.core.queryevaluation;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * SQL dialect adapter for 4D.
 */
public class FourDSQLDialectAdapter extends SQL99DialectAdapter {

    /**
     * TODO: check their value more carefully.
     */
    private static Map<Integer, String> SqlDatatypes;
    static {
        SqlDatatypes = new HashMap<>();
        SqlDatatypes.put(Types.INTEGER, "INT");
        SqlDatatypes.put(Types.BIGINT, "INT");
        SqlDatatypes.put(Types.DECIMAL, "DECIMAL");
        SqlDatatypes.put(Types.REAL, "REAL");
        SqlDatatypes.put(Types.FLOAT, "FLOAT");
        SqlDatatypes.put(Types.DOUBLE, "DOUBLE PRECISION");
        SqlDatatypes.put(Types.CHAR, "VARCHAR");
        SqlDatatypes.put(Types.VARCHAR, "VARCHAR");
        SqlDatatypes.put(Types.TIME, "TIMESTAMP");
        // Not found
        SqlDatatypes.put(Types.TIMESTAMP, "DATETIME");
        // Not found
        SqlDatatypes.put(Types.DATE, "DATE");
    }

    @Override
    public String sqlCast(String value, int type) {
        String strType = SqlDatatypes.get(type);
        if (strType != null) {
            return "CAST(" + value + " AS " + strType + ")";
        }
        throw new RuntimeException("Unsupported SQL type");
    }

    /**
     * 4D does not accept expressions like " NULL AS myVar ".
     */
    @Override
    public String sqlNull() {
        return "0";
    }

    /**
     * 4D does not handle properly expressions like " 1 AS myVarType " (assigns "null" instead of 1).
     */
    @Override
    public String sqlTypeCode(int code) {
        return String.format("CAST(%d as INT)", code);
    }
}
