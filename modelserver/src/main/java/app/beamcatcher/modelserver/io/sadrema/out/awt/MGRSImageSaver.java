package app.beamcatcher.modelserver.io.sadrema.out.awt;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import app.beamcatcher.modelserver.model.Beam;
import app.beamcatcher.modelserver.model.Marker;

public class MGRSImageSaver {

	public static void savePNG(final List<Marker> setMarkerCellSpace, final String pFilename,
			final Map<String, Set<Beam>> pBeamsPerSatellite) {

		final MGRSCanvas mgrsCanvas = new MGRSCanvas(setMarkerCellSpace, pBeamsPerSatellite);
		final BufferedImage bufferedImage = new BufferedImage(mgrsCanvas.getWidth(), mgrsCanvas.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		final Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();

		mgrsCanvas.paint(graphics2D);

		final String fileType = "png";
		final File imageFile = new File(pFilename + "/MGRS_GRID.png");

		try {
			ImageIO.write(bufferedImage, fileType, imageFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
