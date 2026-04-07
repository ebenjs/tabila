SHELL := /bin/sh

BACKEND_DIR := backend
FRONTEND_DIR := frontend
BACKOFFICE_DIR := backoffice

.PHONY: help back fback all

help:
	@echo "Targets disponibles:"
	@echo "  make back   -> lance uniquement le backend Spring Boot"
	@echo "  make fback  -> lance frontend + backoffice"
	@echo "  make all    -> lance backend + frontend + backoffice"

back:
	mvn -f $(BACKEND_DIR)/pom.xml spring-boot:run

fback:
	@echo "Demarrage frontend (5173) + backoffice (5174)..."
	@npm --prefix $(FRONTEND_DIR) run dev -- --host 0.0.0.0 --port 5173 & \
	pid_front=$$!; \
	npm --prefix $(BACKOFFICE_DIR) run dev -- --host 0.0.0.0 --port 5174 & \
	pid_backoffice=$$!; \
	trap 'kill $$pid_front $$pid_backoffice 2>/dev/null' INT TERM EXIT; \
	wait $$pid_front $$pid_backoffice

all:
	@echo "Demarrage backend + frontend + backoffice..."
	@mvn -f $(BACKEND_DIR)/pom.xml spring-boot:run & \
	pid_backend=$$!; \
	npm --prefix $(FRONTEND_DIR) run dev -- --host 0.0.0.0 --port 5173 & \
	pid_front=$$!; \
	npm --prefix $(BACKOFFICE_DIR) run dev -- --host 0.0.0.0 --port 5174 & \
	pid_backoffice=$$!; \
	trap 'kill $$pid_backend $$pid_front $$pid_backoffice 2>/dev/null' INT TERM EXIT; \
	wait $$pid_backend $$pid_front $$pid_backoffice
