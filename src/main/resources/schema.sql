drop table if exists vehicles;

create table vehicles (
  `id` bigint primary key auto_increment,
  `creation_time` timestamp not null,
  `model` varchar(70),
  `name` varchar(70),
  `updated` timestamp not null
);