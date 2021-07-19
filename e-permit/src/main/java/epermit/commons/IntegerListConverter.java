package epermit.commons;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
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
