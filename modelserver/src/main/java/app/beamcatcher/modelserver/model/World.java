package app.beamcatcher.modelserver.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.beamcatcher.modelserver.configuration.Configuration;

public class World {

	private static final Logger logger = LoggerFactory.getLogger(World.class);

	@NotNull
	private Integer worldModelVersion = 2;

	@Valid
	@NotNull
	private Game game;

	@NotNull
	private UUID uuid;

	@Valid
	@NotNull
	@Size(min = Configuration.WORLD_MINIMUM_NUMBER_OF_USERS, max = Configuration.WORLD_MAXIMUM_NUMBER_OF_USERS)
	private Map<UUID, User> mapUser;

	@Valid
	@NotNull
	@Size(min = Configuration.WORLD_MINIMUM_NUMBER_OF_SATELLITES, max = Configuration.WORLD_MAXIMUM_NUMBER_OF_SATELLITES)
	private Map<UUID, Satellite> mapSatellite;

	@NotNull
	@PastOrPresent
	private Date date;

	public World(final Set<Satellite> pSetSatellite, final Game pGame) {
		this.game = pGame;
		this.uuid = UUID.randomUUID();
		this.date = new Date();
		this.mapUser = new HashMap<UUID, User>();
		this.mapSatellite = new HashMap<UUID, Satellite>();
		for (Satellite satellite : pSetSatellite) {
			this.mapSatellite.put(satellite.getUuid(), satellite);
		}
	}

	public Game getGame() {
		return game;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Map<UUID, User> getMapUser() {
		return new HashMap<UUID, User>(this.mapUser);
	}

	public Map<UUID, Satellite> getMapSatellite() {
		return new HashMap<UUID, Satellite>(this.mapSatellite);
	}

	public UUID getUuid() {
		return uuid;
	}

	public Integer getWorldModelVersion() {
		return worldModelVersion;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof World) {
			return this.uuid.equals(((World) obj).uuid);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	// validations here: only validation related to object existence !
	// no uuids as arguments, natural keys ! otherwise nightmare for debugging !

	public void addNewUser(final String pUsername, final Integer pSlotIdentifier) {
		final Set<UUID> setUserUUID = this.mapUser.keySet();
		Boolean usernameExists = Boolean.FALSE;
		for (UUID userUUID : setUserUUID) {
			final User userUnderTest = this.mapUser.get(userUUID);
			final CharSequence usernameUnderTest = userUnderTest.getUsername();
			if (usernameUnderTest.equals(pUsername)) {
				usernameExists = Boolean.TRUE;
				break;
			}
		}

		final Set<Slot> freeSlots = this.game.getFreeSlots();
		UUID slotUUID = null;
		Slot slotFound = null;
		for (Slot slot : freeSlots) {
			Integer identifier = slot.getIdentifier();
			if (identifier.equals(pSlotIdentifier)) {
				slotUUID = slot.getUuid();
				slotFound = slot;
				break;
			}
		}

		Boolean slotNotFound = (slotUUID == null);

		if (usernameExists) {
			logger.info("Username exists " + pUsername + " ! cannot add user !");
		} else if (slotNotFound) {
			logger.error("Free slot '" + pSlotIdentifier + "' not found !");
		} else {
			final User user = new User(pUsername, slotFound);
			this.game.slotFromFreeToUsed(slotFound);
			this.mapUser.put(user.getUuid(), user);
			this.date = new Date();
		}
	}

	public void addMarkerToUser(final String pUsername, final Double pLatitude, final Double pLongitude,
			final Integer pRequestedUnits, final Integer pPriority) {
		final Set<UUID> setUserUUID = this.mapUser.keySet();
		Boolean usernameExists = Boolean.FALSE;
		UUID userUUIDOfUserThatAddedMarker = null;
		for (UUID userUUID : setUserUUID) {
			final User userUnderTest = this.mapUser.get(userUUID);
			final CharSequence usernameUnderTest = userUnderTest.getUsername();
			if (usernameUnderTest.equals(pUsername)) {
				usernameExists = Boolean.TRUE;
				userUUIDOfUserThatAddedMarker = userUUID;
				break;
			}
		}
		if (!usernameExists) {
			logger.error("User not found: " + pUsername + " ! cannot add marker to user !");
		} else {
			final User user = this.mapUser.get(userUUIDOfUserThatAddedMarker);
			user.addMarker(pLatitude, pLongitude, pRequestedUnits, pPriority);
			this.date = new Date();
		}

	}

	public void addBeamsToSatellite(final String pSatelliteName, final Set<Beam> pSetBeam) {
		final Set<UUID> setSatelliteUUID = this.mapSatellite.keySet();
		Boolean satelliteNameExists = Boolean.FALSE;
		Satellite satellite = null;
		for (UUID satelliteUUID : setSatelliteUUID) {
			satellite = this.mapSatellite.get(satelliteUUID);
			if (satellite.getName().equals(pSatelliteName)) {
				satelliteNameExists = Boolean.TRUE;
				break;
			}
		}
		if (!satelliteNameExists) {
			throw new IllegalArgumentException("Satellite not found: " + pSatelliteName + " ! cannot add beams !");
		}
		satellite.setBeams(pSetBeam);

		this.date = new Date();
	}

	public void removeUser(final String pUsername) {
		final Set<UUID> setUserUUID = this.mapUser.keySet();
		Boolean usernameExists = Boolean.FALSE;
		UUID userUUIDToRemove = null;
		for (UUID userUUID : setUserUUID) {
			final User userUnderTest = this.mapUser.get(userUUID);
			final CharSequence usernameUnderTest = userUnderTest.getUsername();
			if (usernameUnderTest.equals(pUsername)) {
				usernameExists = Boolean.TRUE;
				userUUIDToRemove = userUUID;
				break;
			}
		}
		if (!usernameExists) {
			throw new IllegalArgumentException("Username " + pUsername + " not found ! cannot remove !");
		}
		final User user = this.mapUser.get(userUUIDToRemove);
		final Slot slot = user.getSlot();
		this.game.slotFromUsedToFree(slot);
		this.mapUser.remove(userUUIDToRemove);
		this.date = new Date();
	}

}
