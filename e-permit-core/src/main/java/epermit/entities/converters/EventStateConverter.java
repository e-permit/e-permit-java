package epermit.entities.converters;

import java.util.stream.Stream;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import epermit.common.EventState;

@Converter(autoApply = true)
public class EventStateConverter implements AttributeConverter<EventState, Integer> {

    @Override
    public Integer convertToDatabaseColumn(EventState state) {
        if (state == null) {
            return null;
        }
        return state.getCode();
    }

    @Override
    public EventState convertToEntityAttribute(Integer code) {
        if (code == null) {
            return null;
        }

        return Stream.of(EventState.values()).filter(c -> c.getCode().equals(code)).findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}

