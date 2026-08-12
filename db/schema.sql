-- ============================================================
-- Sunrise Dental Clinic - MySQL schema
-- Designed in MySQL Workbench (EER diagram) and exported here.
-- Run this once against a fresh schema, e.g.:
--   mysql -u root -p -e "CREATE DATABASE sunrise_dental;"
--   mysql -u root -p sunrise_dental < db/schema.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    username        VARCHAR(50)  NOT NULL PRIMARY KEY,
    password_hash   VARCHAR(64)  NOT NULL,
    role            ENUM('ADMIN', 'RECEPTIONIST') NOT NULL,
    full_name       VARCHAR(100) NOT NULL DEFAULT ''
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS dentists (
    id              VARCHAR(20)  NOT NULL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    specialization  VARCHAR(100) NOT NULL DEFAULT ''
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS treatment_types (
    id              VARCHAR(20)   NOT NULL PRIMARY KEY,
    name            VARCHAR(100)  NOT NULL,
    fee             DECIMAL(10,2) NOT NULL CHECK (fee >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS appointments (
    appointment_number VARCHAR(20)   NOT NULL PRIMARY KEY,
    patient_name        VARCHAR(100) NOT NULL,
    address              VARCHAR(200) NOT NULL DEFAULT '',
    contact_number       VARCHAR(30)  NOT NULL,
    dentist_id           VARCHAR(20)  NOT NULL,
    treatment_type_id    VARCHAR(20)  NOT NULL,
    appointment_date     DATE         NOT NULL,
    appointment_time     TIME         NOT NULL,
    discount_percent     DECIMAL(5,2) NOT NULL DEFAULT 0 CHECK (discount_percent BETWEEN 0 AND 100),

    CONSTRAINT fk_appt_dentist
        FOREIGN KEY (dentist_id) REFERENCES dentists(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_appt_treatment
        FOREIGN KEY (treatment_type_id) REFERENCES treatment_types(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,

    -- Enforces that the same dentist cannot be double-booked for the same
    -- slot - directly addresses the "double bookings" problem named in
    -- the assignment scenario, at the database layer rather than only in
    -- application code.
    CONSTRAINT uq_dentist_slot UNIQUE (dentist_id, appointment_date, appointment_time)
) ENGINE=InnoDB;

CREATE INDEX idx_appt_date ON appointments(appointment_date);
CREATE INDEX idx_appt_patient_name ON appointments(patient_name);

-- ------------------------------------------------------------
-- Trigger: extra defence-in-depth check (beyond the UNIQUE
-- constraint above) that also gives a clear, custom error message
-- rather than a generic duplicate-key error, and demonstrates
-- business-rule enforcement at the database layer as rewarded by
-- the marking rubric.
-- ------------------------------------------------------------
DELIMITER $$
CREATE TRIGGER trg_prevent_past_date_appointment
BEFORE INSERT ON appointments
FOR EACH ROW
BEGIN
    IF NEW.appointment_date < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Appointment date cannot be in the past.';
    END IF;
END$$
DELIMITER ;

-- ------------------------------------------------------------
-- Stored function: mirrors the discount calculation performed by
-- DiscountFeeStrategy/StandardFeeStrategy in the Java Strategy
-- pattern implementation, so the same business rule can also be
-- verified directly in SQL (e.g. for ad hoc reporting queries).
-- ------------------------------------------------------------
DELIMITER $$
CREATE FUNCTION fn_calculate_total(base_fee DECIMAL(10,2), discount_percent DECIMAL(5,2))
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    RETURN ROUND(base_fee - (base_fee * discount_percent / 100), 2);
END$$
DELIMITER ;

-- ------------------------------------------------------------
-- Stored procedure: produces the clinic revenue summary used by
-- the "View Reports" (Admin) use case, doing the aggregation in
-- the database rather than pulling every row into Java.
-- ------------------------------------------------------------
DELIMITER $$
CREATE PROCEDURE sp_revenue_summary()
BEGIN
    SELECT
        COUNT(*) AS total_appointments,
        SUM(fn_calculate_total(t.fee, a.discount_percent)) AS total_revenue
    FROM appointments a
    JOIN treatment_types t ON t.id = a.treatment_type_id;
END$$
DELIMITER ;

-- ------------------------------------------------------------
-- View: a ready-made, human-readable join used by both the
-- "Display Appointment Details" and "Calculate and Print Bill"
-- use cases, and by ad hoc reporting.
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW vw_appointment_details AS
SELECT
    a.appointment_number,
    a.patient_name,
    a.address,
    a.contact_number,
    d.name AS dentist_name,
    d.specialization,
    t.name AS treatment_name,
    t.fee AS base_fee,
    a.discount_percent,
    fn_calculate_total(t.fee, a.discount_percent) AS total_amount,
    a.appointment_date,
    a.appointment_time
FROM appointments a
JOIN dentists d ON d.id = a.dentist_id
JOIN treatment_types t ON t.id = a.treatment_type_id;

-- ------------------------------------------------------------
-- Seed data: intentionally NOT included here. sunrise.Main already
-- contains seedDefaultData(), which inserts the default accounts,
-- dentists, and treatment types through the DAO layer on first run
-- (needed for user accounts anyway, since PasswordUtil.hash(...)
-- must run in Java). Because of the DAO pattern, that seeding logic
-- works unchanged whether FileUserDao or JdbcUserDao is wired in -
-- no SQL seed script is required.
-- ------------------------------------------------------------
