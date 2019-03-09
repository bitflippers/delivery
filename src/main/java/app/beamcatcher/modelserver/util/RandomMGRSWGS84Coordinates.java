package app.beamcatcher.modelserver.util;

public class RandomMGRSWGS84Coordinates {

	private Double latitude;
	private Double longitude;

	public RandomMGRSWGS84Coordinates(Double pLatitude, Double pLongitude) {
		this.latitude = pLatitude;
		this.longitude = pLongitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

}
