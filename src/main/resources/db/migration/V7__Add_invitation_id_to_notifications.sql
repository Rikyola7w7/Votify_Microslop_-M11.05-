ALTER TABLE notifications ADD COLUMN invitation_id BIGINT;
ALTER TABLE notifications ADD CONSTRAINT fk_invitation FOREIGN KEY (invitation_id) REFERENCES invitations(id);
