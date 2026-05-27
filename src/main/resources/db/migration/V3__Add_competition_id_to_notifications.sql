-- Add competition_id column to notifications table
ALTER TABLE notifications ADD COLUMN competition_id BIGINT;

-- Add foreign key constraint
ALTER TABLE notifications ADD CONSTRAINT fk_notifications_competition 
    FOREIGN KEY (competition_id) REFERENCES competition(id) ON DELETE SET NULL;

-- Create index for faster queries
CREATE INDEX idx_notifications_competition_id ON notifications(competition_id);
