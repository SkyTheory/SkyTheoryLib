package skytheory.lib.util;

import net.minecraft.client.resources.I18n;

public class TextUtils {

	public static String format(String key) {
		return I18n.format(key);
	}

	public static String format(String key, Object... obj) {
		return I18n.format(key, obj);
	}
}
