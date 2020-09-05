--QHV1 Quinn Vierick
DROP TABLE USER_ACCOUNT CASCADE CONSTRAINTS;
DROP TABLE USER_ROLE CASCADE CONSTRAINTS;
DROP TABLE OLYMPICS CASCADE CONSTRAINTS;
DROP TABLE COUNTRY CASCADE CONSTRAINTS;
DROP TABLE TEAM CASCADE CONSTRAINTS;
DROP TABLE SPORT CASCADE CONSTRAINTS;
DROP TABLE PARTICIPANT CASCADE CONSTRAINTS;
DROP TABLE TEAM CASCADE CONSTRAINTS;
DROP TABLE TEAM_MEMBER CASCADE CONSTRAINTS;
DROP TABLE MEDAL CASCADE CONSTRAINTS;
DROP TABLE VENUE CASCADE CONSTRAINTS;
DROP TABLE EVENT CASCADE CONSTRAINTS;
DROP TABLE EVENT_PARTICIPATION CASCADE CONSTRAINTS;
DROP TABLE SCOREBOARD CASCADE CONSTRAINTS;

DROP SEQUENCE country_seq;
DROP SEQUENCE user_seq;
DROP SEQUENCE participant_seq;
DROP SEQUENCE olympic_seq;
DROP SEQUENCE event_seq;
DROP SEQUENCE sport_seq;
DROP SEQUENCE venue_seq;
DROP SEQUENCE team_seq;

