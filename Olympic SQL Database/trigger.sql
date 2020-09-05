--Qhv1 Quinn Vierick
ALTER SESSION SET PLSCOPE_SETTINGS = 'IDENTIFIERS:NONE';
SET SERVEROUTPUT ON;

CREATE OR REPLACE TRIGGER ASSIGN_MEDAL
BEFORE UPDATE OR INSERT ON SCOREBOARD
FOR EACH ROW
BEGIN
    IF :new.position = 1 THEN
        :new.medal_id := 1;
    ELSIF :new.position = 2 THEN
        :new.medal_id := 2;
    ELSIF :new.position = 3 THEN
        :new.medal_id := 3;
    ELSE
        :new.medal_id := null;
    END IF;
END;
/

CREATE OR REPLACE VIEW NUM_ON_TEAM
AS
SELECT count(participant_id) AS NUM, team_id
FROM TEAM_MEMBER
GROUP BY team_id;


CREATE OR REPLACE TRIGGER ATHLETE_DISMISSAL
BEFORE DELETE ON TEAM_MEMBER
FOR EACH ROW
DECLARE 
team integer;
sportid integer;
num_players integer;
BEGIN
team := :old.team_id;
select sport_id into sportid from team where team_id = team;
select team_size into num_players from sport where sportid = sport_id;
IF num_players = 1 THEN
    DELETE FROM EVENT_PARTICIPATION WHERE team_id = team;
    DELETE FROM SCOREBOARD WHERE team_id = team;
    DELETE FROM TEAM WHERE team_id = team;
ELSIF num_players > 1 THEN
    UPDATE EVENT_PARTICIPATION SET status = 'n' WHERE team_id = team;
END IF;

END;
/

CREATE OR REPLACE VIEW NUM_IN_VENUE
AS
SELECT count(event_id) AS NUM, venue_id, event_time
FROM EVENT
GROUP BY venue_id, event_time;

CREATE OR REPLACE TRIGGER ENFORCE_CAPACITY
BEFORE INSERT ON EVENT
FOR EACH ROW
DECLARE
cap integer;
current integer;
time date := :new.event_time;
id integer := :new.venue_id;
max_capacity EXCEPTION;
BEGIN
SELECT capacity INTO cap FROM VENUE WHERE venue_id = id;
SELECT NUM INTO current FROM NUM_IN_VENUE WHERE venue_id = id AND time = event_time;
IF current IS NOT NULL AND current >= cap THEN
    raise_application_error(-20101, 'Max capacity for event at that time has been reached');
END IF; 
EXCEPTION
    WHEN no_data_found THEN
        null;
END;
/

commit;