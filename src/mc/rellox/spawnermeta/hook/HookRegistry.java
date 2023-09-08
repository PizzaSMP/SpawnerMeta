package mc.rellox.spawnermeta.hook;

import java.util.ArrayList;
import java.util.List;

public class HookRegistry {
	
	private static final List<HookInstance<?>> HOOKS = new ArrayList<>();
	
    public static final HookEconomy ECONOMY = new HookEconomy();
    public static final HookWildStacker WILD_STACKER = new HookWildStacker();
    public static final HookWildTools WILD_TOOLS = new HookWildTools();
    public static final HookShopGUI SHOP_GUI = new HookShopGUI();
    public static final HookFlareTokens FLARE_TOKENS = new HookFlareTokens();
	
	public static void load() {
		HOOKS.add(ECONOMY);
		HOOKS.add(WILD_STACKER);
		HOOKS.add(WILD_TOOLS);
		HOOKS.add(SHOP_GUI);
		HOOKS.add(FLARE_TOKENS);
		HOOKS.forEach(HookInstance::load);
	}

}