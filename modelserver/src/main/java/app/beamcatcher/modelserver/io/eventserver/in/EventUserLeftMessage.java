package app.beamcatcher.modelserver.io.eventserver.in;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import app.beamcatcher.modelserver.configuration.Configuration;

public class EventUserLeftMessage {

	@Pattern(regexp = Configuration.EVENT_IDENTIFIER_REGEX)
	@NotNull
	private String eventIdentifier;

	@NotNull
	@Size(min = Configuration.USER_USERNAME_MIN_LENGTH, max = Configuration.USER_USERNMAE_MAX_LENGTH)
	private String username;

	public String getEventIdentifier() {
		return eventIdentifier;
	}

	public void setEventIdentifier(String eventIdentifier) {
		this.eventIdentifier = eventIdentifier;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
