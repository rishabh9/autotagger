package errors;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import models.ImmutableError;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.Configuration;
import play.Environment;
import play.Logger;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.mvc.Http.Status.*;

/**
 * @author rishabh
 */
public class ErrorHandler extends DefaultHttpErrorHandler {

    private static final String UNEXPECTED_ERROR = "An unexpected error has occurred!";
    private static final String INVALID_REQUEST = "Error parsing request.";
    private static final String NOT_FOUND_MSG = "The provided path or resource was not found.";
    private static final String FORBIDDEN_MSG = "You do not have the required permission to perform this operation.";

    private final Logger.ALogger log = Logger.of(ErrorHandler.class);

    @Inject
    public ErrorHandler(Configuration configuration, Environment environment,
                        OptionalSourceMapper sourceMapper, Provider<Router> routes) {
        super(configuration, environment, sourceMapper, routes);
    }

    @Override
    public CompletionStage<Result> onClientError(Http.RequestHeader request, int statusCode, String message) {
        log.error("Status: {}, Message: {}", statusCode, message);
        switch (statusCode) {
            case NOT_FOUND:
                return onNotFound(request, message);
            case BAD_REQUEST:
                return onBadRequest(request, message);
            case FORBIDDEN:
                return onForbidden(request, message);
            case Http.Status.UNSUPPORTED_MEDIA_TYPE:
                return onUnsupportedMediaType(request, message);
            default:
                return onOtherClientError(request, statusCode, message);
        }
    }

    @Override
    protected CompletionStage<Result> onOtherClientError(Http.RequestHeader request, int statusCode, String message) {
        log.error("Status: {}, Message: {}", statusCode, message);
        return CompletableFuture.completedFuture(Results.status(statusCode, toJson(statusCode, INVALID_REQUEST)));
    }

    private CompletionStage<Result> onUnsupportedMediaType(Http.RequestHeader request, String message) {
        log.error("{} - Client requested: '{} {} {} {}', From: '{}'", message, request.method(), request.host(),
                request.path(), request.contentType().orElse(""), request.remoteAddress());
        return CompletableFuture.completedFuture(
                Results.status(UNSUPPORTED_MEDIA_TYPE, toJson(UNSUPPORTED_MEDIA_TYPE, message)));
    }

    @Override
    protected CompletionStage<Result> onNotFound(Http.RequestHeader request, String message) {
        log.error("{} - Client requested: '{} {} {} {}', From: '{}'", message, request.method(), request.host(),
                request.path(), request.contentType().orElse(""), request.remoteAddress());
        return CompletableFuture.completedFuture(Results.notFound(toJson(NOT_FOUND, NOT_FOUND_MSG)));
    }

    @Override
    protected CompletionStage<Result> onBadRequest(Http.RequestHeader request, String message) {
        log.error("{} - Requested: '{} {} {} {}', From: '{}'", message, request.method(), request.host(),
                request.path(), request.contentType().orElse(""), request.remoteAddress());
        return CompletableFuture.completedFuture(Results.badRequest(toJson(BAD_REQUEST, message)));
    }

    @Override
    protected CompletionStage<Result> onForbidden(Http.RequestHeader request, String message) {
        log.error("{} - Requested: '{} {} {} {}', From: '{}'", message, request.method(), request.host(),
                request.path(), request.contentType().orElse(""), request.remoteAddress());
        return CompletableFuture.completedFuture(Results.forbidden(toJson(FORBIDDEN, FORBIDDEN_MSG)));
    }

    @Override
    public CompletionStage<Result> onServerError(Http.RequestHeader request, Throwable exception) {
        log.error(exception.getMessage(), exception);
        if (ExceptionUtils.getRootCause(exception) instanceof JsonMappingException) {
            return CompletableFuture.completedFuture(Results.badRequest(toJson(BAD_REQUEST, INVALID_REQUEST)));
        } else {
            return CompletableFuture.completedFuture(
                    Results.internalServerError(toJson(INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR)));
        }
    }

    @Override
    protected CompletionStage<Result> onDevServerError(Http.RequestHeader request, UsefulException exception) {
        log.error(exception.getMessage(), exception);
        if (ExceptionUtils.getRootCause(exception) instanceof JsonMappingException) {
            return CompletableFuture.completedFuture(Results.badRequest(toJson(BAD_REQUEST, INVALID_REQUEST)));
        } else {
            return CompletableFuture.completedFuture(
                    Results.internalServerError(toJson(INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR)));
        }
    }

    @Override
    protected CompletionStage<Result> onProdServerError(Http.RequestHeader request, UsefulException exception) {
        log.error(exception.getMessage(), exception);
        if (ExceptionUtils.getRootCause(exception) instanceof JsonMappingException) {
            return CompletableFuture.completedFuture(Results.badRequest(toJson(BAD_REQUEST, INVALID_REQUEST)));
        } else {
            return CompletableFuture.completedFuture(
                    Results.internalServerError(toJson(INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR)));
        }
    }

    @Override
    protected void logServerError(Http.RequestHeader request, UsefulException usefulException) {
        // TODO: Add code to log something useful here.
        super.logServerError(request, usefulException);
    }

    private JsonNode toJson(int code, String message) {
        models.Error error = ImmutableError.builder().code(code).message(message).build();
        return Json.toJson(error);
    }
}
