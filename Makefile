.DEFAULT_GOAL: help
.PHONY: help lint test dep dep-dev build deploy

help: ## Displays this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

test: ## Run the tests
	echo "would run the tests"

dep: ## Install all deps using pipenv
	echo "would install dependencies"

build: ## Build project
	echo "would build the project"

run: ## Run the project locally
	echo "would run a dev server"