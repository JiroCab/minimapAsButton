package minimapbutton;

import arc.Core;
import arc.Events;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.mod.Mod;
import mindustry.ui.Styles;

import static mindustry.Vars.*;

public class MMBMain extends Mod {
    public Table mbbTable = new Table();

    public MMBMain(){

        Events.on(EventType.ClientLoadEvent.class, a -> {
            buildButton();
            ui.settings.game.checkPref("mbb-planet-map", false, c -> rebuildSubButtons());
        });
        Events.on(EventType.WorldLoadEvent.class, a ->{
            rebuildSubButtons();
        });

    }

    public void buildButton (){
        Vars.ui.hudGroup.fill( cont ->{
            cont.name = "minimap-button-cont";
            cont.top().right();
            cont.visible(() -> ui.hudfrag.shown && !ui.minimapfrag.shown() && !Core.settings.getBool("minimap"));
            cont.row();
            rebuildSubButtons();
            cont.table(Tex.button, tab -> tab.add(mbbTable)).row();

            cont.table(table ->{
                table.label(() ->
                                (Core.settings.getBool("position") ? player.tileX() + "," + player.tileY() + "\n" : "") +
                                        (Core.settings.getBool("mouseposition") ? "[lightgray]" + World.toTile(Core.input.mouseWorldX()) + "," + World.toTile(Core.input.mouseWorldY()) : ""))
                        .visible(() -> Core.settings.getBool("position") || Core.settings.getBool("mouseposition"))
                        .touchable(Touchable.disabled)
                        .style(Styles.outlineLabel)
                        .name("mbb-position").growX().row();
            });
        });
    }

    public void rebuildSubButtons(){
        mbbTable.clear();
        mbbTable.reset();
        mbbTable.defaults().left();
        mbbTable.button(Icon.map, Styles.cleari, () -> ui.minimapfrag.toggle()).name("mbb-map").size(65f);
        if(Core.settings.getBool("mbb-planet-map"))mbbTable.button(Icon.planet, Styles.cleari, () -> ui.planet.toggle()).name("mbb-planet").size(65f);
    }


}

