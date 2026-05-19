-- PostgreSQL Sequence Initialization Script
-- Resets all sequence generators to prevent duplicate key errors
-- This script runs on every application startup to ensure sequences are in sync with data

-- Reset VOTER sequence to be higher than any existing voter id
SELECT setval('voter_id_seq', (SELECT MAX(id) FROM voter) + 1, false);

-- Reset other sequences for consistency
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users) + 1, false);
SELECT setval('competition_id_seq', (SELECT MAX(id) FROM competition) + 1, false);
SELECT setval('project_id_seq', (SELECT MAX(id) FROM project) + 1, false);
SELECT setval('category_id_seq', (SELECT MAX(id) FROM category) + 1, false);
SELECT setval('vote_id_seq', (SELECT MAX(id) FROM vote) + 1, false);
SELECT setval('judge_id_seq', (SELECT MAX(id) FROM judge) + 1, false);
SELECT setval('project_comment_id_seq', (SELECT MAX(id) FROM project_comment) + 1, false);
