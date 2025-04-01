package minimapbutton;

import mindustry.gen.*;

import static arc.Core.*;
import static mindustry.Vars.ui;

public class MBBSettings{
    private static final int offsetMinMax  = 200, sizeMinMax = 250;


    public static void buildCategory(){
        ui.settings.addCategory("@settings.mbb.settings", Icon.map, table ->{
            table.checkPref("mbb-display", true);

            table.checkPref("mbb-mini-map", true);
            table.checkPref("mbb-planet-map", false);
            table.checkPref("mbb-select-screen", false);
            table.checkPref("mbb-resync", false);

            table.sliderPref("mbb-side", 5, 0, 8, s -> bundle.get("mbb-side"+s));
            table.sliderPref("mbb-offset-x", 0, -offsetMinMax , offsetMinMax, String::valueOf);
            table.sliderPref("mbb-offset-y", 0, -offsetMinMax , offsetMinMax, String::valueOf);
            table.checkPref("mbb-show-stats", true);
            table.sliderPref("mbb-size", 65, 0 , sizeMinMax, String::valueOf);
        });
    }
}
