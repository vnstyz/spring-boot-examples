-- 教师表初始数据
INSERT INTO teacher (name, title, department) VALUES ('张三', '教授', '计算机科学学院');
INSERT INTO teacher (name, title, department) VALUES ('李四', '副教授', '数学学院');
INSERT INTO teacher (name, title, department) VALUES ('王五', '讲师', '英语学院');

-- 学生表初始数据
INSERT INTO student (name, gender, major, enrollment_year) VALUES ('赵六', '男', '计算机科学与技术', 2023);
INSERT INTO student (name, gender, major, enrollment_year) VALUES ('钱七', '女', '软件工程', 2023);
INSERT INTO student (name, gender, major, enrollment_year) VALUES ('孙八', '男', '数据科学', 2024);
INSERT INTO student (name, gender, major, enrollment_year) VALUES ('周九', '女', '数学', 2024);

-- 选课表初始数据
INSERT INTO student_course (student_id, teacher_id, course_name, semester, score) VALUES (1, 1, 'Java程序设计', '2024-2025-1', 92);
INSERT INTO student_course (student_id, teacher_id, course_name, semester, score) VALUES (2, 1, 'Java程序设计', '2024-2025-1', 88);
INSERT INTO student_course (student_id, teacher_id, course_name, semester, score) VALUES (1, 2, '高等数学', '2024-2025-1', 78);
INSERT INTO student_course (student_id, teacher_id, course_name, semester, score) VALUES (3, 2, '线性代数', '2024-2025-2', 85);
INSERT INTO student_course (student_id, teacher_id, course_name, semester, score) VALUES (4, 3, '大学英语', '2024-2025-2', NULL);
