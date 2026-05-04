-- Migration: Add columns for cycle logic implementation
-- Date: 2026-05-04

-- Add testCycleIndex and sessionType to gamesession table
ALTER TABLE gamesession 
  ADD COLUMN IF NOT EXISTS test_cycle_index integer DEFAULT 1 NOT NULL,
  ADD COLUMN IF NOT EXISTS session_type varchar(20) DEFAULT 'NORMAL' NOT NULL;

-- Add correct and answered_at to gamesession_question table for tracking answer results
ALTER TABLE gamesession_question
  ADD COLUMN IF NOT EXISTS correct boolean,
  ADD COLUMN IF NOT EXISTS answered_at timestamp;

-- Create index on gamesession for faster queries by playerId and subject
CREATE INDEX IF NOT EXISTS idx_gamesession_player_subject 
  ON gamesession(player_id, subject);

-- Create index on gamesession_question for faster queries on correct answers and timestamps
CREATE INDEX IF NOT EXISTS idx_gamesession_question_correct_timestamp 
  ON gamesession_question(session_id, correct, answered_at);
