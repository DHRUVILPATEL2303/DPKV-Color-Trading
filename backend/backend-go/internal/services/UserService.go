package services

import (
	"Color-Trading/backend/backend-go/internal/models"
	"Color-Trading/backend/backend-go/internal/repositories/postgres"
	"errors"
	"log"

	"golang.org/x/crypto/bcrypt"
)

type UserService struct {
	repo postgres.UserRepository
}

func NewUserService(repo postgres.UserRepository) *UserService {
	return &UserService{repo: repo}
}

func (s *UserService) SignUp(email string, password string) error {

	hashed, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		return err
	}
	user := models.User{
		Email:    email,
		Password: string(hashed),
	}

	err = s.repo.CreateUserAccount(user)
	if err != nil {
		log.Println(err)
		return err
	}

	return nil

}

func (s *UserService) Login(email string, password string) (user *models.User, err error) {

	byEmail, err := s.repo.GetUserByEmail(email)
	if err != nil {
		return nil, errors.New("user not found")
	}

	if err = bcrypt.CompareHashAndPassword([]byte(byEmail.Password), []byte(password)); err != nil {
		return nil, errors.New("invalid credentials or wrong password ")
	}

	return byEmail, nil
}
