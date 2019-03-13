package app.beamcatcher.modelserver.io.sadrema.out;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uma.jmetal.runner.multiobjective.chetan.NSGAIIBinaryRunner_SDRM;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.io.sadrema.out.awt.MGRSImageSaver;
import app.beamcatcher.modelserver.model.Beam;
import app.beamcatcher.modelserver.model.Marker;

public class SadremaHelper {

	private static final Logger logger = LoggerFactory.getLogger(SadremaHelper.class);

	public static File[] obtainSolution(final List<Marker> pMarker) {

		File beamsTXTFile = null;
		final File[] result = new File[2];

		try {
			logger.info("Computing...");
			long startTime = System.currentTimeMillis();
			final String filename = "markers.csv";
			final String absolutePathToMarkers = Configuration.MODEL_SERVER_IO_DIR_SADREMA_OUT_DATA + "/" + filename;
			final Long totalNumberOfMarkers = Files.lines(Paths.get(absolutePathToMarkers)).count();
			String[] args = new String[] { filename };
			NSGAIIBinaryRunner_SDRM.main(args);
			Boolean sadremaFinished = Boolean.FALSE;
			final String fileToLookFor = Configuration.MODEL_SERVER_IO_DIR_SADREMA_IN_DATA + "/beams.txt";
			beamsTXTFile = new File(fileToLookFor);
			do {
				sadremaFinished = beamsTXTFile.exists();
				Thread.sleep(100);
			} while (!sadremaFinished);
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			logger.info("Optimal solution for " + totalNumberOfMarkers + " marker(s) found in " + elapsedTime + " ms");
			result[0] = beamsTXTFile;
			result[1] = archiveSolution(absolutePathToMarkers, fileToLookFor, pMarker);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		return result;

	}

	private static File archiveSolution(final String pMarkersFile, final String pBeamsFile,
			final List<Marker> pMarker) {
		final Date nowDate = new Date();
		final Long nowLong = nowDate.getTime();
		final String directoryToCreate = Configuration.MODEL_SERVER_IO_DIR_SADREMA_ARCHIVE + "/" + nowLong;
		final File archiveDirectory = new File(directoryToCreate);
		archiveDirectory.mkdir();
		File beamsFileDestination = null;
		try {
			final File markersFileSource = new File(pMarkersFile);
			final File markersFileDestination = new File(directoryToCreate + "/" + markersFileSource.getName());
			final File beamsFileSource = new File(pBeamsFile);
			beamsFileDestination = new File(directoryToCreate + "/" + beamsFileSource.getName());
			Files.copy(markersFileSource.toPath(), markersFileDestination.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
			Files.copy(beamsFileSource.toPath(), beamsFileDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return beamsFileDestination;
	}

	public static void doPNG(final List<Marker> pMarker, final String pAbsolutePathPNG,
			final Map<String, Set<Beam>> pBeamsPerSatellite) {
		MGRSImageSaver.savePNG(pMarker, pAbsolutePathPNG, pBeamsPerSatellite);
	}

}
