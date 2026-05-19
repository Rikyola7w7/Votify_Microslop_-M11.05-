-- Add missing weight multiplier columns to competition table
ALTER TABLE competition ADD COLUMN IF NOT EXISTS judge_weight_multiplier DOUBLE PRECISION DEFAULT 1.0;
ALTER TABLE competition ADD COLUMN IF NOT EXISTS standard_user_weight_multiplier DOUBLE PRECISION DEFAULT 1.0;
