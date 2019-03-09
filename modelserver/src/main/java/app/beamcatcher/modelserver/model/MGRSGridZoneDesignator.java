package app.beamcatcher.modelserver.model;

import java.util.UUID;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang.builder.ToStringBuilder;

import app.beamcatcher.modelserver.configuration.Configuration;

// see https://en.wikipedia.org/wiki/Military_Grid_Reference_System
public class MGRSGridZoneDesignator {

	@NotNull
	@Min(Configuration.MGRS_GRID_ZONE_DESIGNATOR_UTM_ZONE_MINIMUM)
	@Max(Configuration.MGRS_GRID_ZONE_DESIGNATOR_UTM_ZONE_MAXIMUM)
	private Integer utmZone;

	@Pattern(regexp = Configuration.MGRS_GRID_ZONE_DESIGNATOR_LATITUDE_BAND_LETTER_REGEXP)
	@NotNull
	private CharSequence latitudeBandLetter;

	public MGRSGridZoneDesignator(final Integer pUTMZone, final CharSequence pLatitudeBandLetter) {
		this.uuid = UUID.randomUUID();
		this.utmZone = pUTMZone;
		this.latitudeBandLetter = pLatitudeBandLetter;
	}

	public Integer getUtmZone() {
		return utmZone;
	}

	public CharSequence getLatitudeBandLetter() {
		return latitudeBandLetter;
	}

	public UUID getUuid() {
		return uuid;
	}

	@NotNull
	private UUID uuid;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MGRSGridZoneDesignator) {
			return this.uuid.equals(((MGRSGridZoneDesignator) obj).uuid);
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
