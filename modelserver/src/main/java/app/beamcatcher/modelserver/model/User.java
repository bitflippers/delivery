package app.beamcatcher.modelserver.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

import app.beamcatcher.modelserver.configuration.Configuration;

public class User {

	@NotNull
	private UUID uuid;

	@NotNull
	@Size(min = Configuration.USER_USERNAME_MIN_LENGTH, max = Configuration.USER_USERNMAE_MAX_LENGTH)
	private CharSequence username;

	@NotNull
	@Size(min = Configuration.USER_MARKER_MIN, max = Configuration.USER_MARKER_MAX)
	@Valid
	private Map<UUID, Marker> mapMarker;

	@NotNull
	@Valid
	private Slot slot;

	@NotNull
	@PastOrPresent
	private Date mostRecentActivityDate;

	public User() {

	}

	public User(final CharSequence pUsername, final Slot pSlot) {
		this.uuid = UUID.randomUUID();
		this.mostRecentActivityDate = new Date();
		this.mapMarker = new HashMap<UUID, Marker>();
		this.username = pUsername;
		this.slot = pSlot;
	}

	public Slot getSlot() {
		return slot;
	}

	public void setMostRecentActivityDate(Date mostRecentActivityDate) {
		this.mostRecentActivityDate = mostRecentActivityDate;
	}

	public UUID getUuid() {
		return uuid;
	}

	public CharSequence getUsername() {
		return username;
	}

	public HashMap<UUID, Marker> getMapMarker() {
		return new HashMap<UUID, Marker>(this.mapMarker);
	}

	public Date getMostRecentActivityDate() {
		return mostRecentActivityDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			return this.uuid.equals(((User) obj).uuid);
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

	protected void addMarker(final Double pLatitude, final Double pLongitude, final Integer pRequestedUnits,
			final Integer pPriority) {
		final Marker marker = new Marker(pLatitude, pLongitude, pRequestedUnits, pPriority);
		this.mapMarker.put(marker.getUuid(), marker);
		this.setMostRecentActivityDate(new Date());
	}

}
