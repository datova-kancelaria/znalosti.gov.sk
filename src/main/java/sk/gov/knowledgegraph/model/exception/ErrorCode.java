package sk.gov.knowledgegraph.model.exception;

import lombok.Getter;

@Getter
public enum ErrorCode implements Error {

    OUTPUT_FORMAT_FORMAT_MISSING("Missing output format!", 400), MISMATCHED_ACCEPT_HEADER("Mismatched accept header!", 400);

    private final String message;

    private final int code;

    ErrorCode(final String message, final int code) {
        this.message = message;
        this.code = code;
    }

}
