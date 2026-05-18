-- The administrator
INSERT INTO my_user(id, email, password_hash)
VALUES ('294af5a3-57aa-4d9d-82b4-7b1c4a89725d',
        'orguser@example.com',
        '{noop}password1');

INSERT INTO organization(id, slug, name)
VALUES ('4af088a7-c9e4-4310-b115-8a378c140519',
        'demo-org',
        'Demo Org');

INSERT INTO organization_user(organization_id, user_id, created_on)
VALUES ('4af088a7-c9e4-4310-b115-8a378c140519',
        '294af5a3-57aa-4d9d-82b4-7b1c4a89725d',
        NOW());

INSERT INTO organization_user_permissions(organization_user_organization_id, organization_user_user_id, permissions)
VALUES ('4af088a7-c9e4-4310-b115-8a378c140519',
        '294af5a3-57aa-4d9d-82b4-7b1c4a89725d',
        'ADD_USER');


-- The user to add to the org in the test
INSERT INTO my_user(id, email, password_hash)
VALUES ('8c31514e-5970-473f-96fa-d8feeb327c3e',
        'client@example.com',
        '{noop}password2');
