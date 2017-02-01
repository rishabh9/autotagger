package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Set;

/**
 * @author rishabh
 */
@Value.Immutable
@JsonSerialize(as = ImmutableError.class)
@JsonDeserialize(as = ImmutableError.class)
public abstract class Error {

    @JsonProperty("code")
    public abstract int code();

    @JsonProperty("message")
    public abstract String message();

    @JsonProperty("errors")
    public abstract Set<Message> errors();

}
