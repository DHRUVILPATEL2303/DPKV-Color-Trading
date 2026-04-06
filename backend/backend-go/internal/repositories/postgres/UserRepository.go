package postgres

import (
	"Color-Trading/backend/backend-go/internal/models"
	"database/sql"
)

type userRepository struct {
	db *sql.DB
}

func NewUserRepository(db *sql.DB) UserRepository {
	return &userRepository{
		db: db,
	}
}

type UserRepository interface {
	CreateUserAccount(user models.User) error
	GetUserByEmail(email string) (user *models.User, err error)
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
