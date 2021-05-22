package epermit.utils;

import java.lang.reflect.Type;
import java.util.Map;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

public class GsonUtil {
    public static Gson getGson() {
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    public static <T> T fromMap(Map<String, Object> payload, Class<T> objectClass) {
        JsonElement element = getGson().toJsonTree(payload);
        return getGson().fromJson(element, objectClass);
    }

    public static Map<String, Object> toMap(Object obj) {
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        return getGson().fromJson(getGson().toJson(obj), mapType);
    }
}
