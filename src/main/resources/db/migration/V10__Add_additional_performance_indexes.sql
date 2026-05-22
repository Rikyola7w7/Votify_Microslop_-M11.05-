-- Additional performance indexes for faster count queries and batch operations
CREATE INDEX IF NOT EXISTS idx_vote_user_project_category ON vote(user_id, project_id, category_id);
CREATE INDEX IF NOT EXISTS idx_vote_project_user ON vote(project_id, user_id);
CREATE INDEX IF NOT EXISTS idx_project_custom_position ON project(competition_id, custom_position) WHERE custom_position IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_project_category_ids ON project_category(project_id, category_id);
CREATE INDEX IF NOT EXISTS idx_user_project_ids ON user_project(user_id, project_id);
CREATE INDEX IF NOT EXISTS idx_project_comment_project_id ON project_comment(project_id);
CREATE INDEX IF NOT EXISTS idx_voter_user_competition ON voter(user_id, competition_id);
CREATE INDEX IF NOT EXISTS idx_notification_user_read ON notification(user_id, is_read);
CREATE INDEX IF NOT EXISTS idx_notification_user_created ON notification(user_id, creation_date DESC) WHERE is_read = false;
CREATE INDEX IF NOT EXISTS idx_invitation_status ON invitation(status);
CREATE INDEX IF NOT EXISTS idx_checklist_vote_user_project ON checklist_vote(user_id, project_id);
CREATE INDEX IF NOT EXISTS idx_checklist_vote_project_item ON checklist_vote(project_id, checklist_item_id);
CREATE INDEX IF NOT EXISTS idx_judge_user_competition ON is_judge(user_id, competition_id);