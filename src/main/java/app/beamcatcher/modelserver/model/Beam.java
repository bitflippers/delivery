package app.beamcatcher.modelserver.model;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

import app.beamcatcher.modelserver.configuration.Configuration;

public class Beam {

	@NotNull
	@Size(min = Configuration.BEAM_NAME_MIN_LENGTH, max = Configuration.BEAM_NAME_MAX_LENGTH)
	private CharSequence name;

	@NotNull
	@Min(Configuration.BEAM_MINIMUM_DELIVERED_UNITS)
	@Max(Configuration.SATELLITE_MAXIMUM_PRODUCED_UNITS)
	private Integer deliveredUnits;

	@NotNull
	@Valid
	private Footprint footprint;

	@NotNull
	private UUID uuid;

	public Beam(final CharSequence pName, final Integer pDeliveredUnits, final Footprint pFootprint) {
		this.uuid = UUID.randomUUID();
		this.name = pName;
		this.deliveredUnits = pDeliveredUnits;
		this.footprint = pFootprint;
	}

	public CharSequence getName() {
		return name;
	}

	public Integer getDeliveredUnits() {
		return deliveredUnits;
	}

	public Footprint getFootprint() {
		return footprint;
	}

	public UUID getUuid() {
		return uuid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Beam) {
			return this.uuid.equals(((Beam) obj).uuid);
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
