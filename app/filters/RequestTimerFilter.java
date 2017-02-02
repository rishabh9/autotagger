package filters;

import akka.stream.Materializer;
import play.Logger;
import play.mvc.Filter;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.routing.Router;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;


/**
 * This is a simple filter that adds a header to all requests indicating the time taken to service that request.
 *
 * @author rishabh
 */
@Singleton
public class RequestTimerFilter extends Filter {

    private final Logger.ALogger logger = Logger.of(RequestTimerFilter.class);
    private final Executor exec;

    /**
     * @param mat  This object is needed to handle streaming of requests
     *             and responses.
     * @param exec This class is needed to execute code asynchronously.
     *             It is used below by the <code>thenAsyncApply</code> method.
     */
    @Inject
    public RequestTimerFilter(Materializer mat, Executor exec) {
        super(mat);
        this.exec = exec;
    }

    @Override
    public CompletionStage<Result> apply(Function<RequestHeader, CompletionStage<Result>> next,
                                         RequestHeader requestHeader) {

        long startTime = System.currentTimeMillis();
        return next.apply(requestHeader).thenApplyAsync(
                result -> {
                    long endTime = System.currentTimeMillis();
                    long requestTime = endTime - startTime;
                    if (logger.isDebugEnabled()) {
                        Map<String, String> tags = requestHeader.tags();
                        String actionMethod = tags.get(Router.Tags.ROUTE_CONTROLLER) + "." + tags.get(Router.Tags.ROUTE_ACTION_METHOD);
                        logger.debug("{} took {}ms and returned {}", actionMethod, requestTime, result.status());
                    }
                    return result.withHeader("X-Response-Time", String.valueOf(requestTime));
                },
                exec
        );
    }

}
