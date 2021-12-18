package sample.DatabaseConnection.PrefStack;

import java.util.ArrayList;

public abstract class PrefSettings {
    protected final String variableCount = "variable_count";
    protected final String constantCount = "constant_count";
    protected final String isRepeat = "is_repeat";
    public void writeValues(String varCount, String constCount, String isRep) {

    }
    public ArrayList<String> readValues() {
        return null;
    }
}