CREATE TABLE COUNTRY
(
    country_id integer,
    country varchar2(40),
    country_code varchar2(3),
    
    CONSTRAINT COUNTRY_PK PRIMARY KEY (country_id),
    CONSTRAINT COUNTRY_UNIQUE1 UNIQUE (country),
    CONSTRAINT COUNTRY_UNIQUE2 UNIQUE (COUNTRY_CODE)
);
CREATE TABLE USER_ROLE
(
    role_id integer NOT NULL,
    role_name varchar2(20) CHECK ( role_name in ('Organizer', 'Coach', 'Guest')) NOT NULL,
    
    --My assumption is that the role_id will be unique to
    --one role_name
    CONSTRAINT USER_ROLE_PK PRIMARY KEY (role_id),
    CONSTRAINT USER_ROLE_UNIQUE UNIQUE(role_name)
);
CREATE TABLE USER_ACCOUNT
(
    user_id integer NOT NULL,
    username varchar2(40) NOT NULL,
    passkey varchar2(20) NOT NULL,
    role_id integer NOT NULL,
    last_login date NOT NULL,
    
    --My assumption is that all user_id's will be unique, and thus
    --can be used as a primary key, as well as the username being unique
    CONSTRAINT USER_ACCOUNT_PK PRIMARY KEY (user_id),
    CONSTRAINT USER_ACCOUNT_FK FOREIGN KEY (role_id) REFERENCES USER_ROLE (role_id),
    CONSTRAINT USER_ACCOUNT_UNIQUE UNIQUE (username)
);
CREATE TABLE OLYMPICS
(
    olympic_id integer,
    olympic_num varchar2(30) NOT NULL,
    host_city varchar2(30) NOT NULL,
    opening_date date NOT NULL,
    closing_date date NOT NULL,
    official_website varchar2(50),
    
    --My assumption is that the olympic_id will refer
    --to only one olympic game. I am also assuming
    --that olympic_num cannot be used, seeing as
    --winter olympics and summer olympics could have
    --the same number
    CONSTRAINT OLYMPICS_PK PRIMARY KEY (olympic_id),
    CONSTRAINT OLYMPIC_UNIQUE UNIQUE (olympic_num)
);
CREATE TABLE SPORT
(
    sport_id integer,
    sport_name varchar2(30) NOT NULL,
    description varchar2(80) NOT NULL,
    dob date,
    team_size integer CHECK (team_size > 0) NOT NULL,
    
    CONSTRAINT SPORT_PK PRIMARY KEY (sport_id)
);
CREATE TABLE PARTICIPANT
(
    participant_id integer,
    fname varchar2(30) NOT NULL,
    lname varchar2(30) NOT NULL,
    nationality varchar2(40) NOT NULL,
    birth_place varchar2(40) NOT NULL,
    dob date NOT NULL,
    
    CONSTRAINT PARTICIPANT_PK PRIMARY KEY (participant_id),
    CONSTRAINT PARTICIPANT_FK FOREIGN KEY (nationality) REFERENCES COUNTRY (country),
    CONSTRAINT PARTICIPANT_UNIQUE UNIQUE (fname, lname)
);
CREATE TABLE TEAM
(
    team_id integer,
    olympic_id integer NOT NULL,
    team_name varchar2(50) NOT NULl,
    country_id integer NOT NULL,
    sport_id integer NOT NULL,
    coach_id integer NOT NULL,
    
    CONSTRAINT TEAM_PK PRIMARY KEY (team_id, olympic_id),
    CONSTRAINT TEAM_FK1 FOREIGN KEY (country_id) REFERENCES COUNTRY (country_id),
    CONSTRAINT TEAM_FK2 FOREIGN KEY (olympic_id) REFERENCES OLYMPICS (olympic_id),
    CONSTRAINT TEAM_FK3 FOREIGN KEY (coach_id) REFERENCES PARTICIPANT (participant_id),
    CONSTRAINT TEAM_FK4 FOREIGN KEY (sport_id) REFERENCES SPORT (sport_id),
    CONSTRAINT TEAM_UNIQUE UNIQUE (team_id)
);
CREATE TABLE TEAM_MEMBER
(
    team_id integer,
    participant_id integer,
    
    CONSTRAINT TEAM_MEMBER_PK PRIMARY KEY (team_id, participant_id),
    CONSTRAINT TEAM_MEMBER_FK1 FOREIGN KEY (team_id) REFERENCES TEAM (team_id),
    CONSTRAINT TEAM_MEMBER_FK2 FOREIGN KEY (participant_id) REFERENCES PARTICIPANT (participant_id)
);
CREATE TABLE MEDAL
(
    medal_id integer,
    medal_title varchar2(6) CHECK (medal_title IN ('Gold', 'Silver', 'Bronze')),
    points integer NOT NULL,
    
    CONSTRAINT MEDAL_PK PRIMARY KEY (medal_id)
);
CREATE TABLE VENUE
(
    venue_id integer,
    olympic_id integer NOT NULL,
    venue_name varchar2(40) NOT NULL,
    capacity integer CHECK (capacity > 0) NOT NULL,
    
    CONSTRAINT VENUE_PK PRIMARY KEY (venue_id),
    CONSTRAINT VENUE_FK FOREIGN KEY (olympic_id) REFERENCES OLYMPICS (olympic_id)
);
CREATE TABLE EVENT
(
    event_id integer NOT NULL,
    sport_id integer NOT NULL,
    venue_id integer NOT NULL,
    gender char CHECK (gender IN ('m', 'w')),
    event_time date NOT NULL,
    
    CONSTRAINT EVENT_PK PRIMARY KEY (event_id),
    CONSTRAINT EVENT_FK1 FOREIGN KEY (sport_id) REFERENCES SPORT (sport_id),
    CONSTRAINT EVENT_FK2 FOREIGN KEY (venue_id) REFERENCES VENUE (venue_id)
);
CREATE TABLE SCOREBOARD
(
    olympic_id integer NOT NULL,
    event_id integer NOT NULL,
    team_id integer NOT NULL,
    participant_id integer NOT NULL,
    position integer,
    medal_id integer,
    
    CONSTRAINT SCOREBOARD_PK PRIMARY KEY (olympic_id, event_id, team_id, participant_id),
    CONSTRAINT SCOREBOARD_FK1 FOREIGN KEY (event_id) REFERENCES EVENT (event_id),
    CONSTRAINT SCOREBOARD_FK2 FOREIGN KEY (olympic_id) REFERENCES OLYMPICS (olympic_id),
    CONSTRAINT SCOREBOARD_FK3 FOREIGN KEY (participant_id) REFERENCES PARTICIPANT (participant_id),
    CONSTRAINT SCOREBOARD_FK4 FOREIGN KEY (medal_id) REFERENCES MEDAL (medal_id),
    CONSTRAINT SCOREBOARD_FK5 FOREIGN KEY (team_id) REFERENCES TEAM (team_id)
);
CREATE TABLE EVENT_PARTICIPATION
( 
    event_id integer,
    team_id integer,
    status char CHECK(status IN ('e', 'n')) NOT NULL,
    
    CONSTRAINT EVENT_PARTICIPATION_PK PRIMARY KEY (event_id, team_id),
    CONSTRAINT EVENT_PARTICIPATION_FK1 FOREIGN KEY (event_id) REFERENCES EVENT (event_id),
    CONSTRAINT EVENT_PARTICIPATION_FK2 FOREIGN KEY (team_id) REFERENCES TEAM (team_id)
);
COMMIT;

CREATE SEQUENCE country_seq
        MINVALUE 1
        START WITH 10
        INCREMENT BY 1
        CACHE 20;
CREATE SEQUENCE user_seq
        MINVALUE 1
        START WITH 12
        INCREMENT BY 1
        CACHE 20;
CREATE SEQUENCE participant_seq
        MINVALUE 1
        START WITH 20
        INCREMENT BY 1
        CACHE 20;
CREATE SEQUENCE olympic_seq
        MINVALUE 1
        START WITH 5
        INCREMENT BY 1
        CACHE 20;
CREATE SEQUENCE event_seq
        MINVALUE 1
        START WITH 7
        INCREMENT BY 1
        CACHE 20;
CREATE SEQUENCE sport_seq
        MINVALUE 1
        START WITH 8
        INCREMENT BY 1
        CACHE 20;
CREATE SEQUENCE venue_seq
        MINVALUE 1
        START WITH 8
        INCREMENT BY 1
        CACHE 20;
CREATE SEQUENCE team_seq
        MINVALUE 1
        START WITH 8
        INCREMENT BY 1
        CACHE 20;
COMMIT;
    
