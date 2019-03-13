package app.beamcatcher.modelserver.model;

import java.util.UUID;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;

import app.beamcatcher.modelserver.configuration.Configuration;

// Grid based on MGRS - see https://en.wikipedia.org/wiki/Military_Grid_Reference_System
public class SADREMAGridCell {

	@NotNull
	@Min(Configuration.SADREMA_GRID_CELL_MINIMUM_ROW_INDEX)
	@Max(Configuration.SADREMA_GRID_CELL_MAXIMUM_ROW_INDEX)
	private Integer rowIndex;

	@NotNull
	@Min(Configuration.SADREMA_GRID_CELL_MINIMUM_COLUMN_INDEX)
	@Max(Configuration.SADREMA_GRID_CELL_MAXIMUM_COLUMN_INDEX)
	private Integer columnIndex;

	public SADREMAGridCell() {

	}

	public SADREMAGridCell(final Integer pRowIndex, final Integer pColumnIndex) {
		this.uuid = UUID.randomUUID();
		this.rowIndex = pRowIndex;
		this.columnIndex = pColumnIndex;
	}

	public Integer getRowIndex() {
		return rowIndex;
	}

	public Integer getColumnIndex() {
		return columnIndex;
	}

	public UUID getUuid() {
		return uuid;
	}

	@NotNull
	private UUID uuid;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SADREMAGridCell) {
			return this.uuid.equals(((SADREMAGridCell) obj).uuid);
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
