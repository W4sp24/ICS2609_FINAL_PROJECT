-- Database 1: Apache Derby (Authentication)
CREATE TABLE USERS (
    username VARCHAR(50) PRIMARY KEY 
        CONSTRAINT user_format_check CHECK (username LIKE '%@%.%'),
    
    password VARCHAR(255) NOT NULL,
    
    role VARCHAR(10) NOT NULL 
        CONSTRAINT role_check CHECK (role IN ('Admin', 'Guest')),
    
    createdDate DATE NOT NULL
);

INSERT INTO USERS (username, password, role, createdDate) VALUES
('ace_admin@corp.com', '12345678', 'Admin', '2024-01-12'),
('m.rossi@tech.net', '12345678', 'Admin', '2024-02-15'),
('admin_chief@web.org', '12345678', 'Admin', '2024-03-01'),
('k.smith@data.com', '12345678', 'Admin', '2024-03-22'),
('root_access@serv.io', '12345678', 'Admin', '2024-04-10'),
('j.doe@enterprise.com', '12345678', 'Admin', '2024-05-05'),
('lead_sys@cloud.co', '12345678', 'Admin', '2024-06-18'),
('h.vance@security.org', '12345678', 'Admin', '2024-07-20'),
('ops_manager@firm.net', '12345678', 'Admin', '2024-08-11'),
('b.wayne@gotham.com', '12345678', 'Admin', '2024-09-02'),
('super_u@portal.biz', '12345678', 'Admin', '2024-10-14'),
('tech_lead@nexus.io', '12345678', 'Admin', '2024-11-30'),
('a.vader@empire.com', '12345678', 'Admin', '2024-12-05'),
('e.ripley@nostromo.org', '12345678', 'Admin', '2025-01-19'),
('n.neo@matrix.net', '12345678', 'Admin', '2025-02-28');

INSERT INTO USERS (username, password, role, createdDate) VALUES
('user01@mail.com', '12345678', 'Guest', '2024-01-05'),
('tester_a@demo.org', '12345678', 'Guest', '2024-01-18'),
('visit.22@site.net', '12345678', 'Guest', '2024-02-02'),
('hello_world@js.io', '12345678', 'Guest', '2024-02-24'),
('random_guy@web.me', '12345678', 'Guest', '2024-03-10'),
('c.kent@daily.com', '12345678', 'Guest', '2024-03-15'),
('d.prince@themyscira.org', '12345678', 'Guest', '2024-04-01'),
('guest_user_99@hub.biz', '12345678', 'Guest', '2024-04-20'),
('app_test@dev.co', '12345678', 'Guest', '2024-05-12'),
('s.rogers@shield.gov', '12345678', 'Guest', '2024-05-29'),
('t.stark@stark.com', '12345678', 'Guest', '2024-06-04'),
('n.romanoff@widow.ru', '12345678', 'Guest', '2024-06-15'),
('p.parker@bugle.com', '12345678', 'Guest', '2024-07-01'),
('b.banner@gamma.edu', '12345678', 'Guest', '2024-07-14'),
('thor.o@asgard.gov', '12345678', 'Guest', '2024-08-05'),
('loki.l@mischief.io', '12345678', 'Guest', '2024-08-19'),
('user.alpha@test.com', '12345678', 'Guest', '2024-09-10'),
('user.beta@test.com', '12345678', 'Guest', '2024-09-12'),
('user.gamma@test.com', '12345678', 'Guest', '2024-09-15'),
('customer_1@shop.biz', '12345678', 'Guest', '2024-10-05'),
('buyer_pro@market.net', '12345678', 'Guest', '2024-10-22'),
('client_x@services.org', '12345678', 'Guest', '2024-11-02'),
('anon_user@privacy.io', '12345678', 'Guest', '2024-11-18'),
('g.grey@xmen.edu', '12345678', 'Guest', '2024-12-01'),
('s.summers@xmen.edu', '12345678', 'Guest', '2024-12-10'),
('l.logan@weaponx.ca', '12345678', 'Guest', '2024-12-25'),
('o.monroe@weather.org', '12345678', 'Guest', '2025-01-05'),
('r.darkholme@shape.io', '12345678', 'Guest', '2025-01-14'),
('k.pryde@phase.net', '12345678', 'Guest', '2025-01-28'),
('r.lebeau@cards.fr', '12345678', 'Guest', '2025-02-04'),
('a.marie@rogue.com', '12345678', 'Guest', '2025-02-12'),
('user_48@temp.me', '12345678', 'Guest', '2025-02-20'),
('user_49@temp.me', '12345678', 'Guest', '2025-02-25'),
('user_50@temp.me', '12345678', 'Guest', '2025-03-01'),
('last.guest@final.com', '12345678', 'Guest', '2025-03-05');