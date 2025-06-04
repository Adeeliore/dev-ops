#!/bin/bash

LOGFILE="./setup_dev_group.log"

log() {
  echo "$(date '+%Y-%m-%d %H:%M:%S') $1" | tee -a "$LOGFILE"
}

while getopts "d:" opt; do
  case $opt in
    d) BASE_DIR="$OPTARG" ;;
    *) echo "Usage: $0 [-d base_directory]"; exit 1 ;;
  esac
done

if [ -z "$BASE_DIR" ]; then
  read -p "Введите путь до базовой директории для workdir: " BASE_DIR
fi

if [ ! -d "$BASE_DIR" ]; then
  log "Создание директории $BASE_DIR"
  mkdir -p "$BASE_DIR"
fi

if getent group dev > /dev/null; then
  log "Группа dev уже существует"
else
  log "Создание группы dev"
  groupadd dev
fi


log "Поиск всех не системных пользователей (UID >= 1000, исключая nobody)"

USERS=$(awk -F: '$3 >= 1000 && $1 != "nobody" {print $1}' /etc/passwd)

for user in $USERS; do
  log "Добавление пользователя $user в группу dev"
  usermod -aG dev "$user"
done


SUDO_FILE="/etc/sudoers.d/dev-nopasswd"
if [ ! -f "$SUDO_FILE" ]; then
  log "Настройка sudo без пароля для группы dev"
  echo "%dev ALL=(ALL) NOPASSWD: ALL" > "$SUDO_FILE"
  chmod 440 "$SUDO_FILE"
else
  log "Файл sudoers уже настроен: $SUDO_FILE"
fi


for user in $USERS; do
  WORKDIR="$BASE_DIR/${user}_workdir"

  if [ ! -d "$WORKDIR" ]; then
    log "Создание директории $WORKDIR"
    mkdir -p "$WORKDIR"
    chown "$user":"$user" "$WORKDIR"
    chmod 660 "$WORKDIR"
    setfacl -m g:dev:r "$WORKDIR"
  else
    log "Директория $WORKDIR уже существует"
  fi
done

log "Скрипт завершен успешно"
