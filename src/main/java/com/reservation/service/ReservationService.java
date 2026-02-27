package com.reservation.service;

import com.reservation.entity.Reservation;
import com.reservation.entity.Slot;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class ReservationService {

    @Inject
    EntityManager entityManager;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public synchronized List<Slot> getAvailableSlots() {
        return entityManager.createQuery("SELECT s FROM Slot s WHERE s.available = true", Slot.class)
                .getResultList();
    }

    @Transactional
    public synchronized String createReservation(String clientToken, Long slotId) {
        Slot slot = entityManager.find(Slot.class, slotId);

        if (slot == null) {
            return "ERROR: Slotul nu exista!";
        }

        if (!slot.isAvailable()) {
            return "ERROR: Slotul nu mai este disponibil!";
        }

        slot.setAvailable(false);
        entityManager.merge(slot);

        Reservation reservation = new Reservation(
                clientToken,
                slot,
                LocalDateTime.now().format(formatter)
        );
        entityManager.persist(reservation);

        return "SUCCESS: Rezervare creata cu ID: " + reservation.getId();
    }

    @Transactional
    public synchronized List<Reservation> getClientReservations(String clientToken) {
        return entityManager.createQuery(
                        "SELECT r FROM Reservation r WHERE r.clientToken = :token",
                        Reservation.class)
                .setParameter("token", clientToken)
                .getResultList();
    }

    @Transactional
    public synchronized String cancelReservation(String clientToken, Long reservationId) {
        Reservation reservation = entityManager.find(Reservation.class, reservationId);

        if (reservation == null) {
            return "ERROR: Rezervarea nu exista!";
        }

        if (!reservation.getClientToken().equals(clientToken)) {
            return "ERROR: Nu puteti anula rezervarea altui client!";
        }

        Slot slot = reservation.getSlot();
        slot.setAvailable(true);
        entityManager.merge(slot);

        entityManager.remove(reservation);

        return "SUCCESS: Rezervarea a fost anulata!";
    }

    public Slot getSlotById(Long slotId) {
        return entityManager.find(Slot.class, slotId);
    }
}