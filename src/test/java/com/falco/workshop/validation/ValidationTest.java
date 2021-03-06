package com.falco.workshop.validation;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.falco.workshop.validation.ValidationMessage.validationError;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ValidationTest {
    @Test
    public void shouldDetectOverlappingRows() {
        Table table = new Table(asList(new OverlappingDatesValidator()));
        table.addRows(
                row(from("2018-01-01 12:15"), to("2018-01-01 12:30")),
                row(from("2018-01-01 12:30"), to("2018-01-01 12:45")));
        table.validateTable();
        assertThat(table.rowAt(0).validationResults()).containsOnly(validationError("msg.validation.overlapping.dates", asList("from", "to")));
        assertThat(table.rowAt(1).validationResults()).containsOnly(validationError("msg.validation.overlapping.dates", asList("from", "to")));
    }

    @Test
    public void shouldDetectOverlappingRowsWithNulls() {
        Table table = new Table(asList(new OverlappingDatesValidator()));
        table.addRows(
                row(from(null), to("2018-01-01 12:30")),
                row(from("2018-01-01 12:30"), to(null)));
        table.validateTable();
        assertThat(table.rowAt(0).validationResults()).containsOnly(validationError("msg.validation.overlapping.dates", asList("from", "to")));
        assertThat(table.rowAt(1).validationResults()).containsOnly(validationError("msg.validation.overlapping.dates", asList("from", "to")));
    }

    private Consumer<Map<String, Object>> to(String to) {
        return attributes -> attributes.put("to", date(to));
    }

    private Consumer<Map<String, Object>> from(String from) {
        return attributes -> attributes.put("from", date(from));
    }

    private LocalDateTime date(String from) {
        return LocalDateTime.parse(from.replaceAll(" ", "T"));
    }

    @SafeVarargs
    private final Row row(Consumer<Map<String, Object>>... attributes) {
        Map<String, Object> attributesMap = new HashMap<>();
        asList(attributes).forEach(c -> c.accept(attributesMap));
        return new Row(attributesMap);
    }
}
