package scripts;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Players;

import java.util.concurrent.Callable;

public class Chop extends Task<ClientContext> {
    private String treeName;

    Chop(ClientContext ctx, String treeName) {
        super(ctx);
        this.treeName = treeName;
    }

    @Override
    public boolean activate() {
        String treeName = tofuFuncs.Tools.getTreeName(ctx);
        return !ctx.inventory.isFull()
                && !ctx.objects.select().name(treeName).isEmpty()
                && ctx.players.local().animation() == -1;
    }

    @Override
    public void execute() {
        //Selected item bug check
        if (ctx.inventory.selectedItemIndex() != -1) {
            ctx.movement.step(ctx.players.local().tile()); //Click something to deselect item
        }
        GameObject tree = ctx.objects.nearest().poll();
        //if(tree.tile().matrix(ctx).reachable()) {
            if (tree.inViewport()) {
                    tree.interact("Chop");
                    Condition.sleep(Random.nextGaussian(500, 1500, 1000, 100)); //wait a bit to allow our click to register
                Condition.wait(() -> (ctx.players.local().animation() == -1 && !ctx.players.local().inMotion()), 100, 15); //wait a bit to allow our click to register

            } else {
                if(reachable(tree)) {
                    ctx.movement.step(tree);
                    Condition.sleep(Random.nextGaussian(500, 1500, 1000, 100)); //wait a bit to allow our click to register
                    Condition.wait(() -> (ctx.players.local().animation() == -1), 50, 15); //wait a bit to allow our click to register
                }
            }
        //}
    }

    private boolean reachable(GameObject object) { //From coma
        final Tile t = object.tile();
        final Tile[] tiles = {t.derive(-1, 0), t.derive(1, 0), t.derive(0, -1), t.derive(0, 1)};
        for (Tile tile : tiles) {
            if (tile.matrix(ctx).reachable()) {
                return true;
            }
        }
        return false;
    }

    private class Inventory {
    }
}
