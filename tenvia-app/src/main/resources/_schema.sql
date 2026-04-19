CREATE TABLE questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    question_text NVARCHAR(1000) NOT NULL,
    correct_letter VARCHAR(1),
    explanation NVARCHAR(2000)
);

CREATE TABLE question_options (
    id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT NOT NULL,
    content NVARCHAR(255),
    letter VARCHAR(1),
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- Index for faster lookups when loading a question
CREATE INDEX idx_question_id ON question_options(question_id);