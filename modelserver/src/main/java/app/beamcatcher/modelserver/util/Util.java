package app.beamcatcher.modelserver.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.model.SADREMAGridCell;

public class Util {

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static String getRandomUsername() {
		return new Faker().gameOfThrones().character();
	}

	public static final Integer getRandomRequestedUnits() {
		return ThreadLocalRandom.current().nextInt((int) Configuration.MARKER_MINIMUM_REQUESTED_UNITS,
				(int) Configuration.MARKER_MAXIMUM_REQUESTED_UNITS + 1);
	}

	public static final Integer getRandomPriority() {
		return ThreadLocalRandom.current().nextInt((int) Configuration.MARKER_MINIMUM_PRIORITY,
				(int) Configuration.MARKER_MAXIMUM_PRIORITY + 1);
	}

	public static final Set<SADREMAGridCell> getRandomSetSADREMAGridCellForBeam() {

		final Integer numberOfSADREMAGridCellToGenerate = ThreadLocalRandom.current()
				.nextInt((int) Configuration.BEAM_MIN_SIZE_IN_CELLS, (int) Configuration.BEAM_MAX_SIZE_IN_CELLS + 1);
		final Set<SADREMAGridCell> randomSetSADREMAGridCellForBeam = new HashSet<SADREMAGridCell>();

		Integer currentRowIndex = ThreadLocalRandom.current().nextInt(
				(int) Configuration.SADREMA_GRID_CELL_MINIMUM_ROW_INDEX,
				(int) Configuration.SADREMA_GRID_CELL_MAXIMUM_ROW_INDEX + 1);
		Integer currentColumnIndex = ThreadLocalRandom.current().nextInt(
				(int) Configuration.SADREMA_GRID_CELL_MINIMUM_COLUMN_INDEX,
				(int) Configuration.SADREMA_GRID_CELL_MAXIMUM_COLUMN_INDEX + 1);

		SADREMAGridCell sadremaGridCell = new SADREMAGridCell(currentRowIndex, currentColumnIndex);

		Integer numberOfGeneratedCells = 0;

		numberOfGeneratedCells++;

		randomSetSADREMAGridCellForBeam.add(sadremaGridCell);

		final Set<String> possibleDirections = new HashSet<String>();
		final String UP = "UP";
		final String DOWN = "DOWN";
		final String LEFT = "LEFT";
		final String RIGHT = "RIGHT";

		if (currentColumnIndex != Configuration.SADREMA_GRID_CELL_MINIMUM_COLUMN_INDEX) {
			possibleDirections.add(LEFT);
		}

		if (currentColumnIndex != Configuration.SADREMA_GRID_CELL_MAXIMUM_COLUMN_INDEX) {
			possibleDirections.add(RIGHT);
		}

		if (currentRowIndex != Configuration.SADREMA_GRID_CELL_MINIMUM_ROW_INDEX) {
			possibleDirections.add(UP);
		}

		if (currentRowIndex != Configuration.SADREMA_GRID_CELL_MAXIMUM_ROW_INDEX) {
			possibleDirections.add(DOWN);
		}

		if (numberOfGeneratedCells < numberOfSADREMAGridCellToGenerate) {
			do {

				int possibleDirectionsSize = possibleDirections.size();
				int randomDirectionIndex = ThreadLocalRandom.current().nextInt(0, (int) possibleDirectionsSize);

				Object[] possibleDirectionsArray = possibleDirections.toArray();
				String direction = possibleDirectionsArray[randomDirectionIndex].toString();

				if (direction.equals(UP)) {
					currentRowIndex--;
				} else if (direction.equals(RIGHT)) {
					currentColumnIndex++;
				} else if (direction.equals(LEFT)) {
					currentColumnIndex--;
				} else if (direction.equals(DOWN)) {
					currentRowIndex++;
				}

				sadremaGridCell = new SADREMAGridCell(currentRowIndex, currentColumnIndex);

				randomSetSADREMAGridCellForBeam.add(sadremaGridCell);

				possibleDirections.clear();

				if (currentColumnIndex != Configuration.SADREMA_GRID_CELL_MINIMUM_COLUMN_INDEX
						&& !direction.equals("LEFT")) {
					possibleDirections.add(LEFT);
				}

				if (currentColumnIndex != Configuration.SADREMA_GRID_CELL_MAXIMUM_COLUMN_INDEX
						&& !direction.equals("RIGHT")) {
					possibleDirections.add(RIGHT);
				}

				if (currentRowIndex != Configuration.SADREMA_GRID_CELL_MINIMUM_ROW_INDEX && !direction.equals("UP")) {
					possibleDirections.add(UP);
				}

				if (currentRowIndex != Configuration.SADREMA_GRID_CELL_MAXIMUM_ROW_INDEX && !direction.equals("DOWN")) {
					possibleDirections.add(DOWN);
				}

				numberOfGeneratedCells++;

			} while (numberOfGeneratedCells < numberOfSADREMAGridCellToGenerate);
		}

		return randomSetSADREMAGridCellForBeam;
	}

	public static RandomMGRSWGS84Coordinates getRandomLocation() {

		Boolean found = Boolean.FALSE;
		double latitude = 0;
		double longitude = 0;

		do {
			final Random random = new Random();
			double u = random.nextDouble();
			double v = random.nextDouble();
			latitude = Math.toDegrees(Math.acos(u * 2 - 1)) - 90;
			longitude = 360 * v - 180;

			Boolean latitudeOk = (latitude >= Configuration.MGRS_WGS84_LATITUDE_LOWER_LIMIT_INCLUSIVE)
					&& (latitude <= Configuration.MGRS_WGS84_LATITUDE_UPPER_LIMIT_INCLUSIVE);
			Boolean longitudeOk = (longitude >= Configuration.MGRS_WGS84_LONGITUDE_LOWER_LIMIT_INCLUSIVE)
					&& (longitude <= Configuration.MGRS_WGS84_LONGITUDE_UPPER_LIMIT_INCLUSIVE);

			found = latitudeOk && longitudeOk;

		} while (!found);

		return new RandomMGRSWGS84Coordinates(latitude, longitude);
	}

	public static String getJSON(final Object pObject) {
		try {
			return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(pObject);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static String getHomeDirectory() {
		return System.getProperty("user.home");
	}

}
