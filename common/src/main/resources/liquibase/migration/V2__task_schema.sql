create schema if not exists task;

create table if not exists task.tasks
(

    id          uuid primary key,
    title       varchar(255) not null,
    description text,
    is_active boolean default true,
    status      varchar(20)  not null,
    priority    varchar(20),
    creator_id  uuid,
    assignee_id uuid,
    created_at timestamp without time zone default now(),
    updated_at timestamp without time zone,
    constraint fk_creator_id_user foreign key (creator_id) references auth.users (id),
    constraint fk_assignee_id_user foreign key (assignee_id) references auth.users (id)
);

create table if not exists task.comments
(
    id        uuid primary key,
    user_id   uuid,
    task_id uuid,
    text      text not null,
    is_active boolean default true,
    created_at timestamp default current_timestamp,
    constraint fk_user_id foreign key (user_id) references auth.users (id),
    constraint fk_task_id foreign key (task_id) references task.tasks (id)
);

