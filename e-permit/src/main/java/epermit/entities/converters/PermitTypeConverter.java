package epermit.entities.converters;

import java.util.stream.Stream;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import epermit.models.enums.PermitType;

@Converter(autoApply = true)
public class PermitTypeConverter implements AttributeConverter<PermitType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PermitType permitType) {
        if (permitType == null) {
            return null;
        }
        return permitType.getCode();
    }

    @Override
    public PermitType convertToEntityAttribute(Integer code) {
        if (code == null) {
            return null;
        }

        return Stream.of(PermitType.values()).filter(c -> c.getCode().equals(code)).findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
