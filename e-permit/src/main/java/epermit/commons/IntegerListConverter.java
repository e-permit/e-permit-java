package epermit.commons;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.google.gson.reflect.TypeToken;

@Converter
public class IntegerListConverter implements AttributeConverter<List<Integer>, String> {

  @Override
  public String convertToDatabaseColumn(List<Integer> list) {
    return GsonUtil.getGson().toJson(list);
  }

  @Override
  public List<Integer> convertToEntityAttribute(String json) {
    Type listType = new TypeToken<ArrayList<Integer>>(){}.getType();
    return  GsonUtil.getGson().fromJson(json, listType);
  }
}
