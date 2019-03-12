package app.beamcatcher.modelserver.model;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.thirdparty.berico.gov.nasa.worldwind.geom.Angle;
import app.beamcatcher.modelserver.thirdparty.berico.gov.nasa.worldwind.geom.coords.MGRSCoord;

public class Location {

	@NotNull
	@Valid
	private MGRSWGS84Coordinate mgrsWGS84Coordinate;

	@NotNull
	@Valid
	private MGRSGridZoneDesignator mgrsGridZoneDesignator;

	@NotNull
	@Valid
	private SADREMAGridCell sadremaGridCell;

	public Location() {

	}

	public Location(final Double pLatitude, final Double pLongitude) {
		this.uuid = UUID.randomUUID();
		setMGRSWGS84Coordinate(pLatitude, pLongitude);
		setMGRSGridZoneDesignator(pLatitude, pLongitude);
		setSADREMAGridCell();
	}

	private void setSADREMAGridCell() {
		final CharSequence latitudeBandLetter = this.mgrsGridZoneDesignator.getLatitudeBandLetter();
		final Integer sadremaGrillCellRowIndex = Configuration.SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP
				.get(latitudeBandLetter.charAt(0));
		final Integer sadremaGrillCellColumnIndex = this.mgrsGridZoneDesignator.getUtmZone();
		final SADREMAGridCell sadremaGridCell = new SADREMAGridCell(sadremaGrillCellRowIndex,
				sadremaGrillCellColumnIndex);
		this.sadremaGridCell = sadremaGridCell;
	}

	private void setMGRSGridZoneDesignator(final Double pLatitude, final Double pLongitude) {
		final Angle latitudeAngle = Angle.fromDegrees(pLatitude);
		final Angle longitudeAngle = Angle.fromDegrees(pLongitude);
		final MGRSCoord mgrsCoord = MGRSCoord.fromLatLon(latitudeAngle, longitudeAngle,
				Configuration.MGRS_GRID_ZONE_DESIGNATOR_API_ARGCODE_FOR_RESOLUTION_IN_SQUARE_KMS);
		final String mgrsCoordAsString = mgrsCoord.toString();
		final String utmZoneString = mgrsCoordAsString.substring(0, 2);
		final Integer utmZoneInteger = Integer.valueOf(utmZoneString);
		final Character latitudeBandLetterString = mgrsCoordAsString.charAt(2);
		final MGRSGridZoneDesignator mgrsGridZoneDesignator = new MGRSGridZoneDesignator(utmZoneInteger,
				latitudeBandLetterString.toString());
		this.mgrsGridZoneDesignator = mgrsGridZoneDesignator;
	}

	private void setMGRSWGS84Coordinate(final Double pLatitude, final Double pLongitude) {
		final MGRSWGS84Coordinate mgrswgs84Coordinate = new MGRSWGS84Coordinate(pLatitude, pLongitude);
		this.mgrsWGS84Coordinate = mgrswgs84Coordinate;
	}

	public MGRSWGS84Coordinate getMgrsWGS84Coordinate() {
		return mgrsWGS84Coordinate;
	}

	public MGRSGridZoneDesignator getMgrsGridZoneDesignator() {
		return mgrsGridZoneDesignator;
	}

	public SADREMAGridCell getSadremaGridCell() {
		return sadremaGridCell;
	}

	@NotNull
	private UUID uuid;

	public UUID getUuid() {
		return uuid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Location) {
			return this.uuid.equals(((Location) obj).uuid);
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
