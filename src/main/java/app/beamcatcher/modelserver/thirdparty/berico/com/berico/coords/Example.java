package app.beamcatcher.modelserver.thirdparty.berico.com.berico.coords;

import java.util.HashMap;
import java.util.Map;

import app.beamcatcher.modelserver.thirdparty.berico.gov.nasa.worldwind.geom.Angle;
import app.beamcatcher.modelserver.thirdparty.berico.gov.nasa.worldwind.geom.coords.MGRSCoord;

public class Example {

	public static void main(String[] args) {

		// Latitude (row)
		// X to C, North to South, 90 to -90, 1 to 20
		for (int i = -90; i <= 90; i++) {
			Angle latitude = Angle.fromDegrees(i);
			Angle longitude = Angle.fromDegrees(-150);
			System.out.println(i + "-->" + MGRSCoord.fromLatLon(latitude, longitude, 1).toString());
		}

		// Longitude (column)
		// 1 to 60, West to East, -180 to +180, 1 to 60
//		for (int i = -180; i <= 180; i++) {
//			Angle latitude = Angle.fromDegrees(0);
//			Angle longitude = Angle.fromDegrees(i);
//			System.out.println(i + "-->" + MGRSCoord.fromLatLon(latitude, longitude, 1).toString());
//		}

		System.out.println(getRowIndexFromMGRSString("01F"));

	}

	public static Integer getColumnIndexFromMGRSString(final String pMGRSString) {
		Integer columnIndex;
		if (pMGRSString == null || pMGRSString.isEmpty() || pMGRSString.length() < 2) {
			throw new IllegalArgumentException();
		} else {

			final String longitudeIdentifier = pMGRSString.substring(0, 2);

			columnIndex = Integer.valueOf(longitudeIdentifier);

			if (columnIndex > 60) {
				throw new IllegalArgumentException();
			}

			if (columnIndex < 1) {
				throw new IllegalArgumentException();
			}

		}
		return columnIndex;
	}

	public static Integer getRowIndexFromMGRSString(final String pMGRSString) {
		Integer rowIndex;
		if (pMGRSString == null || pMGRSString.isEmpty() || pMGRSString.length() < 3) {
			throw new IllegalArgumentException();
		} else {

			final Character mgrsZoneLetterLatitude = pMGRSString.charAt(2);

			final Map<Character, Integer> mapMGRSLetterToRowIndex = new HashMap<Character, Integer>();
			mapMGRSLetterToRowIndex.put('C', 20);
			mapMGRSLetterToRowIndex.put('D', 19);
			mapMGRSLetterToRowIndex.put('E', 18);
			mapMGRSLetterToRowIndex.put('F', 17);
			mapMGRSLetterToRowIndex.put('G', 16);
			mapMGRSLetterToRowIndex.put('H', 15);
			mapMGRSLetterToRowIndex.put('J', 14);
			mapMGRSLetterToRowIndex.put('K', 13);
			mapMGRSLetterToRowIndex.put('L', 12);
			mapMGRSLetterToRowIndex.put('M', 11);
			mapMGRSLetterToRowIndex.put('N', 10);
			mapMGRSLetterToRowIndex.put('P', 9);
			mapMGRSLetterToRowIndex.put('Q', 8);
			mapMGRSLetterToRowIndex.put('R', 7);
			mapMGRSLetterToRowIndex.put('S', 6);
			mapMGRSLetterToRowIndex.put('T', 5);
			mapMGRSLetterToRowIndex.put('U', 4);
			mapMGRSLetterToRowIndex.put('V', 3);
			mapMGRSLetterToRowIndex.put('W', 2);
			mapMGRSLetterToRowIndex.put('X', 1);

			rowIndex = mapMGRSLetterToRowIndex.get(mgrsZoneLetterLatitude);

			if (rowIndex == null) {
				throw new IllegalArgumentException();
			}

			if (rowIndex > 20) {
				throw new IllegalArgumentException();
			}

			if (rowIndex < 1) {
				throw new IllegalArgumentException();
			}

		}
		return rowIndex;
	}

	public static void main2(String[] args) {
		String mgrs = Coordinates.mgrsFromLatLon(37.10, -112.12);

		System.out.println(mgrs);

		double[] latLon = Coordinates.latLonFromMgrs("12S");

		System.out.println(String.format("%s, %s", latLon[0], latLon[1]));
	}
}
