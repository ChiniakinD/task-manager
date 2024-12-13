create schema if not exists auth;

create table if not exists auth.users
(
    id       uuid primary key,
    login    varchar(255) unique not null,
    password varchar(255)        not null,
    email    varchar(255) unique not null
);

create table if not exists auth.roles
(
    id          uuid primary key,
    role        varchar(100) unique not null,
    description text
);

create table if not exists auth.user_roles
(
    id      uuid primary key default gen_random_uuid(),
    user_id uuid not null,
    role_id uuid not null,
    constraint fk_user_roles_user foreign key (user_id) references auth.users (id),
    constraint fk_user_roles_role foreign key (role_id) references auth.roles (id),
    constraint user_role unique (user_id, role_id)
);

insert into auth.roles(id, role, description)
values (gen_random_uuid(), 'ADMIN', 'Роль со всеми правами'),
       (gen_random_uuid(), 'USER', 'Обычный пользователь');

insert into auth.users (id, login, password, email) -- пароль admin
values (gen_random_uuid(), 'admin', '$2a$10$Ju0Gg8nKhw2sunfj3yoI3e6rgwkc1UMreFn0V1sEefQWfuwuBboN2', 'admin@test.com');

insert into auth.user_roles (id, user_id, role_id)
values (gen_random_uuid(),
        (select id from auth.users where login = 'admin'),
        (select id from auth.roles where role = 'ADMIN'))
