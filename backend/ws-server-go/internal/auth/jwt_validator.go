package auth

import (
	"errors"

	"github.com/golang-jwt/jwt/v5"
)

var secret = []byte("your_secret_key")

func ValidateToken(tokenStr string) (int, string, error) {

	claims := jwt.MapClaims{}

	token, err := jwt.ParseWithClaims(tokenStr, claims, func(token *jwt.Token) (interface{}, error) {

		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, errors.New("invalid signing method")
		}

		return secret, nil
	})

	if err != nil {
		return 0, "", err
	}

	if !token.Valid {
		return 0, "", errors.New("invalid token")
	}

	userIDFloat, ok := claims["user_id"].(float64)
	if !ok {
		return 0, "", errors.New("invalid user_id")
	}

	email, ok := claims["email"].(string)
	if !ok {
		return 0, "", errors.New("invalid email")
	}

	return int(userIDFloat), email, nil
}
