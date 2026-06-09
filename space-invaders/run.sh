#!/bin/bash
# ─────────────────────────────────────────────────────────────────
#  Space Invaders — Build & Run Script (Linux / macOS)
# ─────────────────────────────────────────────────────────────────
#  Requirements: Java 8+ (JDK, not just JRE)
#  Usage:  chmod +x run.sh && ./run.sh
# ─────────────────────────────────────────────────────────────────

set -e

SRC_DIR="src"
OUT_DIR="out"
MAIN_CLASS="main.Game"

echo "───────────────────────────────────────────────"
echo "  SPACE INVADERS — Build & Run"
echo "───────────────────────────────────────────────"

# Check Java
if ! command -v javac &>/dev/null; then
    echo "ERROR: javac not found."
    echo "Please install a Java Development Kit (JDK) 8 or newer."
    echo "  Ubuntu/Debian: sudo apt install default-jdk"
    echo "  macOS:         brew install openjdk"
    exit 1
fi

JAVA_VER=$(javac -version 2>&1 | awk '{print $2}')
echo "  Java version : $JAVA_VER"

# Compile
echo "  Compiling..."
mkdir -p "$OUT_DIR"

find "$SRC_DIR" -name "*.java" > /tmp/si_sources.txt
javac -d "$OUT_DIR" -sourcepath "$SRC_DIR" @/tmp/si_sources.txt

echo "  Build successful!"
echo "───────────────────────────────────────────────"
echo "  Launching game..."
echo ""

java -cp "$OUT_DIR" "$MAIN_CLASS"
