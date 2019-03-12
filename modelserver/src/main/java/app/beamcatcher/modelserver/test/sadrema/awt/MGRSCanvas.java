package app.beamcatcher.modelserver.test.sadrema.awt;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.beamcatcher.modelserver.model.Beam;
import app.beamcatcher.modelserver.model.Footprint;
import app.beamcatcher.modelserver.model.Marker;
import app.beamcatcher.modelserver.model.SADREMAGridCell;

public class MGRSCanvas extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Integer NUMBER_OF_MGRS_COLUMNS_AT_100_KM2_RESOLUTION = 60;
	private static final Integer NUMBER_OF_MGRS_ROWS_AT_100_KM2_RESOLUTION = 20;
	private static final Integer SCALE_DOWN_FACTOR = 1;
	private static final Integer MGRS_COLUMN_WIDTH_AT_100_KM2_RESOLUTION = 100 / SCALE_DOWN_FACTOR;
	private static final Integer MGRS_ROW_HEIGHT_AT_100_KM2_RESOLUTION = 100 / SCALE_DOWN_FACTOR;
	private static final Integer MGRS_CANVAS_WIDTH = NUMBER_OF_MGRS_COLUMNS_AT_100_KM2_RESOLUTION
			* MGRS_COLUMN_WIDTH_AT_100_KM2_RESOLUTION;
	private static final Integer MGRS_CANVAS_HEIGHT = NUMBER_OF_MGRS_ROWS_AT_100_KM2_RESOLUTION
			* MGRS_ROW_HEIGHT_AT_100_KM2_RESOLUTION;

	private List<Marker> setMarkerCellSpace;
	private Map<String, Set<Beam>> beamsPerSatellite;

	public MGRSCanvas(final List<Marker> pSetMarkerCellSpace, final Map<String, Set<Beam>> pBeamsPerSatellite) {
		this();
		this.setMarkerCellSpace = pSetMarkerCellSpace;
		this.beamsPerSatellite = pBeamsPerSatellite;
	}

	public MGRSCanvas() {
		this.setSize(MGRS_CANVAS_WIDTH, MGRS_CANVAS_HEIGHT);
	}

	@Override
	public void paint(final Graphics pGraphics) {
		this.paintVerticalLines(pGraphics);
		this.paintHorizontalLines(pGraphics);
		this.paintMarkers(pGraphics);
		this.paintBeams(pGraphics);
	}

	private void paintBeams(Graphics pGraphics) {
		final Set<String> setSatelliteName = this.beamsPerSatellite.keySet();
		for (String satelliteName : setSatelliteName) {
			final Set<Beam> beams = this.beamsPerSatellite.get(satelliteName);
			for (Beam beam : beams) {
				final Footprint footprint = beam.getFootprint();
				final Set<SADREMAGridCell> setSADREMAGridCell = footprint.getSetSADREMAGridCell();
				for (SADREMAGridCell sadremaGridCell : setSADREMAGridCell) {
					final Integer pRowIndex = sadremaGridCell.getRowIndex();
					final Integer pColumnIndex = sadremaGridCell.getColumnIndex();
					final String textToDisplayAsString = satelliteName + "-" + beam.getName().toString().toUpperCase()
							+ ": " + beam.getDeliveredUnits();
					final Integer leftPadding = 25;
					final Integer x = ((pColumnIndex - 1) * MGRS_COLUMN_WIDTH_AT_100_KM2_RESOLUTION) + leftPadding;
					Integer y = (pRowIndex * MGRS_ROW_HEIGHT_AT_100_KM2_RESOLUTION)
							- (MGRS_ROW_HEIGHT_AT_100_KM2_RESOLUTION / 2);
					y = y + 20;
					final char textToDisplayAsCharArray[] = textToDisplayAsString.toCharArray();
					pGraphics.drawChars(textToDisplayAsCharArray, 0, textToDisplayAsCharArray.length, x, y);
				}
			}
		}
	}

	private void paintVerticalLines(final Graphics pGraphics) {
		final Integer lineBeginCoordinateY = 0;
		final Integer lineEndCoordinateY = MGRS_CANVAS_HEIGHT;
		for (int i = 0; i < NUMBER_OF_MGRS_COLUMNS_AT_100_KM2_RESOLUTION; i++) {
			final Integer lineBeginCoordinateX = i * (MGRS_COLUMN_WIDTH_AT_100_KM2_RESOLUTION);
			final Integer lineEndCoordinateX = lineBeginCoordinateX;
			pGraphics.drawLine(lineBeginCoordinateX, lineBeginCoordinateY, lineEndCoordinateX, lineEndCoordinateY);
		}

	}

	private void paintHorizontalLines(final Graphics pGraphics) {
		final Integer lineBeginCoordinateX = 0;
		final Integer lineEndCoordinateX = MGRS_CANVAS_WIDTH;
		for (int i = 0; i < NUMBER_OF_MGRS_COLUMNS_AT_100_KM2_RESOLUTION; i++) {
			final Integer lineBeginCoordinateY = i * (MGRS_ROW_HEIGHT_AT_100_KM2_RESOLUTION);
			final Integer lineEndCoordinateY = lineBeginCoordinateY;
			pGraphics.drawLine(lineBeginCoordinateX, lineBeginCoordinateY, lineEndCoordinateX, lineEndCoordinateY);
		}

	}

	public void paintMarkers(final Graphics pGraphics) {
		final String fontFamily = "TimesRoman";
		final Integer fontSize = 36;
		final Font font = new Font(fontFamily, Font.PLAIN, fontSize);

		pGraphics.setFont(font);

		if (this.setMarkerCellSpace != null) {
			for (Marker markerCellSpace : this.setMarkerCellSpace) {
				final String textToDisplayAsString = markerCellSpace.getRequestedUnits() + ","
						+ markerCellSpace.getPriority();

				final Integer leftPadding = 25;

				final Integer x = ((markerCellSpace.getLocation().getSadremaGridCell().getColumnIndex() - 1)
						* MGRS_COLUMN_WIDTH_AT_100_KM2_RESOLUTION) + leftPadding;
				final Integer y = (markerCellSpace.getLocation().getSadremaGridCell().getRowIndex()
						* MGRS_ROW_HEIGHT_AT_100_KM2_RESOLUTION) - (MGRS_ROW_HEIGHT_AT_100_KM2_RESOLUTION / 2);

				final char textToDisplayAsCharArray[] = textToDisplayAsString.toCharArray();
				pGraphics.drawChars(textToDisplayAsCharArray, 0, 3, x, y);
			}
		}

	}
}
