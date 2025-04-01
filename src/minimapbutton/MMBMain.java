package minimapbutton;

import arc.Core;
import arc.Events;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.*;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.mod.Mod;
import mindustry.ui.Styles;

import static arc.Core.*;
import static mindustry.Vars.*;

public class MMBMain extends Mod {
    public Table mbbTable = new Table(), mbbCont = new Table();
    final Seq<Integer> alignSides = Seq.with(Align.bottom, Align.bottomLeft, Align.bottomRight, Align.top, Align.topLeft, Align.topRight, Align.center, Align.left, Align.right);

    public MMBMain(){

        Events.on(EventType.ClientLoadEvent.class, a -> {
            buildButton();
            MBBSettings.buildCategory();
        });
        Events.on(EventType.WorldLoadEvent.class, a ->{
            rebuildSubButtons();
        });

        Events.on(EventType.HostEvent.class, a ->{
            rebuildSubButtons();
        });

    }

    public void buildButton (){
        Vars.ui.hudGroup.fill( cont -> {
            mbbCont.clear();
            mbbCont = cont;
            rebuildSubButtons();
        });
    }

    public void rebuildSubButtons(){
        mbbCont.clear();
        mbbCont.name = "minimap-button-mbbCont";
        mbbTable.clear();
        mbbTable.reset();

        if(!settings.getBool("mbb-display"))return;

        mbbTable.defaults().size(settings.getInt("mbb-size")).left();

        mbbCont.align(alignSides.get(settings.getInt("mbb-side")));
        mbbCont.visible(() -> ui.hudfrag.shown && !ui.minimapfrag.shown() && !Core.settings.getBool("minimap"));
        mbbCont.moveBy(Core.settings.getInt("mbb-offset-x"), Core.settings.getInt("mbb-offset-y"));


        mbbCont.table(Tex.button, tab -> tab.add(mbbTable)).row();

        if(settings.getBool("mbb-show-stats")){
            mbbCont.table(table ->{
                table.label(() ->
                (Core.settings.getBool("position") ? player.tileX() + "," + player.tileY() + "\n" : "") +
                (Core.settings.getBool("mouseposition") ? "[lightgray]" + World.toTile(Core.input.mouseWorldX()) + "," + World.toTile(Core.input.mouseWorldY()) : ""))
                .visible(() -> Core.settings.getBool("position") || Core.settings.getBool("mouseposition"))
                .touchable(Touchable.disabled)
                .style(Styles.outlineLabel)
                .name("mbb-position").growX().row();
            });
        }



        if(Core.settings.getBool("mbb-select-screen"))mbbTable.button(Icon.units, Styles.cleari, () -> {
            control.input.selectedUnits.clear();
            control.input.commandBuildings.clear();
            for(var unit : player.team().data().units){
                if(unit.isCommandable() && unit.within(Core.camera.position, Math.max(Core.camera.height, Core.camera.width)))
                    control.input.selectedUnits.add(unit);
            }
        }).scaling(Scaling.bounded);
        if(Core.settings.getBool("mbb-resync") && net.active())mbbTable.button(Icon.refresh, Styles.cleari, () -> {
            if(net.active() && net.client())Call.sendChatMessage("/sync");
            else Sounds.buttonClick.play();
        }).scaling(Scaling.bounded);
        if(Core.settings.getBool("mbb-planet-map") && state.isCampaign() && (!net.active() || net.server()))mbbTable.button(Icon.planet, Styles.cleari, () -> ui.planet.toggle()).name("mbb-planet").scaling(Scaling.bounded);
        mbbTable.button(Icon.map, Styles.cleari, () -> ui.minimapfrag.toggle()).name("mbb-map").scaling(Scaling.bounded);
    }


}

