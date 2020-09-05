INSERT INTO USER_ROLE VALUES (1, 'Organizer');
INSERT INTO USER_ROLE VALUES (2, 'Coach');
INSERT INTO USER_ROLE VALUES (3, 'Guest');
COMMIT;

INSERT INTO MEDAL VALUES (1, 'Gold', 5);
INSERT INTO MEDAL VALUES (2, 'Silver', 3);
INSERT INTO MEDAL VALUES (3, 'Bronze', 1);
COMMIT;


INSERT INTO OLYMPICS VALUES 
(1, 'XXVIII', 'Athens', TO_DATE('08/13/2004','mm/dd/yyyy'), TO_DATE('08/29/2004', 'mm/dd/yyyy'), 'https://www.olympic.org/athens-2004');

INSERT INTO OLYMPICS VALUES 
(2, 'XXIX', 'Beijing', TO_DATE('08/08/2008','mm/dd/yyyy'), TO_DATE('08/24/2008', 'mm/dd/yyyy'), 'https://www.olympic.org/beijing-2008');

INSERT INTO OLYMPICS VALUES 
(3, 'XXX', 'London', TO_DATE('07/27/2012','mm/dd/yyyy'), TO_DATE('08/12/2012', 'mm/dd/yyyy'), 'https://www.olympic.org/london-2012');

INSERT INTO OLYMPICS VALUES 
(4, 'XXXI', 'Rio de Janeiro', TO_DATE('08/05/2016','mm/dd/yyyy'), TO_DATE('08/21/2016', 'mm/dd/yyyy'), 'https://www.olympic.org/rio-2016');
COMMIT;

INSERT INTO USER_ACCOUNT VALUES (1,'Carlos Arthur Nuzman','Rio', 1, TO_DATE('04/20/1980','mm/dd/yyyy'));
INSERT INTO USER_ACCOUNT VALUES (2,'Gianna Angelopoulos-Daskalaki','Athens', 1, TO_DATE('03/21/1971','mm/dd/yyyy'));
INSERT INTO USER_ACCOUNT VALUES (3,'Hu Jintao','Beijing', 1, TO_DATE('01/19/1991','mm/dd/yyyy'));
INSERT INTO USER_ACCOUNT VALUES (4,'Sebastian Newbold Coe','London', 1, TO_DATE('10/30/2000','mm/dd/yyyy'));
COMMIT;


INSERT INTO SPORT VALUES 
(1, 'Table Tennis', 'Points scored by bouncing ball on opponents side twice. Table game.', TO_DATE('09/17/1988', 'mm/dd/yyyy'), 2);
INSERT INTO SPORT VALUES 
(2, 'Tennis', 'Points scored by bouncing ball on opponents side twice', TO_DATE('09/17/1988', 'mm/dd/yyyy'), 2);
INSERT INTO SPORT VALUES 
(3, 'Archery', 'Points scored by hitting target 70m away. Higher points closer to center', TO_DATE('05/14/1900', 'mm/dd/yyyy'), 1);
INSERT INTO SPORT VALUES 
(4, 'Swimming', 'Various swimming races', TO_DATE('04/06/1896', 'mm/dd/yyyy'), 4);
INSERT INTO SPORT VALUES
(5, 'Wrestling', 'Includes freestyle and Greco-Roman', TO_DATE('04/06/1896', 'mm/dd/yyyy'), 1);
INSERT INTO SPORT VALUES
(6, 'Weightlifting', 'Includes various weightlifting such as deadlifts', TO_DATE('04/06/1896', 'mm/dd/yyyy'), 1);
INSERT INTO SPORT VALUES
(7, 'Boxing', 'Seperated by weight', TO_DATE('07/01/1904', 'mm/dd/yyyy'), 1);
COMMIT;
    
