package app.beamcatcher.modelserver.configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {

	public static final int MINIMUM_SLOT_INDEX = 1;
	public static final int MAXIMUM_SLOT_INDEX = 12;
	public static final int WORLD_MINIMUM_NUMBER_OF_USERS = 0;
	public static final int WORLD_MAXIMUM_NUMBER_OF_USERS = 12;

	public static final String EVENT_IDENTIFIER_USER_JOINED = "USER_JOINED";
	public static final String EVENT_IDENTIFIER_USER_LEFT = "USER_LEFT";
	public static final String EVENT_IDENTIFIER_USER_PLACED_MARKER = "USER_PLACED_MARKER";
	public static final String EVENT_IDENTIFIER_REGEX = "^(" + EVENT_IDENTIFIER_USER_JOINED + "|"
			+ EVENT_IDENTIFIER_USER_PLACED_MARKER + "|" + EVENT_IDENTIFIER_USER_LEFT + ")$";

	public static final String HOME_DIR = System.getProperty("user.home") + "/gameofcode";
	public static final String MODEL_SERVER_BASE_DIR = HOME_DIR + "/modelserver";
	public static final String MODEL_SERVER_IO_DIR = MODEL_SERVER_BASE_DIR + "/io";
	public static final String MODEL_SERVER_IO_DIR_SADREMA = MODEL_SERVER_IO_DIR + "/sadrema";
	public static final String MODEL_SERVER_IO_DIR_SADREMA_OUT = MODEL_SERVER_IO_DIR_SADREMA + "/out";
	public static final String MODEL_SERVER_IO_DIR_SADREMA_OUT_SIGNAL = MODEL_SERVER_IO_DIR_SADREMA_OUT + "/signal";
	public static final String MODEL_SERVER_IO_DIR_SADREMA_OUT_DATA = MODEL_SERVER_IO_DIR_SADREMA_OUT + "/data";
	public static final String MODEL_SERVER_IO_DIR_SADREMA_OUT_PNG = MODEL_SERVER_IO_DIR_SADREMA_OUT + "/png";
	public static final String MODEL_SERVER_PROPERTIES_FILENAME = "modelserver.conf";

	public static final String MODEL_SERVER_LOG_DIR = MODEL_SERVER_BASE_DIR + "/log";
	public static final String MODEL_SERVER_EVENT_LOG = MODEL_SERVER_LOG_DIR + "/event.log";

	public static final String MODEL_SERVER_IO_DIR_EVENT_SERVER = MODEL_SERVER_IO_DIR + "/eventserver";
	public static final String MODEL_SERVER_IO_DIR_EVENT_SERVER_IN = MODEL_SERVER_IO_DIR_EVENT_SERVER + "/in";
	public static final String MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_SIGNAL = MODEL_SERVER_IO_DIR_EVENT_SERVER_IN
			+ "/signal";
	public static final String MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_DATA = MODEL_SERVER_IO_DIR_EVENT_SERVER_IN + "/data";

	public static final long MGRS_WGS84_LATITUDE_LOWER_LIMIT_INCLUSIVE = -80L;
	public static final long MGRS_WGS84_LATITUDE_UPPER_LIMIT_INCLUSIVE = 84L;
	public static final long MGRS_WGS84_LONGITUDE_LOWER_LIMIT_INCLUSIVE = -180L;
	public static final long MGRS_WGS84_LONGITUDE_UPPER_LIMIT_INCLUSIVE = 180L;

	public static final int SADREMA_GRID_CELL_MINIMUM_ROW_INDEX = 1;
	public static final int SADREMA_GRID_CELL_MAXIMUM_ROW_INDEX = 20;
	public static final int SADREMA_GRID_CELL_MINIMUM_COLUMN_INDEX = 1;
	public static final int SADREMA_GRID_CELL_MAXIMUM_COLUMN_INDEX = 60;

	public static final int MGRS_GRID_ZONE_DESIGNATOR_UTM_ZONE_MINIMUM = 1;
	public static final int MGRS_GRID_ZONE_DESIGNATOR_UTM_ZONE_MAXIMUM = 60;

	public static final int MGRS_GRID_ZONE_DESIGNATOR_API_ARGCODE_FOR_RESOLUTION_IN_SQUARE_KMS = 1;

	public static final String MGRS_GRID_ZONE_DESIGNATOR_LATITUDE_BAND_LETTER_REGEXP = "^[CDEFGHJKLMNPQRSTUVWX]$";

	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_C = 20;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_D = 19;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_E = 18;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_F = 17;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_G = 16;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_H = 15;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_J = 14;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_K = 13;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_L = 12;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_M = 11;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_N = 10;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_P = 9;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_Q = 8;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_R = 7;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_S = 6;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_T = 5;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_U = 4;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_V = 3;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_W = 2;
	public static final int SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_X = 1;

	public static Map<Character, Integer> SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP;

	static {
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP = new HashMap<Character, Integer>();
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('C',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_C);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('D',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_D);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('E',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_E);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('F',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_F);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('G',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_G);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('H',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_H);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('J',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_J);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('K',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_K);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('L',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_L);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('M',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_M);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('N',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_N);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('P',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_P);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('Q',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_Q);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('R',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_R);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('S',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_S);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('T',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_T);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('U',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_U);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('V',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_V);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('W',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_W);
		SADREMA_GRID_CELL_ROW_INDEX_PER_MGRS_LATITUDE_BAND_LETTER_MAP.put('X',
				SADREMA_GRID_CELL_ROW_INDEX_FOR_MGRS_LATITUDE_BAND_LETTER_MAP_X);
	}

	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_20 = 'C';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_19 = 'D';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_18 = 'E';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_17 = 'F';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_16 = 'G';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_15 = 'H';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_14 = 'J';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_13 = 'K';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_12 = 'L';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_11 = 'M';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_10 = 'N';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_9 = 'P';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_8 = 'Q';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_7 = 'R';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_6 = 'S';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_5 = 'T';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_4 = 'U';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_3 = 'V';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_2 = 'W';
	public static final Character SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_1 = 'X';

	public static Map<Integer, Character> SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP;

	static {
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP = new HashMap<Integer, Character>();
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(20,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_20);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(19,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_19);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(18,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_18);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(17,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_17);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(16,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_16);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(15,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_15);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(14,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_14);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(13,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_13);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(12,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_12);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(11,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_11);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(10,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_10);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(9,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_9);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(8,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_8);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(7,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_7);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(6,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_6);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(5,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_5);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(4,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_4);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(3,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_3);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(2,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_2);
		SADREMA_MGRS_LATITUDE_BAND_LETTER_PER_GRID_CELL_ROW_INDEX_MAP.put(1,
				SADREMA_MGRS_LATITUDE_BAND_LETTER_FOR_GRID_CELL_ROW_INDEX_MAP_1);
	}

	public static final long MARKER_MINIMUM_REQUESTED_UNITS = 1L;
	public static final long MARKER_MAXIMUM_REQUESTED_UNITS = 10L;

	public static final long MARKER_MINIMUM_PRIORITY = 1L;
	public static final long MARKER_MAXIMUM_PRIORITY = 10L;

	public static final int USER_USERNAME_MIN_LENGTH = 1;
	public static final int USER_USERNMAE_MAX_LENGTH = 50;

	public static final int USER_MARKER_MIN = 0;
	public static final int USER_MARKER_MAX = 4;

	public static final int BEAM_NAME_MIN_LENGTH = 1;
	public static final int BEAM_NAME_MAX_LENGTH = 100;
	public static final long BEAM_MINIMUM_DELIVERED_UNITS = 1;

	public static final int BEAM_MIN_SIZE_IN_CELLS = 1;
	public static final int BEAM_MAX_SIZE_IN_CELLS = 25;
	public static final int SATELLITE_NAME_MIN_LENGTH = 1;
	public static final int SATELLITE_NAME_MAX_LENGTH = 50;
	public static final int SATELLITE_MINIMUM_NUMBER_OF_BEAMS = 0;
	public static final int SATELLITE_MAXIMUM_NUMBER_OF_BEAMS = 5;
	public static final int SATELLITE_MINIMUM_PRODUCED_UNITS = 0;
	public static final int SATELLITE_MAXIMUM_PRODUCED_UNITS = 100000;
	public static final int WORLD_MINIMUM_NUMBER_OF_SATELLITES = 3;
	public static final int WORLD_MAXIMUM_NUMBER_OF_SATELLITES = 3;

	public static final String WORLD_SATELLITE_1_NAME = "SUPERSAT-1";
	public static final int WORLD_SATELLITE_1_PRODUCED_UNITS = 42;

	public static final String WORLD_SATELLITE_2_NAME = "SUPERSAT-2";
	public static final int WORLD_SATELLITE_2_PRODUCED_UNITS = 42;

	public static final String WORLD_SATELLITE_3_NAME = "SUPERSAT-3";
	public static final int WORLD_SATELLITE_3_PRODUCED_UNITS = 42;
	public static final int EVENT_SERVER_HTTP_ENDPOINT_PORT = 8090;

	public static final String SADREMA_CSV_SIGNAL_SUFFIX = "signal";
	public static final String SADREMA_CSV_SATELLITES_SIGNAL_SUFFIX = "satellites." + SADREMA_CSV_SIGNAL_SUFFIX;
	public static final String SADREMA_CSV_MARKERS_SIGNAL_SUFFIX = "markers." + SADREMA_CSV_SIGNAL_SUFFIX;
	public static final long EVENT_POLLING_FREQUENCY_IN_MILLISECONDS = 1000;
	public static final long SADREMA_SOLUTION_RETRIEVAL_FREQUENCY_IN_MILLISECONDS = 5000;

	public static final Integer SLOT_1_ID = 1;
	public static final Integer SLOT_2_ID = 2;
	public static final Integer SLOT_3_ID = 3;
	public static final Integer SLOT_4_ID = 4;
	public static final Integer SLOT_5_ID = 5;
	public static final Integer SLOT_6_ID = 6;
	public static final Integer SLOT_7_ID = 7;
	public static final Integer SLOT_8_ID = 8;
	public static final Integer SLOT_9_ID = 9;
	public static final Integer SLOT_10_ID = 10;
	public static final Integer SLOT_11_ID = 11;
	public static final Integer SLOT_12_ID = 12;
	public static final Integer SLOT_13_ID = 13;
	public static final Integer SLOT_14_ID = 14;
	public static final Integer SLOT_15_ID = 15;
	public static final Integer SLOT_16_ID = 16;
	public static final Integer SLOT_17_ID = 17;
	public static final Integer SLOT_18_ID = 18;
	public static final Integer SLOT_19_ID = 19;
	public static final Integer SLOT_20_ID = 20;

	public static StringBuffer getProperties() throws IllegalArgumentException, IllegalAccessException {
		final StringBuffer stringBuffer = new StringBuffer();
		final List<Field> staticFields = getStaticFields();
		for (Field staticField : staticFields) {
			final String staticFieldName = staticField.getName();
			final Class<?> staticFieldType = staticField.getType();

			Object fieldValue = null;

			if (staticFieldType == int.class) {
				fieldValue = staticField.getInt(null);
			} else if (staticFieldType == long.class) {
				fieldValue = staticField.getLong(null);
			} else if (staticFieldType == String.class) {
				fieldValue = staticField.get(null);
			}

			if (fieldValue != null) {
				stringBuffer.append(staticFieldName + "=" + fieldValue.toString());
				stringBuffer.append(System.getProperty("line.separator"));
			}

		}
		return stringBuffer;
	}

	private static List<Field> getStaticFields() {
		final Field[] declaredFields = Configuration.class.getDeclaredFields();
		final List<Field> staticFields = new ArrayList<Field>();
		for (Field field : declaredFields) {
			if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
				staticFields.add(field);
			}
		}
		return staticFields;
	}

}
