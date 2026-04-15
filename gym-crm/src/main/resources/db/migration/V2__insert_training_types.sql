INSERT INTO training_type (id, training_type_name)
SELECT UUID_TO_BIN(UUID()), t.name
FROM (
         SELECT 'YOGA' AS name
         UNION ALL SELECT 'CROSSFIT'
         UNION ALL SELECT 'MMA'
         UNION ALL SELECT 'WRESTLING'
     ) t
WHERE NOT EXISTS (
    SELECT 1 FROM training_type
);