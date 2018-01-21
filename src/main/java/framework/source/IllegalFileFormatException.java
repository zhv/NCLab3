package framework.source;

class IllegalFileFormatException extends RuntimeException {
    IllegalFileFormatException() {
    }

    IllegalFileFormatException(String message) {
        super(message);
    }

    IllegalFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    IllegalFileFormatException(Throwable cause) {
        super(cause);
    }

    IllegalFileFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
