package services

import (
	"Color-Trading/backend/backend-go/internal/models"
	"Color-Trading/backend/backend-go/internal/repositories/postgres"
	"Color-Trading/backend/backend-go/pkg/jwtutils"
	"errors"
	"log"
	"time"

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

func (s *UserService) Login(email string, password string) (*models.User, string, string, error) {

	byEmail, err := s.repo.GetUserByEmail(email)
	if err != nil {
		return nil, "", "", errors.New("user not found")
	}

	if err = bcrypt.CompareHashAndPassword([]byte(byEmail.Password), []byte(password)); err != nil {
		return nil, "", "", errors.New("invalid credentials or wrong password ")
	}

	accessToken, _ := jwtutils.GenerateToken(int(byEmail.Id), byEmail.Email)
	refreshToken, _ := jwtutils.GenerateRefreshToken(int(byEmail.Id))

	expiresAt := time.Now().Add(7 * 24 * time.Hour)
	err = s.repo.SaveRefreshToken(int(byEmail.Id), refreshToken, expiresAt)
	if err != nil {
		return nil, "", "", err
	}

	return byEmail, accessToken, refreshToken, nil

}
func (s *UserService) FindRefreshToken(refreshToken string) (userId int, err error) {
	id, err := s.repo.FindRefreshToken(refreshToken)
	if err != nil {
		return 0, err
	}

	return id, nil
}

func (s *UserService) FindUserByID(id int) (user *models.User, err error) {
	byID, err := s.repo.FindUserByID(id)
	if err != nil {
		return nil, err
	}

	return byID, nil

}
func (s *UserService) DeleteRefreshToken(refreshToken string) (err error) {
	err = s.repo.DeleteRefreshToken(refreshToken)
	if err != nil {
		return err
	}
	return nil
}
