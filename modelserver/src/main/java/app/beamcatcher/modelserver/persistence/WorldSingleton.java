package app.beamcatcher.modelserver.persistence;

import java.util.HashSet;
import java.util.Set;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.model.Game;
import app.beamcatcher.modelserver.model.Satellite;
import app.beamcatcher.modelserver.model.Slot;
import app.beamcatcher.modelserver.model.World;

public class WorldSingleton {

	public static volatile World INSTANCE;

	static {

		final Set<Satellite> setSatellite = new HashSet<Satellite>();

		final Satellite SATELLITE_1 = new Satellite(Configuration.WORLD_SATELLITE_1_NAME,
				Configuration.WORLD_SATELLITE_1_PRODUCED_UNITS);
		final Satellite SATELLITE_2 = new Satellite(Configuration.WORLD_SATELLITE_2_NAME,
				Configuration.WORLD_SATELLITE_2_PRODUCED_UNITS);
		final Satellite SATELLITE_3 = new Satellite(Configuration.WORLD_SATELLITE_3_NAME,
				Configuration.WORLD_SATELLITE_3_PRODUCED_UNITS);

		setSatellite.add(SATELLITE_1);
		setSatellite.add(SATELLITE_2);
		setSatellite.add(SATELLITE_3);

		final Set<Slot> freeSlots = new HashSet<Slot>();
		final Set<Slot> usedSlots = new HashSet<Slot>();

		final Slot slot1 = new Slot(Configuration.SLOT_1_ID);
		freeSlots.add(slot1);

		final Slot slot2 = new Slot(Configuration.SLOT_2_ID);
		freeSlots.add(slot2);

		final Slot slot3 = new Slot(Configuration.SLOT_3_ID);
		freeSlots.add(slot3);

		final Slot slot4 = new Slot(Configuration.SLOT_4_ID);
		freeSlots.add(slot4);

		final Slot slot5 = new Slot(Configuration.SLOT_5_ID);
		freeSlots.add(slot5);

		final Slot slot6 = new Slot(Configuration.SLOT_6_ID);
		freeSlots.add(slot6);

		final Slot slot7 = new Slot(Configuration.SLOT_7_ID);
		freeSlots.add(slot7);

		final Slot slot8 = new Slot(Configuration.SLOT_8_ID);
		freeSlots.add(slot8);

		final Slot slot9 = new Slot(Configuration.SLOT_9_ID);
		freeSlots.add(slot9);

		final Slot slot10 = new Slot(Configuration.SLOT_10_ID);
		freeSlots.add(slot10);

		final Slot slot11 = new Slot(Configuration.SLOT_11_ID);
		freeSlots.add(slot11);

		final Slot slot12 = new Slot(Configuration.SLOT_12_ID);
		freeSlots.add(slot12);

		final Game game = new Game(freeSlots, usedSlots);

		INSTANCE = new World(setSatellite, game);
	}

	private WorldSingleton() {
	}

}