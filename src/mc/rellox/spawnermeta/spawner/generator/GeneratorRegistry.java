package mc.rellox.spawnermeta.spawner.generator;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import mc.rellox.spawnermeta.SpawnerMeta;
import mc.rellox.spawnermeta.api.spawner.IGenerator;
import mc.rellox.spawnermeta.utility.reflect.Reflect.RF;

public final class GeneratorRegistry implements Listener {
	
	private static final Map<World, SpawnerWorld> SPAWNERS = new HashMap<>();
	private static boolean refresh = false;
	
	public static void initialize() {
		Bukkit.getPluginManager().registerEvents(new GeneratorRegistry(), SpawnerMeta.instance());
		load();
		run();
	}
	
	private static void run() {
		new BukkitRunnable() {
			int t = 0;
			@Override
			public void run() {
				SPAWNERS.values().forEach(SpawnerWorld::tick);
				if(++t > 600) {
					t = 0;
					SPAWNERS.values().forEach(SpawnerWorld::reduce);
				}
				if(refresh == true) SPAWNERS.values().forEach(SpawnerWorld::refresh);
			}
		}.runTaskTimer(SpawnerMeta.instance(), 20, 1);
	}
	
	public static void load() {
		try {
			Bukkit.getWorlds().forEach(world -> get(world).load());
		} catch (Exception e) {
			RF.debug(e);
		}
	}
	
	public static void reload() {
		try {
			clear();
			load();
		} catch (Exception e) {
			RF.debug(e);
		}
	}
	
	private static SpawnerWorld get(World world) {
		SpawnerWorld sw = SPAWNERS.get(world);
		if(sw == null) SPAWNERS.put(world, sw = new SpawnerWorld(world));
		return sw;
	}
	
	public static void put(Block block) {
		get(block.getWorld()).put(block);
	}
	
	public static IGenerator get(Block block) {
		return get(block.getWorld()).get(block);
	}
	
	public static void update(Block block) {
		IGenerator generator = get(block.getWorld()).get(block);
		if(generator != null) {
			generator.update();
			generator.valid();
			generator.rewrite();
		}
		SpawningManager.unlink(block);
	}
	
	public static void update() {
		SPAWNERS.values().forEach(SpawnerWorld::update);
	}
	
	public static void remove(Block block) {
		IGenerator generator = get(block.getWorld()).raw(block);
		if(generator != null) generator.remove(false);
	}
	
	public static void clear() {
		SPAWNERS.values().forEach(SpawnerWorld::clear);
		SPAWNERS.clear();
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	private void onWorldLoad(WorldLoadEvent event) {
		World world = event.getWorld();
		SpawnerWorld sw = new SpawnerWorld(world);
		SPAWNERS.put(world, sw);
		sw.load();
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onWorldUnload(WorldUnloadEvent event) {
		World world = event.getWorld();
		SPAWNERS.remove(world);
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onChunkLoad(ChunkLoadEvent event) {
		get(event.getWorld()).load(event.getChunk());
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onChunkUnload(ChunkUnloadEvent event) {
		get(event.getWorld()).unload(event.getChunk());
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onJoin(PlayerJoinEvent event) {
		refresh = true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onQuit(PlayerQuitEvent event) {
		refresh = true;
	}
	
}
