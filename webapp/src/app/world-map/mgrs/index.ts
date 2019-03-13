export abstract class MGRS {

  public static utmStepDegrees = 6;
  public static columnIndexZeroLatitude = 1;

  public static MGRSBandLetterStepInDegrees = 8;
  public static rowIndexZeroLongitude = 11;

  public static convert(columnIndex: number, rowIndex: number) {
    const latitudeDecimalGridZoneDesignatorNorthWestCorner = -180 + ((columnIndex - MGRS.columnIndexZeroLatitude) * MGRS.utmStepDegrees);
    let longitudeDecimalGridZoneDesignatorNorthWestCorner = 0;

    if (rowIndex <= MGRS.rowIndexZeroLongitude) {
      const increment = (MGRS.MGRSBandLetterStepInDegrees * (MGRS.rowIndexZeroLongitude - rowIndex));
      longitudeDecimalGridZoneDesignatorNorthWestCorner = 0 + increment;
    } else {
      const decrease = MGRS.MGRSBandLetterStepInDegrees * (rowIndex - MGRS.rowIndexZeroLongitude);
      longitudeDecimalGridZoneDesignatorNorthWestCorner = 0 - decrease;
    }

    const x1 = latitudeDecimalGridZoneDesignatorNorthWestCorner;
    const y1 = longitudeDecimalGridZoneDesignatorNorthWestCorner;
    const x2 = latitudeDecimalGridZoneDesignatorNorthWestCorner + MGRS.utmStepDegrees;
    const y2 = longitudeDecimalGridZoneDesignatorNorthWestCorner - MGRS.MGRSBandLetterStepInDegrees;
    return {
      x1, y1, x2, y2,
      longitudeDecimalGridZoneDesignatorNorthWestCorner, latitudeDecimalGridZoneDesignatorNorthWestCorner,
      centerX: (x1 + x2) / 2, centerY: (y1 + y2) / 2
    };
  }
}
