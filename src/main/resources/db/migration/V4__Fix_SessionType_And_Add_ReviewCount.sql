-- Migration: Fix session_type column type and add review_question_count
-- Date: 2026-05-05

-- 1. Alter session_type to varchar(20) if it was smallint
-- We use a DO block to handle the conversion safely
DO $$ 
BEGIN 
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'gamesession' 
        AND column_name = 'session_type' 
        AND data_type = 'smallint'
    ) THEN 
        -- 1. Alter type to varchar
        ALTER TABLE gamesession ALTER COLUMN session_type TYPE varchar(20) USING 
            CASE 
                WHEN session_type = 0 THEN 'NORMAL'
                WHEN session_type = 1 THEN 'REVIEW'
                ELSE 'NORMAL'
            END;
    END IF;
END $$;

-- 2. Add review_question_count column
ALTER TABLE gamesession 
  ADD COLUMN IF NOT EXISTS review_question_count integer;

