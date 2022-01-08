package sample.DatabaseConnection.PrefStack;

import java.util.prefs.Preferences;

public class ModeSetter extends PrefSettings{
    static String stockMode = "StockHome";
    static String cryptoMode = "CryptoBase";
    static String mode = "OperationMode";

    private ModeSetter(){
    }

    public static void switchMode(){
        Preferences preferences = Preferences.userNodeForPackage(PrefSettings.class);
        if(preferences.get(mode,stockMode).equals(stockMode))
            preferences.put(mode,cryptoMode);
        else
            preferences.put(mode,stockMode);

        //System.out.println(preferences.get(mode,stockMode));
    }

    public static String getMode(){
        Preferences preferences = Preferences.userNodeForPackage(PrefSettings.class);
        return preferences.get(mode,stockMode);
    }
}
