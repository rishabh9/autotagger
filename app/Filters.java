import filters.AccessLoggingFilter;
import filters.RequestTimerFilter;
import play.Environment;
import play.Mode;
import play.filters.cors.CORSFilter;
import play.filters.gzip.GzipFilter;
import play.http.HttpFilters;
import play.mvc.EssentialFilter;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class configures filters that run on every request. This
 * class is queried by Play to get a list of filters.
 * <p>
 * Play will automatically use filters from any class called
 * <code>Filters</code> that is placed the root package. You can load filters
 * from a different class by adding a `play.http.filters` setting to
 * the <code>application.conf</code> configuration file.
 */
@Singleton
public class Filters implements HttpFilters {

    private final Environment env;
    private final EssentialFilter requestTimerFilter;
    private final CORSFilter corsFilter;
    private final GzipFilter gzipFilter;
    private final AccessLoggingFilter accessLoggingFilter;

    /**
     * @param env                 Basic environment settings for the current application.
     * @param requestTimerFilter  A Request Timer Filter
     * @param corsFilter          The CORS Filter
     * @param gzipFilter          The GZip Filter
     * @param accessLoggingFilter The Access Logging Filter
     */
    @Inject
    public Filters(Environment env, RequestTimerFilter requestTimerFilter, CORSFilter corsFilter, GzipFilter gzipFilter,
                   AccessLoggingFilter accessLoggingFilter) {
        this.env = env;
        this.requestTimerFilter = requestTimerFilter;
        this.corsFilter = corsFilter;
        this.gzipFilter = gzipFilter;
        this.accessLoggingFilter = accessLoggingFilter;
    }

    @Override
    public EssentialFilter[] filters() {
        // Use the request timer filter if we're running development mode.
        if (env.mode().equals(Mode.DEV)) {
            return new EssentialFilter[]{
                    requestTimerFilter,
                    corsFilter.asJava(),
                    gzipFilter.asJava(),
                    accessLoggingFilter.asJava()};
        } else {
            return new EssentialFilter[]{
                    corsFilter.asJava(),
                    gzipFilter.asJava(),
                    accessLoggingFilter.asJava()
            };
        }
    }

}
