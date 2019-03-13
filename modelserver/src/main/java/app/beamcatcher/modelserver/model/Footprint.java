package app.beamcatcher.modelserver.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

import app.beamcatcher.modelserver.configuration.Configuration;

public class Footprint {

	@NotNull
	@Size(min = Configuration.BEAM_MIN_SIZE_IN_CELLS, max = Configuration.BEAM_MAX_SIZE_IN_CELLS)
	@Valid
	private Set<MGRSGridZoneDesignator> setMGRSGridZoneDesignator;

	@NotNull
	@Size(min = Configuration.BEAM_MIN_SIZE_IN_CELLS, max = Configuration.BEAM_MAX_SIZE_IN_CELLS)
	@Valid
	private Set<SADREMAGridCell> setSADREMAGridCell;

	public Footprint() {

	}

	public Footprint(Set<SADREMAGridCell> pSetSADREMAGridCell) {
		this.uuid = UUID.randomUUID();
		this.setSADREMAGridCell = pSetSADREMAGridCell;
		this.setMGRSGridZoneDesignator(pSetSADREMAGridCell);
	}

	private void setMGRSGridZoneDesignator(final Set<SADREMAGridCell> pSetSADREMAGridCell) {

		final Set<MGRSGridZoneDesignator> setMGRSGridZoneDesignator = new HashSet<MGRSGridZoneDesignator>();

		for (SADREMAGridCell sadremaGridCell : pSetSADREMAGridCell) {
			final Integer utmZone = sadremaGridCell.getColumnIndex();
			final Integer rowIndex = sadremaGridCell.getRowIndex();
			final Character mgrsLatitudeBandLetter = Configuration.SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP
					.get(rowIndex);
			final CharSequence mgrsLatitudeBandLetterCharSequence = String.valueOf(mgrsLatitudeBandLetter);
			final MGRSGridZoneDesignator mgrsGridZoneDesignator = new MGRSGridZoneDesignator(utmZone,
					mgrsLatitudeBandLetterCharSequence);
			setMGRSGridZoneDesignator.add(mgrsGridZoneDesignator);
		}

		this.setMGRSGridZoneDesignator = setMGRSGridZoneDesignator;

	}

	public Set<MGRSGridZoneDesignator> getSetMGRSGridZoneDesignator() {
		return new HashSet<MGRSGridZoneDesignator>(this.setMGRSGridZoneDesignator);
	}

	public Set<SADREMAGridCell> getSetSADREMAGridCell() {
		return new HashSet<SADREMAGridCell>(this.setSADREMAGridCell);
	}

	@NotNull
	private UUID uuid;

	public UUID getUuid() {
		return uuid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Footprint) {
			return this.uuid.equals(((Footprint) obj).uuid);
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
