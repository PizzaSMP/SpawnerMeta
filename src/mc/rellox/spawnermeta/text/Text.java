package mc.rellox.spawnermeta.text;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import mc.rellox.spawnermeta.commands.CommandManager;
import mc.rellox.spawnermeta.text.content.Content;

public final class Text {
	
	public static final String infinity = "" + '\u221E';
	
	public static void success(String success, Object... os) {
		CommandManager.success(Bukkit.getConsoleSender(), success, os);
	}
	
	public static void failure(String error, Object... os) {
		CommandManager.warn(Bukkit.getConsoleSender(), error, os);
	}
	
	public static List<String> toText(List<Content> list) {
		return list.stream()
				.map(Content::text)
				.collect(Collectors.toList());
	}

	public static String color(String hex) {
		String s = "�x";
		for(char c : hex.toCharArray()) s += "�" + c;
		return s;
	}
	
	public static String color(int rgb) {
		return color(String.format("%06x", rgb));
	}
	
	public static void clean(List<String> list) {
		boolean n = false;
		Iterator<String> it = list.iterator();
		while(it.hasNext() == true) {
			if(it.next().isEmpty() == true) {
				if(n == true) it.remove();
				n = true;
			} else n = false;
		}
		if(list.isEmpty() == false) {
			int last = list.size() - 1;
			if(list.get(last).isEmpty() == true)
				list.remove(last);
		}
	}
	
	public static List<String> fromLegacy(List<String> list) {
		return list.stream()
				.map(Text::fromLegacy)
				.collect(Collectors.toList());
	}
	
	public static String fromLegacy(String s) {
		StringBuilder sb = new StringBuilder();
		boolean l = false, h = false, i = false;
		String x = "";
		for(char c : s.toCharArray()) {
			if(c == '<') i = true;
			else if(c == '&') l = true;
			else if(c == '#') h = true;
			else {
				if(l == true) {
					String o = switch (c) {
					case 'a' -> "<#00ff00>"; case 'b' -> "<#00ffff>";
					case 'c' -> "<#ff0000>"; case 'd' -> "<#ff00ff>";
					case 'e' -> "<#ffff00>"; case 'f' -> "<#ffffff>";
					case '1' -> "<#000080>"; case '2' -> "<#008000>";
					case '3' -> "<#008080>"; case '4' -> "<#800000>";
					case '5' -> "<#800080>"; case '6' -> "<#ff8000>";
					case '7' -> "<#c4c4c4>"; case '8' -> "<#595959>";
					case '9' -> "<#0000ff>"; case '0' -> "<#000000>";
					case 'k' -> "<!obfuscated>"; case 'l' -> "<!bold>";
					case 'm' -> "<!strikethrough>"; case 'n' -> "<!underline>";
					case 'o' -> "<!italic>"; default -> "";
					};
					sb.append(o);
					l = false;
				} else if(h == true) {
					x += "" + c;
					if(x.length() >= 6) {
						sb.append("<#").append(x).append('>');
						x = "";
						h = false;
					}
				} else sb.append(c);
			}
			if(i == true && l == true || h == true) return s;
			i = false;
		}
		return sb.toString();
	}
	
}
