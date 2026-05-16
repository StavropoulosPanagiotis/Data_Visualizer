USE `data_visualizer`;

-- ----------------------------------------------------------
-- This function is used to handle abbreviations like J. or Int. on journals
-- ----------------------------------------------------------

SET GLOBAL log_bin_trust_function_creators = 1;

DROP FUNCTION IF EXISTS normalize_journal;

DELIMITER $

CREATE FUNCTION normalize_journal(journal_name TEXT)
RETURNS TEXT
DETERMINISTIC
BEGIN
    SET journal_name = REPLACE(journal_name, 'J.', 'Journal of');
    SET journal_name = REPLACE(journal_name, 'Trans.', 'Transactions on');
    SET journal_name = REPLACE(journal_name, 'Int.', 'International');
    SET journal_name = REPLACE(journal_name, 'Inf.', 'Information');
    SET journal_name = REPLACE(journal_name, 'Comput.', 'Computing');
    SET journal_name = REPLACE(journal_name, 'Commun.', 'Communications');
    SET journal_name = REPLACE(journal_name, 'Syst.', 'Systems');
    SET journal_name = REPLACE(journal_name, 'Theor.', 'Theoretical');
    SET journal_name = REPLACE(journal_name, 'Appl.', 'Applied');
    SET journal_name = REPLACE(journal_name, 'Electr.', 'Electronic');
    SET journal_name = REPLACE(journal_name, 'Math.', 'Mathematical');
    SET journal_name = REPLACE(journal_name, 'Comp.', 'Computer');
    SET journal_name = REPLACE(journal_name, 'Knowl.', 'Knowledge');
    SET journal_name = REPLACE(journal_name, 'Eng.', 'Engineering');
    SET journal_name = REPLACE(journal_name, 'Artif.', 'Artificial');
    SET journal_name = REPLACE(journal_name, 'Softw.', 'Software');
    SET journal_name = REPLACE(journal_name, 'Ann.', 'Annals');
    SET journal_name = REPLACE(journal_name, 'Eur.', 'European');
    SET journal_name = REPLACE(journal_name, 'Fundam.', 'Fundamenta');
    SET journal_name = REPLACE(journal_name, 'Comb.', 'Combinatorics');
    SET journal_name = REPLACE(journal_name, 'Rel.', 'Relations');
    SET journal_name = REPLACE(journal_name, 'Process.', 'Processing');
    SET journal_name = REPLACE(journal_name, 'Med.', 'Medical');
    SET journal_name = REPLACE(journal_name, 'Adv.', 'Advanced');
    SET journal_name = REPLACE(journal_name, 'Cyberpsy.', 'Cyberpsychology');
    SET journal_name = REPLACE(journal_name, 'Des.', 'Design');
    SET journal_name = REPLACE(journal_name, 'Perform.', 'Performance');
    SET journal_name = REPLACE(journal_name, 'Sci.', 'Science');
    SET journal_name = REPLACE(journal_name, 'Oper.', 'Operations');
    SET journal_name = REPLACE(journal_name, 'Sig.', 'Signal');
    SET journal_name = REPLACE(journal_name, 'Geosci.', 'Geosciences');
    SET journal_name = REPLACE(journal_name, 'Struct.', 'Structures');
    SET journal_name = REPLACE(journal_name, 'Mach.', 'Machine');
    RETURN journal_name;
END$
DELIMITER ;

SET GLOBAL log_bin_trust_function_creators = 0;