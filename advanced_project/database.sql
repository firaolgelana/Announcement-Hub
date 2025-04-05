CREATE DATABASE IF NOT EXISTS AnnouncementPortal;
USE AnnouncementPortal;

CREATE TABLE IF NOT EXISTS Users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('INSTITUTE', 'JOB_SEEKER') NOT NULL
);

CREATE TABLE IF NOT EXISTS Announcements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    institute_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (institute_id) REFERENCES Users(id)
);

CREATE TABLE IF NOT EXISTS JobSeekerProfiles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    resume TEXT,
    profile_details TEXT,
    FOREIGN KEY (user_id) REFERENCES Users(id)
);


CREATE TABLE IF NOT EXISTS InterviewResults (
    id INT PRIMARY KEY AUTO_INCREMENT,
    jobseeker_id INT,
    result VARCHAR(50),
    comments TEXT,
    FOREIGN KEY (jobseeker_id) REFERENCES Users(id)
);
