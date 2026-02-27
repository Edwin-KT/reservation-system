INSERT INTO slot (id, name, date, time, available) VALUES (1, 'Sala A', '2026-01-25', '10:00', true);
INSERT INTO slot (id, name, date, time, available) VALUES (2, 'Sala A', '2026-01-25', '12:00', true);
INSERT INTO slot (id, name, date, time, available) VALUES (3, 'Sala A', '2026-01-25', '14:00', true);
INSERT INTO slot (id, name, date, time, available) VALUES (4, 'Sala B', '2026-01-25', '10:00', true);
INSERT INTO slot (id, name, date, time, available) VALUES (5, 'Sala B', '2026-01-25', '12:00', true);
INSERT INTO slot (id, name, date, time, available) VALUES (6, 'Sala B', '2026-01-25', '14:00', true);
INSERT INTO slot (id, name, date, time, available) VALUES (7, 'Sala C', '2026-01-26', '10:00', true);
INSERT INTO slot (id, name, date, time, available) VALUES (8, 'Sala C', '2026-01-26', '12:00', true);
INSERT INTO slot (id, name, date, time, available) VALUES (9, 'Sala C', '2026-01-26', '14:00', true);
INSERT INTO slot (id, name, date, time, available) VALUES (10, 'Sala D', '2026-01-26', '10:00', true);

ALTER SEQUENCE slot_seq RESTART WITH 11;
ALTER SEQUENCE reservation_seq RESTART WITH 1;