package app.beamcatcher.modelserver.test.sadrema.awt;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

import app.beamcatcher.modelserver.model.Marker;

public class MGRSImageSaver {

	public static void savePNG(final Set<Marker> setMarkerCellSpace, final String pFilename) {

		final MGRSCanvas mgrsCanvas = new MGRSCanvas(setMarkerCellSpace);
		final BufferedImage bufferedImage = new BufferedImage(mgrsCanvas.getWidth(), mgrsCanvas.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		final Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();

		mgrsCanvas.paint(graphics2D);

		final String fileType = "png";
		final File imageFile = new File(pFilename);

		try {
			ImageIO.write(bufferedImage, fileType, imageFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
