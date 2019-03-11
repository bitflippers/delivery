package app.beamcatcher.modelserver.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.beamcatcher.modelserver.configuration.Configuration;

public class Game {

	private static final Logger logger = LoggerFactory.getLogger(Game.class);

	@NotNull
	private UUID uuid;

	@NotNull
	@Size(min = 0, max = Configuration.MAXIMUM_SLOT_INDEX)
	@Valid
	private Set<Slot> freeSlots;

	@NotNull
	@Size(min = 0, max = Configuration.MAXIMUM_SLOT_INDEX)
	@Valid
	private Set<Slot> usedSlots;

	public Game() {

	}

	public Game(final Set<Slot> pFreeSlots, final Set<Slot> pUsedSlots) {
		super();
		this.uuid = UUID.randomUUID();
		this.freeSlots = pFreeSlots;
		this.usedSlots = pUsedSlots;
	}

	public UUID getUuid() {
		return uuid;
	}

	public Set<Slot> getFreeSlots() {
		return new HashSet<Slot>(freeSlots);
	}

	public Set<Slot> getUsedSlots() {
		return new HashSet<Slot>(usedSlots);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Game) {
			return this.uuid.equals(((Game) obj).uuid);
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

	public void slotFromFreeToUsed(final Slot pSlot) {
		this.freeSlots.remove(pSlot);
		this.usedSlots.add(pSlot);
		final Integer numberOfFreeSlots = this.freeSlots.size();
		final Integer numberOfUsedSlots = this.usedSlots.size();
		final Boolean slotsOK = (numberOfFreeSlots + numberOfUsedSlots) == Configuration.MAXIMUM_SLOT_INDEX;
		if (!slotsOK) {
			logger.error("free slots (" + numberOfFreeSlots + ") + used slots (" + numberOfUsedSlots
					+ ") not equal to total slots (" + Configuration.MAXIMUM_SLOT_INDEX + ")");
			System.exit(-1);
		}
	}

	public void slotFromUsedToFree(final Slot pSlot) {
		this.usedSlots.remove(pSlot);
		this.freeSlots.add(pSlot);
		final Integer numberOfFreeSlots = this.freeSlots.size();
		final Integer numberOfUsedSlots = this.usedSlots.size();
		final Boolean slotsOK = (numberOfFreeSlots + numberOfUsedSlots) == Configuration.MAXIMUM_SLOT_INDEX;
		if (!slotsOK) {
			logger.error("free slots (" + numberOfFreeSlots + ") + used slots (" + numberOfUsedSlots
					+ ") not equal to total slots (" + Configuration.MAXIMUM_SLOT_INDEX + ")");
			System.exit(-1);
		}
	}
}
