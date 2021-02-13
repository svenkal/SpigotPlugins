package me.svenkal.creeperfun;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public class CreeperListener implements Listener {

    // store launched players by uuid
    private final Set<UUID> excludeFallDamage = new CopyOnWriteArraySet<>();

    // check that the player was damaged by a creeper
    private final Set<UUID> wasDamaged = new CopyOnWriteArraySet<>();

    private boolean isUnderSky(Player p) {
        return p.getLocation().getBlockY() >= p.getWorld().getHighestBlockYAt(p.getLocation());
    }

    private boolean isNearby(Location location, EntityType target, int cube) {
        List<Entity> nearby = (List<Entity>) location.getWorld().getNearbyEntities(location, cube, cube, cube);

        for (Entity e : nearby) {
            if (e.getType() == target) {
                return true;
            }
        }

        return false;
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {

        // check if a player is getting damaged
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        Player target = (Player) event.getEntity();

        // cancel fall damage if player uuid is in set
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (excludeFallDamage.contains(target.getUniqueId())) {
                event.setCancelled(true);

                excludeFallDamage.remove(target.getUniqueId());
                // play a sound
                target.playSound(target.getLocation(), Sound.ITEM_TOTEM_USE, 5F, 1F);

                return;
            }
        }

        // check that damage source is entity explosion
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            return;
        }

        // check that we are under the open sky
        if (!isUnderSky(target)) {
            return;
        }

        // this is just to prevent other entity explosion damage from launching the player
        if (isNearby(target.getLocation(), EntityType.CREEPER, 10)) {
            // explosion hits player, prevent fall and block damage
            excludeFallDamage.add(target.getUniqueId());
            wasDamaged.add(target.getUniqueId());

            // launch player somewhat high :)
            target.setVelocity(new Vector(0, Main.getInstance().getConfig().getInt("launch_velocity"), 0));

            // play sound and some particles
            target.playSound(target.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 5F, 1F);
            target.spawnParticle(Particle.EXPLOSION_HUGE, target.getLocation(), 5);

            // disable explosion damage (hp)
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onExplode(EntityExplodeEvent event) {

        // check that is a creeper explosion
        if (event.getEntity().getType() != EntityType.CREEPER) {
            return;
        }

        Creeper c = (Creeper) event.getEntity();

        // check that the creeper was ignited by flint and steel
        if (c.getTarget() == null) {
            return;
        }

        // check that the target is a  player
        if (c.getTarget().getType() != EntityType.PLAYER) {
            return;
        }

        Player target = (Player) c.getTarget();

        // check that player is under open sky
        if (!isUnderSky(target)) {
            return;
        }

        // check that the player would be damaged by the explosion
        if (wasDamaged.contains(target.getUniqueId())) {
            // stop the explosion from breaking blocks
            event.setCancelled(true);

            // remove player from set
            wasDamaged.remove(target.getUniqueId());
        }
    }
}

