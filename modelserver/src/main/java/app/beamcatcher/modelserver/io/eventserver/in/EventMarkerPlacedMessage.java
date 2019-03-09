package app.beamcatcher.modelserver.io.eventserver.in;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import app.beamcatcher.modelserver.configuration.Configuration;

public class EventMarkerPlacedMessage {

	@Pattern(regexp = Configuration.EVENT_IDENTIFIER_REGEX)
	@NotNull
	private String eventIdentifier;

	@NotNull
	@Size(min = Configuration.USER_USERNAME_MIN_LENGTH, max = Configuration.USER_USERNMAE_MAX_LENGTH)
	private String username;

	@NotNull
	@Min(Configuration.MGRS_WGS84_LATITUDE_LOWER_LIMIT_INCLUSIVE)
	@Max(Configuration.MGRS_WGS84_LATITUDE_UPPER_LIMIT_INCLUSIVE)
	private Double latitude;

	@NotNull
	@Min(Configuration.MGRS_WGS84_LONGITUDE_LOWER_LIMIT_INCLUSIVE)
	@Max(Configuration.MGRS_WGS84_LONGITUDE_UPPER_LIMIT_INCLUSIVE)
	private Double longitude;

	@NotNull
	@Min(Configuration.MARKER_MINIMUM_REQUESTED_UNITS)
	@Max(Configuration.MARKER_MAXIMUM_REQUESTED_UNITS)
	private Integer requestedUnits;

	@NotNull
	@Min(Configuration.MARKER_MINIMUM_PRIORITY)
	@Max(Configuration.MARKER_MAXIMUM_PRIORITY)
	private Integer priority;

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

	public Integer getRequestedUnits() {
		return requestedUnits;
	}

	public void setRequestedUnits(Integer requestedUnits) {
		this.requestedUnits = requestedUnits;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

}
