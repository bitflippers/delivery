package app.beamcatcher.modelserver.model;

import java.util.Date;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.apache.commons.lang.builder.ToStringBuilder;

import app.beamcatcher.modelserver.configuration.Configuration;

public class Marker {

	@NotNull
	@Min(Configuration.MARKER_MINIMUM_REQUESTED_UNITS)
	@Max(Configuration.MARKER_MAXIMUM_REQUESTED_UNITS)
	private Integer requestedUnits;

	private Boolean covered;

	@Valid
	private Location location;

	@NotNull
	@Min(Configuration.MARKER_MINIMUM_PRIORITY)
	@Max(Configuration.MARKER_MAXIMUM_PRIORITY)
	private Integer priority;

	@NotNull
	@PastOrPresent
	private Date spawnDate;

	@NotNull
	private UUID uuid;

	public Marker() {

	}

	public Marker(final Double pLatitude, final Double pLongitude, final Integer pRequestedUnits,
			final Integer pPriority) {
		this.uuid = UUID.randomUUID();
		this.spawnDate = new Date();
		this.requestedUnits = pRequestedUnits;
		this.priority = pPriority;
		this.location = new Location(pLatitude, pLongitude);
	}

	public Integer getRequestedUnits() {
		return requestedUnits;
	}

	public void setRequestedUnits(Integer requestedUnits) {
		this.requestedUnits = requestedUnits;
	}

	public Boolean getCovered() {
		return covered;
	}

	public void setCovered(Boolean covered) {
		this.covered = covered;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Date getSpawnDate() {
		return spawnDate;
	}

	public UUID getUuid() {
		return uuid;
	}

	public Location getLocation() {
		return location;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Marker) {
			return this.uuid.equals(((Marker) obj).uuid);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
