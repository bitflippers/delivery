package app.beamcatcher.modelserver.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.beamcatcher.modelserver.configuration.Configuration;

public class Satellite {

	private static final Logger logger = LoggerFactory.getLogger(Satellite.class);

	@NotNull
	@Min(Configuration.SATELLITE_MINIMUM_PRODUCED_UNITS)
	@Max(Configuration.SATELLITE_MAXIMUM_PRODUCED_UNITS)
	private Integer producedUnits;

	@NotNull
	@Min(Configuration.SATELLITE_MINIMUM_PRODUCED_UNITS)
	@Max(Configuration.SATELLITE_MAXIMUM_PRODUCED_UNITS)
	private Integer usedUnits;

	@NotNull
	@Min(Configuration.SATELLITE_MINIMUM_PRODUCED_UNITS)
	@Max(Configuration.SATELLITE_MAXIMUM_PRODUCED_UNITS)
	private Integer freeUnits;

	@NotNull
	@Size(min = Configuration.SATELLITE_MINIMUM_NUMBER_OF_BEAMS, max = Configuration.SATELLITE_MAXIMUM_NUMBER_OF_BEAMS)
	@Valid
	private Map<UUID, Beam> mapBeam;

	@NotNull
	@Size(min = Configuration.SATELLITE_NAME_MIN_LENGTH, max = Configuration.SATELLITE_NAME_MAX_LENGTH)
	private CharSequence name;

	public Satellite() {

	}

	public Satellite(final String pName, final Integer pProducedUnits) {
		this.uuid = UUID.randomUUID();
		this.name = pName;
		this.freeUnits = 0;
		this.usedUnits = 0;
		this.producedUnits = pProducedUnits;
		this.mapBeam = new HashMap<UUID, Beam>();
	}

	public Integer getProducedUnits() {
		return producedUnits;
	}

	public Integer getUsedUnits() {
		return usedUnits;
	}

	public Integer getFreeUnits() {
		return freeUnits;
	}

	public Map<UUID, Beam> getMapBeam() {
		return new HashMap<UUID, Beam>(mapBeam);
	}

	public CharSequence getName() {
		return name;
	}

	public UUID getUuid() {
		return uuid;
	}

	@NotNull
	private UUID uuid;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Satellite) {
			return this.uuid.equals(((Satellite) obj).uuid);
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

	protected void setBeams(final Set<Beam> pSetBeam) {

		Integer totalBeamDeliveredUnits = 0;
		for (Beam beam : pSetBeam) {
			Integer deliveredUnits = beam.getDeliveredUnits();
			totalBeamDeliveredUnits = totalBeamDeliveredUnits + deliveredUnits;
		}

		if (totalBeamDeliveredUnits > this.getProducedUnits()) {
			logger.error("Total beam delivered units (" + totalBeamDeliveredUnits
					+ ") is greater than satellite production (" + this.producedUnits + ") !");
			System.exit(-1);
		}

		// check capacities, update
		this.mapBeam.clear();

		this.freeUnits = this.producedUnits;
		this.usedUnits = 0;

		for (Beam beam : pSetBeam) {
			final UUID beamUUID = beam.getUuid();
			this.mapBeam.put(beamUUID, beam);
		}

		this.usedUnits = totalBeamDeliveredUnits;
		this.freeUnits = this.producedUnits - this.usedUnits;
	}

}
