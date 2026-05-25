-- V7: Migrate from PostgreSQL enums to entity-based certificate types

-- Create certificate_type_entity table
CREATE TABLE certificate_type_entity (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    color_code VARCHAR(7),
    icon_type VARCHAR(50)
);

-- Create ranking_type_entity table
CREATE TABLE ranking_type_entity (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- Insert predefined certificate types
INSERT INTO certificate_type_entity (code, display_name, description, color_code, icon_type)
VALUES 
    ('PARTICIPANT', 'Certificado de Participante', 'Certificate for all competition participants', '#4a90e2', 'PARTICIPANT'),
    ('JUDGE_WINNER', 'Ganador - Ranking de Jueces', '1st place winner in judges ranking', '#dab517', 'JUDGE_WINNER'),
    ('POPULAR_WINNER', 'Ganador - Ranking Popular', '1st place winner in popular ranking', '#ff6b6b', 'POPULAR_WINNER');

-- Insert predefined ranking types
INSERT INTO ranking_type_entity (code, display_name, description)
VALUES 
    ('JUDGES_RANKING', '1st Place - Judges Ranking', 'Top ranked project by judges'),
    ('POPULAR_RANKING', '1st Place - Popular Vote', 'Top ranked project by popular vote');

-- Add new foreign key columns to certificate table
ALTER TABLE certificate 
ADD COLUMN certificate_type_id BIGINT,
ADD COLUMN ranking_type_id BIGINT;

-- Populate certificate_type_id from old enum values using mapping
UPDATE certificate 
SET certificate_type_id = (
    SELECT id FROM certificate_type_entity 
    WHERE code = certificate.certificate_type::text
);

-- Populate ranking_type_id from old enum values using mapping
UPDATE certificate 
SET ranking_type_id = (
    SELECT id FROM ranking_type_entity 
    WHERE code = certificate.ranking_type::text
);

-- Add foreign key constraints
ALTER TABLE certificate 
ADD CONSTRAINT fk_certificate_type FOREIGN KEY (certificate_type_id) 
    REFERENCES certificate_type_entity(id) ON DELETE RESTRICT,
ADD CONSTRAINT fk_ranking_type FOREIGN KEY (ranking_type_id) 
    REFERENCES ranking_type_entity(id) ON DELETE RESTRICT;

-- Make the new columns NOT NULL now that they're populated
ALTER TABLE certificate 
ALTER COLUMN certificate_type_id SET NOT NULL,
ALTER COLUMN ranking_type_id SET NOT NULL;

-- Drop old enum columns
ALTER TABLE certificate 
DROP COLUMN certificate_type,
DROP COLUMN ranking_type;

-- Drop old PostgreSQL enum types
DROP TYPE IF EXISTS certificate_type CASCADE;
DROP TYPE IF EXISTS ranking_type_enum CASCADE;

-- Create indexes for performance on the new entity tables
CREATE INDEX idx_certificate_type_code ON certificate_type_entity(code);
CREATE INDEX idx_ranking_type_code ON ranking_type_entity(code);

-- Create indexes for the new foreign keys
CREATE INDEX idx_certificate_type_fk ON certificate(certificate_type_id);
CREATE INDEX idx_certificate_ranking_fk ON certificate(ranking_type_id);

-- Create index for combined queries
CREATE INDEX idx_certificate_type_ranking ON certificate(certificate_type_id, ranking_type_id);
