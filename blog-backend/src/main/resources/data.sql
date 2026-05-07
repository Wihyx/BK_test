INSERT INTO `user` (username, password, nickname, role)
SELECT 'admin', 'admin123', 'admin', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE username = 'admin');

INSERT INTO `category` (name, description, sort)
SELECT 'default', 'default', 1
WHERE NOT EXISTS (SELECT 1 FROM `category`);

INSERT INTO `quote` (content)
SELECT '代码是写给人看的，顺便能在机器上运行。'
WHERE NOT EXISTS (SELECT 1 FROM `quote`);

INSERT INTO `quote` (content)
SELECT 'Stay hungry, stay foolish.'
WHERE NOT EXISTS (SELECT 1 FROM `quote` WHERE content = 'Stay hungry, stay foolish.');

INSERT INTO `quote` (content)
SELECT '大道至简，知易行难。'
WHERE NOT EXISTS (SELECT 1 FROM `quote` WHERE content = '大道至简，知易行难.');

INSERT INTO `quote` (content)
SELECT '千里之行，始于足下。'
WHERE NOT EXISTS (SELECT 1 FROM `quote` WHERE content = '千里之行，始于足下.');

INSERT INTO `quote` (content)
SELECT '简单是可靠的先决条件。'
WHERE NOT EXISTS (SELECT 1 FROM `quote` WHERE content = '简单是可靠的先决条件.');

INSERT INTO `profile` (id, name, bio, tech_stack, email, github)
SELECT 1, '博主', '用文字记录思考，用代码改变世界', 'Java,SpringBoot,Vue,MySQL', '', ''
WHERE NOT EXISTS (SELECT 1 FROM `profile`);
