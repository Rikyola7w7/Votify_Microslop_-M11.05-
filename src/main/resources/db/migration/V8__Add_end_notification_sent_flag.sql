-- Add end_notification_sent flag to competition table
-- This flag tracks whether END_TIME_COMPETITION notification has been sent
-- to prevent duplicate notifications from the scheduler

ALTER TABLE competition ADD COLUMN IF NOT EXISTS end_notification_sent boolean DEFAULT false;
