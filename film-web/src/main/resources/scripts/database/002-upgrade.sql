-- CREACIÓN USUARIO ADMIN Y ROLES --
INSERT INTO roles (id, name) VALUES (1, 'USER');
INSERT INTO roles (id, name) VALUES (2, 'ADMIN');

INSERT INTO users (id, username, password, email, name, surname, date_of_birth, created, last_login)
VALUES (
    1,
    'admin',
    '$2a$12$mDV2wxEaRJjQcay14ka5/.fO7DKukYxUeiP/hFNtsqn3LNyUU2FdG', -- pwd:admin123
    'admin@filmo.com',
    'Administrador',
    'Filmo',
    '1995-03-01',
    NOW(),
    NULL
);

INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO users_roles (user_id, role_id) VALUES (1, 2);