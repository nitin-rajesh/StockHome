package sample.DatabaseConnection.PrefStack;

import java.util.prefs.Preferences;

public class PrefWriter extends PrefSettings{
    @Override
    public void writeValues(String varCount, String constCount, String isRep) {
        Preferences preferences = Preferences.userNodeForPackage(PrefSettings.class);
        preferences.put(variableCount,varCount);
        preferences.put(constantCount,constCount);
        preferences.put(isRepeat,isRep);
    }
}
