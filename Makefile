MODS_DIR := $(HOME)/Library/Application Support/PrismLauncher/instances/VanyLLa3d/minecraft/mods

.PHONY: build install

build:
	./gradlew build

install: build
	cp build/libs/ars_nouveau-1.21.11-5.11.2.jar "$(MODS_DIR)/ars_nouveau-1.21.11-5.11.2.jar"
