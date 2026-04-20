INSERT INTO users (username, is_premium)
VALUES ('human_user_1', false) ON CONFLICT DO NOTHING;

INSERT INTO users (username, is_premium)
VALUES ('human_user_2', true) ON CONFLICT DO NOTHING;

INSERT INTO bots (name, persona_description)
VALUES ('TechBot-Alpha', 'A helpful technology assistant bot')
ON CONFLICT DO NOTHING;

INSERT INTO bots (name, persona_description)
VALUES ('DataBot-Beta', 'A data analysis assistant bot')
ON CONFLICT DO NOTHING;
