-- Script SQL complet : Modèle Physique de Données (MPD) pour Pay My Buddy

-- Table des utilisateurs
CREATE TABLE user (
                      user_id INT AUTO_INCREMENT PRIMARY KEY,
                      email VARCHAR(100) NOT NULL UNIQUE,
                      password VARCHAR(255) NOT NULL,
                      first_name VARCHAR(50),
                      last_name VARCHAR(50),
                      balance DECIMAL(10,2) DEFAULT 0.00,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des comptes bancaires des utilisateurs
CREATE TABLE bank_account (
                              bank_account_id INT AUTO_INCREMENT PRIMARY KEY,
                              user_id INT NOT NULL,
                              bank_name VARCHAR(100) NOT NULL,
                              iban VARCHAR(34) NOT NULL,
                              bic VARCHAR(11),
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- Table des transactions (transferts entre utilisateurs)
CREATE TABLE transaction (
                             transaction_id INT AUTO_INCREMENT PRIMARY KEY,
                             sender_id INT NOT NULL,
                             receiver_id INT NOT NULL,
                             amount DECIMAL(10,2) NOT NULL,
                             description VARCHAR(255),
                             fee DECIMAL(10,2) DEFAULT 0.00,
                             transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (sender_id) REFERENCES user(user_id),
                             FOREIGN KEY (receiver_id) REFERENCES user(user_id)
);

-- Table des dépôts sur le compte Pay My Buddy
CREATE TABLE deposit (
                         deposit_id INT AUTO_INCREMENT PRIMARY KEY,
                         user_id INT NOT NULL,
                         amount DECIMAL(10,2) NOT NULL,
                         deposit_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- Table des retraits vers le compte bancaire
CREATE TABLE withdrawal (
                            withdrawal_id INT AUTO_INCREMENT PRIMARY KEY,
                            user_id INT NOT NULL,
                            bank_account_id INT NOT NULL,
                            amount DECIMAL(10,2) NOT NULL,
                            withdrawal_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES user(user_id),
                            FOREIGN KEY (bank_account_id) REFERENCES bank_account(bank_account_id)
);

-- Table des connexions (amis)
CREATE TABLE connection (
                            connection_id INT AUTO_INCREMENT PRIMARY KEY,
                            user_id INT NOT NULL,
                            friend_id INT NOT NULL,
                            connected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES user(user_id),
                            FOREIGN KEY (friend_id) REFERENCES user(user_id),
                            UNIQUE KEY unique_connection (user_id, friend_id)
);

-- Index supplémentaires pour optimisation (optionnel)
CREATE INDEX idx_transaction_date ON transaction(transaction_date);
CREATE INDEX idx_deposit_user_id ON deposit(user_id);
CREATE INDEX idx_withdrawal_user_id ON withdrawal(user_id);
