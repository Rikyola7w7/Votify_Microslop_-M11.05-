-- Create certificate type enum
CREATE TYPE certificate_type AS ENUM (
    'PARTICIPANT',
    'JUDGE_WINNER',
    'POPULAR_WINNER'
);

-- Create ranking type enum
CREATE TYPE ranking_type_enum AS ENUM (
    'JUDGES_RANKING',
    'POPULAR_RANKING'
);

-- Create certificate table
CREATE TABLE certificate (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    competition_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    certificate_type certificate_type NOT NULL,
    ranking_type ranking_type_enum NOT NULL,
    position INTEGER,
    generated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_certificate_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT fk_certificate_competition FOREIGN KEY (competition_id) REFERENCES competition(id) ON DELETE CASCADE,
    CONSTRAINT fk_certificate_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    CONSTRAINT fk_certificate_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE,
    
    UNIQUE(user_id, competition_id, project_id, certificate_type, ranking_type)
);

-- Create indexes for performance
CREATE INDEX idx_certificate_user ON certificate(user_id);
CREATE INDEX idx_certificate_competition ON certificate(competition_id);
CREATE INDEX idx_certificate_type ON certificate(certificate_type);
CREATE INDEX idx_certificate_date ON certificate(generated_date DESC);
