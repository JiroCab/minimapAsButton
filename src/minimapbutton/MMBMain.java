package minimapbutton;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.*;
import arc.scene.*;
import arc.scene.event.Touchable;
import arc.scene.style.*;
import arc.scene.ui.ImageButton.*;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.*;
import mindustry.Vars;
import mindustry.core.GameState.*;
import mindustry.core.World;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.mod.Mod;
import mindustry.ui.Styles;

import static arc.Core.*;
import static mindustry.Vars.*;

public class MMBMain extends Mod {
    public Table mbbTable = new Table(), mbbCont = new Table();
    static public Seq<Drawable> tableStyles;
    final Seq<Integer> alignSides = Seq.with(Align.bottom, Align.bottomLeft, Align.bottomRight, Align.top, Align.topLeft, Align.topRight, Align.center, Align.left, Align.right);
    static public boolean selectScreen = true, prevCmdMode = false;
    public float screenSize = 0;

    public MMBMain(){

        Events.on(EventType.ClientLoadEvent.class, a -> {
           tableStyles =  Seq.with(Tex.buttonTrans, Tex.clear, Styles.black3, Tex.inventory, Tex.button, Tex.pane, Styles.black5, Styles.black6, Styles.black8, Styles.black9);
            buildButton();
            prevCmdMode = control.input.commandMode;
            MBBSettings.buildCategory();
        });
        Events.on(EventType.WorldLoadEvent.class, a ->{
            rebuildSubButtons();
        });

        Events.on(EventType.HostEvent.class, a ->{
            rebuildSubButtons();
        });

        Events.run(EventType.Trigger.update, () -> {
            if(!state.isPlaying()) return;
            if(control.input.commandMode != prevCmdMode){
                prevCmdMode = control.input.commandMode;
                rebuildSubButtons();
            }
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
        mbbCont.reset();
        mbbCont.name = "minimap-button-mbbCont";
        mbbTable.clear();
        mbbTable.reset();
        mbbTable.left();

        if(!settings.getBool("mbb-display"))return;

        mbbCont.align(alignSides.get(settings.getInt("mbb-side")));
        mbbCont.visible(() -> ui.hudfrag.shown && !ui.minimapfrag.shown() && !Core.settings.getBool("minimap"));
        mbbCont.moveBy(Core.settings.getInt("mbb-offset-x"), Core.settings.getInt("mbb-offset-y"));

        ImageButtonStyle style = Styles.clearNonei;

        mbbCont.table(t -> t.add(mbbTable)).row();
        mbbTable.table( tableStyles.get(settings.getInt("mhu-tab-style")), t -> {
            t.defaults().size(settings.getInt("mbb-size"));


            boolean unitc = settings.getBool("mbb-select-unit"), factoryc = settings.getBool("mbb-select-factory");
            if(control.input.commandMode && (unitc || factoryc)){
                if(settings.getBool("mbb-select-screen")) t.button(Icon.resize, Styles.clearNoneTogglei, () -> selectScreen = !selectScreen).update(z -> z.setChecked(!selectScreen));

                if(unitc) t.button(Icon.units, style, () -> {
                    control.input.selectedUnits.clear();
                    control.input.commandBuildings.clear();
                    for(var unit : player.team().data().units){
                        if(!selectScreen || (unit.isCommandable() && unit.within(camera.position,  Math.max(Core.camera.height, Core.camera.width) / 8f)))
                            control.input.selectedUnits.add(unit);
                    }
                }).scaling(Scaling.bounded);

                if(factoryc) t.button(Icon.crafting, style, () -> {
                    control.input.selectedUnits.clear();
                    control.input.commandBuildings.clear();
                    for(var build : player.team().data().buildings){
                        if(!build.block.commandable) continue;
                        if(!selectScreen || (build.within(camera.position, Math.max(Core.camera.height, Core.camera.width) / 8f)))
                            control.input.commandBuildings.add(build);
                    }
                }).scaling(Scaling.bounded);
            }else{

                if(settings.getBool("mbb-resync") && net.active()) t.button(Icon.refresh, style, () -> {
                    if(net.active() && net.client()) Call.sendChatMessage("/sync");
                    else Sounds.buttonClick.play();
                }).scaling(Scaling.bounded);
                if(settings.getBool("mbb-planet-map") && state.isCampaign() && (!net.active() || net.server())) t.button(Icon.planet, style, () -> ui.planet.toggle()).name("mbb-planet").scaling(Scaling.bounded);
                t.button(Icon.map, style, () -> ui.minimapfrag.toggle()).name("mbb-map").scaling(Scaling.bounded);
            }
        }).row();


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

    }


}

