package models.loggers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

/**
 * Model used to log the incoming request by the access logger.
 *
 * @author rishabh
 */
@Value.Immutable
@JsonSerialize(as = ImmutableRequest.class)
@JsonDeserialize(as = ImmutableRequest.class)
public abstract class Request {

    @JsonProperty("method")
    public abstract String method();

    @JsonProperty("uri")
    public abstract String uri();

    @JsonProperty("remoteAddress")
    public abstract String remoteAddress();

    @JsonProperty("path")
    public abstract String path();

    @JsonProperty("acceptedTypes")
    public abstract List<String> acceptedTypes();

    @JsonProperty("acceptedLanguages")
    public abstract List<String> acceptLanguages();

    @JsonProperty("charset")
    public abstract String charset();

    @JsonProperty("contentType")
    public abstract String contentType();

    @JsonProperty("headers")
    public abstract Map<String, String[]> headers();

    @JsonProperty("host")
    public abstract String host();

    @JsonProperty("queryString")
    public abstract Map<String, String[]> queryString();

    @JsonProperty("secure")
    public abstract boolean secure();

    @JsonProperty("tags")
    public abstract Map<String, String> tags();

    @JsonProperty("version")
    public abstract String version();

}
