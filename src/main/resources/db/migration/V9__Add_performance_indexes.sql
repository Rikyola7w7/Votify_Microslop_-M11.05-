-- Performance indexes for query optimization
CREATE INDEX IF NOT EXISTS idx_vote_project_id ON vote(project_id);
CREATE INDEX IF NOT EXISTS idx_vote_user_id ON vote(user_id);
CREATE INDEX IF NOT EXISTS idx_vote_category_id ON vote(category_id);
CREATE INDEX IF NOT EXISTS idx_vote_user_category ON vote(user_id, category_id);
CREATE INDEX IF NOT EXISTS idx_project_competition_id ON project(competition_id);
CREATE INDEX IF NOT EXISTS idx_project_manual_vote_count ON project(manual_vote_count) WHERE manual_vote_count IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_category_competition_id ON category(competition_id);
CREATE INDEX IF NOT EXISTS idx_notification_user_id ON notification(user_id);
CREATE INDEX IF NOT EXISTS idx_notification_competition_id ON notification(competition_id);
CREATE INDEX IF NOT EXISTS idx_invitation_user_id ON invitation(user_id);
CREATE INDEX IF NOT EXISTS idx_checklist_vote_voter_id ON checklist_vote(voter_id);
CREATE INDEX IF NOT EXISTS idx_checklist_item_competition_id ON checklist_item(competition_id);