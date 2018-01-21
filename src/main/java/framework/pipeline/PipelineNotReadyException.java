package framework.pipeline;

public class PipelineNotReadyException extends RuntimeException {
    public PipelineNotReadyException() {
        super();
    }

    PipelineNotReadyException(String message) {
        super(message);
    }

    public PipelineNotReadyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PipelineNotReadyException(Throwable cause) {
        super(cause);
    }

    public PipelineNotReadyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