INSERT INTO COUNTRY VALUES
(1, 'Peoples Republic of China', 'CHN');
INSERT INTO COUNTRY VALUES
(2, 'United States of America', 'USA');
INSERT INTO COUNTRY VALUES
(3, 'Germany', 'DEU');
INSERT INTO COUNTRY VALUES
(4, 'Turkey', 'TUR');
INSERT INTO COUNTRY VALUES
(5, 'Republic of Korea', 'KOR');
INSERT INTO COUNTRY VALUES
(6, 'Russian Federation', 'RUS');
INSERT INTO COUNTRY VALUES
(7, 'Japan', 'JPN');
INSERT INTO COUNTRY VALUES
(8, 'Italy', 'ITA');
INSERT INTO COUNTRY VALUES
(9, 'United Kingdom', 'GBR');
COMMIT;

INSERT INTO PARTICIPANT VALUES
(1, 'Atagün', 'Yalçinkaya', 'Turkey', 'Altinda?, Ankara', TO_DATE('12/14/1986', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(2, 'Michael', 'Phelps', 'United States of America', 'Baltimore, Maryland', TO_DATE('06/30/1985', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(3, 'Enver', 'Yilmaz', 'Turkey', 'Istanbul', TO_DATE('11/30/1972', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(4, 'Hiroshi', 'Yamamoto', 'Japan', 'Yokohama, Japan', TO_DATE('10/31/1962', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(5, 'Marco', 'Galiazzo', 'Italy', 'Padova, Veneto', TO_DATE('05/07/1983', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(6, 'Bob', 'Robert', 'United Kingdom', 'Brighton, England', TO_DATE('02/04/1973', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(7, 'Stephen', 'Scully', 'United States of America', 'Providence, Rhode Island', TO_DATE('03/11/1969', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(8, 'Nurcan', 'Taylan', 'Turkey', 'Mamak, Ankara', TO_DATE('10/29/1983', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(9, 'Mehmet', 'Üstünda?', 'Turkey', 'Mamak, Ankara', TO_DATE('09/12/1973', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(10, 'Ryan', 'Lochte', 'United States of America', 'Rochester, New York', TO_DATE('08/03/1984', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(11, 'Peter', 'Vanderkaay', 'United States of America', 'Royal Oak, Michigan', TO_DATE('02/12/1984', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(12, 'Klete', 'Keller', 'United States of America', 'Las Vegas, Nevada', TO_DATE('03/21/1982', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(13, 'Bob', 'Bowman', 'United States of America', 'Columbia, South Carolina', TO_DATE('04/06/1965', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(14, 'Nan', 'Wang', 'Peoples Republic of China', 'Fushun, Liaoning', TO_DATE('10/23/1978', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(15, 'Yining', 'Zhang', 'Peoples Republic of China', 'Beijing, China', TO_DATE('10/05/1981', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(16, 'Huo', 'Ren', 'Peoples Republic of China', 'Wuhan, China', TO_DATE('06/18/1965', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(17, 'Nicolas', 'Kiefer', 'Germany', 'Holzminden, West Germany', TO_DATE('07/05/1977', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(18, 'Rainer', 'Schüttler', 'Germany', 'Korbach, West Germany', TO_DATE('04/25/1976', 'mm/dd/yyyy'));
INSERT INTO PARTICIPANT VALUES
(19, 'Torben', 'Beltz', 'Germany', 'Berlin, West Germany', TO_DATE('11/27/1976', 'mm/dd/yyyy'));
COMMIT;

INSERT INTO TEAM VALUES
(1, 1, 'Mens Italian Archery', 8, 3, 6);
INSERT INTO TEAM VALUES
(2, 1, 'Mens Turkish Boxing', 4, 7, 3);
INSERT INTO TEAM VALUES
(3, 1, 'Mens Japanese Archery', 7, 3, 7);
INSERT INTO TEAM VALUES
(4, 1, 'Womens Turkish Weightlifting', 4, 6, 9);
INSERT INTO TEAM VALUES
(5, 1, 'Mens American Swimming', 2, 4, 13);
INSERT INTO TEAM VALUES
(6, 1, 'Womens Chinese Table Tenis', 1, 1, 16);
INSERT INTO TEAM VALUES
(7, 1, 'Mens German Tennis', 3, 2, 19);
COMMIT;

INSERT INTO TEAM VALUES
(team_seq.NEXTVAL, 2, 'Mens American Swimming', 2, 4, 13);
INSERT INTO TEAM_MEMBER VALUES
(team_seq.CURRVAL, 2); --mens american swimming
INSERT INTO TEAM_MEMBER VALUES
(team_seq.CURRVAL, 10);
INSERT INTO TEAM_MEMBER VALUES
(team_seq.CURRVAL, 11);
INSERT INTO TEAM_MEMBER VALUES
(team_seq.CURRVAL, 12);
COMMIT;

INSERT INTO TEAM VALUES
(team_seq.NEXTVAL, 2, 'Womens Chinese Table Tenis', 1, 1, 16);
INSERT INTO TEAM_MEMBER VALUES
(team_seq.CURRVAL, 14); --womens chinese table tennis
INSERT INTO TEAM_MEMBER VALUES
(team_seq.CURRVAL, 15);
COMMIT;

INSERT INTO TEAM VALUES
(team_seq.NEXTVAL, 3, 'Mens German Tennis', 3, 2, 19);
INSERT INTO TEAM_MEMBER VALUES
(team_seq.CURRVAL, 17); --mens german tennis
INSERT INTO TEAM_MEMBER VALUES
(team_seq.CURRVAL, 18);
COMMIT;

INSERT INTO TEAM VALUES
(team_seq.NEXTVAL, 3, 'Womens Chinese Table Tenis', 1, 1, 16);
INSERT INTO TEAM_MEMBER VALUES
(team_seq.CURRVAL, 14); --womens chinese table tennis
INSERT INTO TEAM_MEMBER VALUES
(team_seq.CURRVAL, 15);
COMMIT;


INSERT INTO TEAM_MEMBER VALUES
(1, 5); --mens italian archery
INSERT INTO TEAM_MEMBER VALUES
(2, 1); --mens turkish boxing
INSERT INTO TEAM_MEMBER VALUES
(3, 4); --mens japanense archery
INSERT INTO TEAM_MEMBER VALUES
(4, 8); --womens turkish weightlifiting
INSERT INTO TEAM_MEMBER VALUES
(5, 2); --mens american swimming
INSERT INTO TEAM_MEMBER VALUES
(5, 10);
INSERT INTO TEAM_MEMBER VALUES
(5, 11);
INSERT INTO TEAM_MEMBER VALUES
(5, 12);
INSERT INTO TEAM_MEMBER VALUES
(6, 14); --womens chinese table tennis
INSERT INTO TEAM_MEMBER VALUES
(6, 15);
INSERT INTO TEAM_MEMBER VALUES
(7, 17); --mens german tennis
INSERT INTO TEAM_MEMBER VALUES
(7, 18);
COMMIT;

INSERT INTO VENUE VALUES
(1, 1, 'Panathinaiko Stadium', 1);
INSERT INTO VENUE VALUES
(2, 1, 'Athens Olympic Aquatic Centre', 3);
INSERT INTO VENUE VALUES
(3, 1, 'Galatsi Olympic Hall', 4);
INSERT INTO VENUE VALUES
(4, 1, 'Nikaia Olympic Weightlifting Hall', 4);
INSERT INTO VENUE VALUES
(5, 1, 'Athens Olympic Tennis Centre', 16);
INSERT INTO VENUE VALUES
(6, 1, 'Ano Liosia Olympic Hall', 3);
INSERT INTO VENUE VALUES
(7, 1, 'Peristeri Olympic Boxing Hall', 1);
COMMIT;

INSERT INTO EVENT VALUES
(1, 1, 3, 'w', TO_DATE('08/14/2004 12:00:00', 'mm/dd/yyyy hh24:mi:ss')); --table tennis
INSERT INTO EVENT VALUES
(2, 3, 1, 'm', TO_DATE('08/15/2004 12:00:00', 'mm/dd/yyyy hh24:mi:ss')); --archery
INSERT INTO EVENT VALUES
(3, 7, 7, 'm', TO_DATE('08/14/2004 12:00:00', 'mm/dd/yyyy hh24:mi:ss')); --boxing
INSERT INTO EVENT VALUES
(4, 6, 4, 'w', TO_DATE('08/20/2004 12:00:00', 'mm/dd/yyyy hh24:mi:ss')); --weightlifting
INSERT INTO EVENT VALUES
(5, 4, 2, 'm', TO_DATE('08/19/2004 12:00:00', 'mm/dd/yyyy hh24:mi:ss')); --swimming
INSERT INTO EVENT VALUES
(6, 2, 5, 'm', TO_DATE('08/27/2004 12:00:00', 'mm/dd/yyyy hh24:mi:ss')); --tennis
COMMIT;


INSERT INTO EVENT_PARTICIPATION VALUES
(1, 6, 'e'); --chinese table tennis
INSERT INTO EVENT_PARTICIPATION VALUES
(2, 3, 'e'); --japanese archery team
INSERT INTO EVENT_PARTICIPATION VALUES
(2, 1, 'e'); --italian archery team
INSERT INTO EVENT_PARTICIPATION VALUES
(3, 2, 'e'); --turkish boxing team
INSERT INTO EVENT_PARTICIPATION VALUES
(4, 4, 'e'); --turkish weightlifting team
INSERT INTO EVENT_PARTICIPATION VALUES
(5, 5, 'e'); --american swimming team
INSERT INTO EVENT_PARTICIPATION VALUES
(6, 7, 'e'); --german tennis team
COMMIT;

INSERT INTO SCOREBOARD VALUES
(1, 1, 6, 14, 1, null);
INSERT INTO SCOREBOARD VALUES
(1, 1, 6, 15, 1, null);
INSERT INTO SCOREBOARD VALUES
(1, 2, 1, 5, 1, null);
INSERT INTO SCOREBOARD VALUES
(1, 2, 3, 4, 2, null);
INSERT INTO SCOREBOARD VALUES
(1, 3, 2, 1, 2, null);
INSERT INTO SCOREBOARD VALUES
(1, 4, 4, 8, 1, null);

COMMIT;
INSERT INTO USER_ACCOUNT VALUES (5,'Bob Robert','BR', 2, TO_DATE('02/04/1973','mm/dd/yyyy'));
INSERT INTO USER_ACCOUNT VALUES (6,'Enver Yilmaz','EY', 2, TO_DATE('11/30/1972','mm/dd/yyyy'));
INSERT INTO USER_ACCOUNT VALUES (7,'Stephen Scully','SY', 2, TO_DATE('03/11/1969','mm/dd/yyyy'));
INSERT INTO USER_ACCOUNT VALUES (8,'Mehmet Üstünda','MU', 2, TO_DATE('09/12/1973','mm/dd/yyyy'));
INSERT INTO USER_ACCOUNT VALUES (9,'Bob Bowman','BB', 2, TO_DATE('04/06/1965','mm/dd/yyyy'));
INSERT INTO USER_ACCOUNT VALUES (10,'Huo Ren','HR', 2, TO_DATE('06/18/1965','mm/dd/yyyy'));
INSERT INTO USER_ACCOUNT VALUES (11,'Torben Beltz','TB', 2, TO_DATE('11/27/1976','mm/dd/yyyy'));
INSERT INTO USER_ACCOUNT VALUES (user_seq.NEXTVAL, 'Guest', 'GUEST', 3, TO_DATE('4/14/2020', 'mm/dd/yyyy'));

COMMIT;