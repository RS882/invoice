package com.billing.invoice.exception_handler.exceptions.server_exception.exceptions;

import com.billing.invoice.exception_handler.exceptions.server_exception.ServerIOException;

public class MinioException extends ServerIOException {
    public MinioException(String message) {
        super("Error while working with files in Minio :" + message);
    }
}
