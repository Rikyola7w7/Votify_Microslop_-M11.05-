-- Seed data for Votify development (H2 compatible)

-- Create users
INSERT INTO users (username, name, email, password, birth_date, creation_date) VALUES
('admin', 'Admin User', 'admin@votify.com', '$2a$10$8K1p/a0dR1xqM8K3hQv1aOQJQzqNqJXqJQzqJQzqJQzqJQzqJQzqJ', DATE '1990-01-01', CURRENT_TIMESTAMP),
('judge1', 'Judge One', 'judge1@votify.com', '$2a$10$8K1p/a0dR1xqM8K3hQv1aOQJQzqNqJXqJQzqJQzqJQzqJQzqJQzqJ', DATE '1985-05-15', CURRENT_TIMESTAMP),
('participant1', 'Participant One', 'participant1@votify.com', '$2a$10$8K1p/a0dR1xqM8K3hQv1aOQJQzqNqJXqJQzqJQzqJQzqJQzqJQzqJ', DATE '1995-03-20', CURRENT_TIMESTAMP),
('participant2', 'Participant Two', 'participant2@votify.com', '$2a$10$8K1p/a0dR1xqM8K3hQv1aOQJQzqNqJXqJQzqJQzqJQzqJQzqJQzqJ', DATE '1992-08-10', CURRENT_TIMESTAMP);

-- Create competitions with ACTIVE status
INSERT INTO competition (name, description, start_date, end_date, status, active, event_type, created_by, voter_type, auto_vote, max_votes_per_person, max_votes, judge_weight_multiplier, standard_user_weight_multiplier, comments_enabled, comments_required) VALUES
('Tech Innovation Summit 2026', 'Annual technology innovation competition', CURRENT_TIMESTAMP, DATEADD('DAY', 30, CURRENT_TIMESTAMP), 'ACTIVE', true, 'TECH', 'admin', 'ALL', false, 5, 100, 2.0, 1.0, true, false),
('Design Excellence Award', 'Creative design competition', CURRENT_TIMESTAMP, DATEADD('DAY', 14, CURRENT_TIMESTAMP), 'ACTIVE', true, 'DESIGN', 'admin', 'JUDGES', false, 3, 50, 3.0, 1.0, true, false),
('Startup Pitch Competition', 'Early-stage startup showcase', DATEADD('DAY', -7, CURRENT_TIMESTAMP), DATEADD('DAY', 7, CURRENT_TIMESTAMP), 'PAUSED', true, 'BUSINESS', 'admin', 'ALL', false, 2, 75, 2.5, 1.0, true, false);

-- Create categories
INSERT INTO category (name, description, competition_id) VALUES
('Innovation', 'Technical innovation and creativity', 1),
('Impact', 'Social and business impact', 1),
('Presentation', 'Pitch and presentation quality', 1),
('Best Design', 'Visual and UI/UX design', 2),
('Creativity', 'Creative execution', 2),
('Best Pitch', 'Business pitch quality', 3);

-- Create projects
INSERT INTO project (title, description, competition_id, created_by_user_id, submission_date) VALUES
('AI-Powered Health Monitor', 'Machine learning solution for health monitoring', 1, 3, CURRENT_TIMESTAMP),
('Smart City Dashboard', 'IoT integration for city management', 1, 4, CURRENT_TIMESTAMP),
('E-Commerce Redesign', 'Modern e-commerce platform design', 2, 3, CURRENT_TIMESTAMP);

-- Create judges assignment
INSERT INTO judge (user_id, competition_id, weight_multiplier) VALUES
(1, 1, 2.0),
(2, 1, 2.5),
(1, 2, 3.0),
(2, 2, 2.5),
(1, 3, 2.0);