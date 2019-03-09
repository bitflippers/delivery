package app.beamcatcher.modelserver.thirdparty.berico.com.berico.coords;

import app.beamcatcher.modelserver.thirdparty.berico.gov.nasa.worldwind.geom.Angle;
import app.beamcatcher.modelserver.thirdparty.berico.gov.nasa.worldwind.geom.coords.MGRSCoord;

public class Coordinates {
	
	public static String mgrsFromLatLon(double lat, double lon){
		
		Angle latitude = Angle.fromDegrees(lat);
		
		Angle longitude = Angle.fromDegrees(lon);
		
		return MGRSCoord
				.fromLatLon(latitude, longitude)
				.toString();
	}
	
	public static double[] latLonFromMgrs(String mgrs){
		
		MGRSCoord coord = MGRSCoord.fromString(mgrs);
		
		return new double[]{ 
			coord.getLatitude().degrees, 
			coord.getLongitude().degrees 
		};
	}

}