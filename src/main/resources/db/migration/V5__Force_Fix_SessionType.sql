-- Migration: Force fix session_type column type to varchar(20)
-- Date: 2026-05-05

-- This migration forces the conversion of session_type to varchar(20) 
-- regardless of its current state, mapping ordinals 0 and 1 if necessary.
ALTER TABLE gamesession 
  ALTER COLUMN session_type TYPE varchar(20) 
  USING (
    CASE 
      WHEN session_type::text = '0' THEN 'NORMAL'
      WHEN session_type::text = '1' THEN 'REVIEW'
      ELSE session_type::text
    END
  );

-- Ensure started_at and finished_at are timestamps (not varchar)
ALTER TABLE gamesession 
  ALTER COLUMN started_at TYPE timestamp USING started_at::timestamp,
  ALTER COLUMN finished_at TYPE timestamp USING finished_at::timestamp;

-- Ensure review_question_count exists
ALTER TABLE gamesession 
  ADD COLUMN IF NOT EXISTS review_question_count integer;
