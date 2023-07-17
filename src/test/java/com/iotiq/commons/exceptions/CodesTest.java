package com.iotiq.commons.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CodesTest {

    @Test
    void toStringArray() {
    }

    @Test
    void add() {
        Codes codes = new Codes();

        {
            String[] stringArray = codes.toStringArray();

            assertThat(stringArray).isEmpty();
        }

        {
            codes.add("exception");
            assertThat(codes.toStringArray()).containsAll(List.of("exception"));
        }

        {
            codes.add("errorName");
            assertThat(codes.toStringArray()).containsAll(List.of("exception", "exception.errorName"));
        }
    }


    @Test
    void applicationException() {
        {
            Exp exp = new Exp(HttpStatus.INTERNAL_SERVER_ERROR, "prefix", List.of("mp1", "mp2"));
            String[] codes = exp.getCodes();

            assertThat(codes).containsAll(List.of("exception", "exception.prefix", "exception.prefix.mp1", "exception.prefix.mp1.mp2"));

            assertThat(exp.getDefaultMessage()).isEqualTo("exception.prefix.mp1.mp2");
        }

        {
            Exp exp = new Exp(HttpStatus.INTERNAL_SERVER_ERROR, "prefix", List.of("mp1", "mp2"), "arg1", "arg2");
            String[] codes = exp.getCodes();

            assertThat(codes).containsAll(List.of("exception", "exception.prefix", "exception.prefix.mp1", "exception.prefix.mp1.mp2"));
        }
    }
}


class Exp extends ApplicationException {

    protected Exp(HttpStatus status, String prefix, List<String> messageParts, Object... arguments) {
        super(status, prefix, messageParts, arguments);
    }

    protected Exp(HttpStatus status, String prefix, List<String> messageParts) {
        super(status, prefix, messageParts);
    }
}