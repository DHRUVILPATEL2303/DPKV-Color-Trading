package postgres

import (
	"Color-Trading/backend/backend-go/internal/models"
	"database/sql"
	"time"
)

type userRepository struct {
	db *sql.DB
}

func (u *userRepository) FindUserByID(userID int) (*models.User, error) {
	var user models.User
	err := u.db.QueryRow("SELECT id,email,password,balance FROM users WHERE id = $1", userID).Scan(&user.Id, &user.Email, &user.Password, &user.Balance)
	if err != nil {
		return nil, err
	}
	return &user, nil
}

func NewUserRepository(db *sql.DB) UserRepository {
	return &userRepository{
		db: db,
	}
}

type UserRepository interface {
	CreateUserAccount(user models.User) error
	GetUserByEmail(email string) (user *models.User, err error)
	SaveRefreshToken(userId int, refreshToken string, expiredAt time.Time) error
	FindRefreshToken(refreshToken string) (int, error)
	DeleteRefreshToken(token int) error
	FindUserByID(userID int) (*models.User, error)
}

func (u *userRepository) SaveRefreshToken(userId int, refreshToken string, expiredAt time.Time) error {
	_, err := u.db.Exec("INSERT INTO refresh_tokens (user_id,token,expires_at) VALUES ($1,$2,$3)", userId, refreshToken, expiredAt)
	if err != nil {
		return err
	}

	return nil
}

func (u *userRepository) FindRefreshToken(refreshToken string) (int, error) {
	var userId int

	err := u.db.QueryRow("SELECT user_id FROM refresh_tokens WHERE token = $1", refreshToken).Scan(&userId)
	if err != nil {
		return 0, err
	}

	return userId, nil
}

func (u *userRepository) DeleteRefreshToken(token int) error {
	_, err := u.db.Exec("DELETE FROM refresh_tokens WHERE token = $1", token)
	if err != nil {
		return err
	}
	return nil
}

func (u *userRepository) GetUserByEmail(email string) (*models.User, error) {
	user := &models.User{}
	err := u.db.QueryRow(`SELECT id, email, password, balance FROM users WHERE email = $1`, email).Scan(&user.Id, &user.Email, &user.Password, &user.Balance)
	if err != nil {
		return nil, err
	}

	return user, nil

}

func (u *userRepository) CreateUserAccount(user models.User) error {
	_, err := u.db.Exec("INSERT INTO users(email,password ) VALUES ($1,$2)", user.Email, user.Password)
	if err != nil {
		return err
	}

	return nil

}
