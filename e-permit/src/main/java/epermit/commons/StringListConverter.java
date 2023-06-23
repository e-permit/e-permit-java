package epermit.commons;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.google.gson.reflect.TypeToken;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

  @Override
  public String convertToDatabaseColumn(List<String> list) {
    return GsonUtil.getGson().toJson(list);
  }

  @Override
  public List<String> convertToEntityAttribute(String json) {
    Type listType = new TypeToken<ArrayList<String>>(){}.getType();
    return  GsonUtil.getGson().fromJson(json, listType);
  }
}
