package epermit.commons;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.google.gson.reflect.TypeToken;

import epermit.models.dtos.QuotaEvent;

@Converter
public class QuotaEventListConverter implements AttributeConverter<List<QuotaEvent>, String> {

    @Override
    public String convertToDatabaseColumn(List<QuotaEvent> list) {
        return GsonUtil.getGson().toJson(list);
    }

    @Override
    public List<QuotaEvent> convertToEntityAttribute(String json) {
        Type listType = new TypeToken<ArrayList<QuotaEvent>>() {
        }.getType();
        return GsonUtil.getGson().fromJson(json, listType);
    }
}