import filters.AccessLoggingFilter;
import filters.RequestTimerFilter;
import play.filters.cors.CORSFilter;
import play.filters.csrf.CSRFFilter;
import play.filters.gzip.GzipFilter;
import play.filters.headers.SecurityHeadersFilter;
import play.filters.hosts.AllowedHostsFilter;
import play.http.DefaultHttpFilters;

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
public class Filters extends DefaultHttpFilters {

    /**
     * @param requestTimerFilter  A Request Timer Filter
     * @param corsFilter          The CORS Filter
     * @param gzipFilter          The GZip Filter
     * @param accessLoggingFilter The Access Logging Filter
     */
    @Inject
    public Filters(
            AccessLoggingFilter accessLoggingFilter,
            RequestTimerFilter requestTimerFilter,
            CORSFilter corsFilter,
            GzipFilter gzipFilter,
            CSRFFilter csrfFilter,
            AllowedHostsFilter allowedHostsFilter,
            SecurityHeadersFilter securityHeadersFilter
    ) {
        super(
                accessLoggingFilter,
                requestTimerFilter,
                corsFilter,
                gzipFilter,
                csrfFilter,
                allowedHostsFilter,
                securityHeadersFilter
        );
    }
}
