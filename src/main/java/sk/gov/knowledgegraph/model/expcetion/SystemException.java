package sk.gov.knowledgegraph.model.expcetion;

public class SystemException extends RuntimeException {
    private static final long serialVersionUID = 6930501473624734213L;

    public SystemException(final String message) {
        super(message);
    }

    public SystemException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
