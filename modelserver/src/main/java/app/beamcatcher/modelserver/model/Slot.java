package app.beamcatcher.modelserver.model;

import java.util.UUID;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;

import app.beamcatcher.modelserver.configuration.Configuration;

public class Slot {

	@NotNull
	private UUID uuid;

	@NotNull
	@Min(Configuration.MINIMUM_SLOT_INDEX)
	@Max(Configuration.MAXIMUM_SLOT_INDEX)
	private Integer identifier;

	public Slot(Integer identifier) {
		this.uuid = UUID.randomUUID();
		this.identifier = identifier;
	}

	public UUID getUuid() {
		return uuid;
	}

	public Integer getIdentifier() {
		return identifier;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Slot) {
			return this.uuid.equals(((Slot) obj).uuid);
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
