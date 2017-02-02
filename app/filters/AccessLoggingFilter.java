package filters;

import akka.stream.Materializer;
import models.loggers.ImmutableRequest;
import play.Logger;
import play.api.http.MediaType;
import play.i18n.Lang;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Locale;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * This {@link Filter} logs all incoming requests.
 *
 * @author rishabh
 */
@Singleton
public class AccessLoggingFilter extends Filter {

    private final Logger.ALogger accessLogger = Logger.of(AccessLoggingFilter.class);

    @Inject
    public AccessLoggingFilter(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> next,
                                         Http.RequestHeader requestHeader) {

        if (accessLogger.isInfoEnabled()) {
            accessLogger.info(getRequest(requestHeader));
        }
        return next.apply(requestHeader);
    }

    private String getRequest(Http.RequestHeader requestHeader) {

        ImmutableRequest.Builder builder = ImmutableRequest.builder()
                .method(requestHeader.method())
                .uri(requestHeader.uri())
                .remoteAddress(requestHeader.remoteAddress())
                .path(requestHeader.path())
                .charset(requestHeader.charset().isPresent() ? requestHeader.charset().get() : "")
                .contentType(requestHeader.contentType().isPresent() ? requestHeader.contentType().get() : "")
                .headers(requestHeader.headers())
                .host(requestHeader.host())
                .queryString(requestHeader.queryString())
                .secure(requestHeader.secure())
                .tags(requestHeader.tags())
                .version(requestHeader.version());

        requestHeader.acceptedTypes()
                .stream()
                .map(MediaType::mediaType)
                .forEach(builder::addAcceptedTypes);

        requestHeader.acceptLanguages()
                .stream()
                .map(Lang::toLocale)
                .map(Locale::getDisplayName)
                .forEach(builder::addAcceptLanguages);

        return builder.build().toString();
    }
}
