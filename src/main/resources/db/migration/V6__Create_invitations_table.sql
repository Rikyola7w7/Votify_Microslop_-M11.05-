CREATE TABLE invitations (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    project_id BIGINT NOT NULL,
    project_name VARCHAR(255) NOT NULL,
    competition_id BIGINT NOT NULL REFERENCES competition(id),
    invited_by_id BIGINT NOT NULL REFERENCES "user"(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    creation_date TIMESTAMP NOT NULL,
    expiration_date TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES "user"(id),
    CONSTRAINT fk_competition FOREIGN KEY (competition_id) REFERENCES competition(id),
    CONSTRAINT fk_invited_by FOREIGN KEY (invited_by_id) REFERENCES "user"(id)
);

CREATE INDEX idx_invitations_user_id ON invitations(user_id);
CREATE INDEX idx_invitations_project_id ON invitations(project_id);
CREATE INDEX idx_invitations_status ON invitations(status);
