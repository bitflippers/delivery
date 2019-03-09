package app.beamcatcher.modelserver.model;

import java.util.UUID;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;

import app.beamcatcher.modelserver.configuration.Configuration;

/**
 *
 * Acronyms:
 * 
 * Military Grid Reference System (MGRS)
 * 
 * World Geodetic System 1984 (WGS84)
 *
 * Description:
 *
 * This class models a WGS84 coordinate (latitude, longitude) that is
 * constrained to exist in the MGRS domain. MGRS defines it's domain in
 * latitudes 84 (North) to -80 (South). Other WGS84 latitudes do not have a
 * corresponding MGRS Grid Zone Designator (GZD) and thus are not of interest of
 * the app.beamcatcher project. See
 * http://earth-info.nga.mil/GandG/coordsys/images/utm_mgrs_images/MGRS_GZD.pdf
 * for more info.
 * 
 */
public class MGRSWGS84Coordinate {

	@NotNull
	@Min(Configuration.MGRS_WGS84_LATITUDE_LOWER_LIMIT_INCLUSIVE)
	@Max(Configuration.MGRS_WGS84_LATITUDE_UPPER_LIMIT_INCLUSIVE)
	private Double latitude;

	@NotNull
	@Min(Configuration.MGRS_WGS84_LONGITUDE_LOWER_LIMIT_INCLUSIVE)
	@Max(Configuration.MGRS_WGS84_LONGITUDE_UPPER_LIMIT_INCLUSIVE)
	private Double longitude;

	public MGRSWGS84Coordinate(final Double pLatitude, final Double pLongitude) {
		this.uuid = UUID.randomUUID();
		this.latitude = pLatitude;
		this.longitude = pLongitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public UUID getUuid() {
		return uuid;
	}

	@NotNull
	private UUID uuid;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MGRSWGS84Coordinate) {
			return this.uuid.equals(((MGRSWGS84Coordinate) obj).uuid);
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
