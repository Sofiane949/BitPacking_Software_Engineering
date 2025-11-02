JCC = javac
JVM = java
JAR = jar

JAR_FILE = Compression.jar
MAIN_CLASS = Main

SRC_DIR = src
TEST_DIR = test
BUILD_DIR = build

LIB_DIR = lib
JUNIT_JAR = $(LIB_DIR)/junit-platform-console-standalone-1.14.1.jar

SOURCES = $(wildcard $(SRC_DIR)/*.java)
TEST_SOURCES = $(wildcard $(TEST_DIR)/*.java)

CP = $(BUILD_DIR)
TEST_CP = $(BUILD_DIR):$(JUNIT_JAR)

all: $(JAR_FILE)

$(JAR_FILE): compile
	$(JAR) cfe $(JAR_FILE) $(MAIN_CLASS) -C $(BUILD_DIR) .
	@echo "Usage: java -jar $(JAR_FILE) <TYPE> <fichier_entree>"

compile: $(SOURCES)
	mkdir -p $(BUILD_DIR)
	$(JCC) -d $(BUILD_DIR) $(SOURCES)

run: compile
	$(JVM) -cp $(CP) $(MAIN_CLASS)

test: compile-tests
	@echo "--- Lancement des tests JUnit ---"
	@if [ ! -f $(JUNIT_JAR) ]; then \
		echo "Erreur: $(JUNIT_JAR) introuvable (attendu dans $(LIB_DIR)/)"; \
		exit 1; \
	fi
	$(JVM) -jar $(JUNIT_JAR) execute --class-path $(TEST_CP) --scan-class-path

compile-tests: compile $(TEST_SOURCES)
	$(JCC) -d $(BUILD_DIR) -cp $(TEST_CP) $(TEST_SOURCES)

# Nettoyage
clean:
	@echo "--- Nettoyage ---"
	rm -rf $(BUILD_DIR)
	rm -f $(JAR_FILE)

# Cibles non-fichiers
.PHONY: all run test compile compile-tests clean
