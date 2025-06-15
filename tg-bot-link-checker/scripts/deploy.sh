#!/usr/bin/env bash
set -e

usage() {
  echo "Использование: $0 -t <tag>"
  echo "  -t <tag>  — тот же тег, который вы использовали в build.sh"
  exit 1
}

TAG=""
while getopts "t:" opt; do
  case ${opt} in
    t)
      TAG=${OPTARG}
      ;;
    *)
      usage
      ;;
  esac
done
shift $((OPTIND - 1))

if [[ -z "${TAG}" ]]; then
  echo "❌ Ошибка: нужно указать тег через -t"
  usage
fi

echo "🔍 Проверка локальных образов с тегом ${TAG}"
for image in tg-bot-link-checker-bot:${TAG} tg-bot-link-checker-scrapper:${TAG} tg-bot-link-checker-grafana:${TAG}; do
  if ! docker image inspect "$image" > /dev/null 2>&1; then
    echo "❌ Образ $image не найден локально. Запустите build.sh с тегом ${TAG}."
    exit 1
  fi
  echo "✅ Образ $image найден локально."
done

echo "📦 Загрузка внешних образов"
docker pull postgres:15-alpine || { echo "❌ Ошибка загрузки postgres:15-alpine"; exit 1; }
docker pull confluentinc/cp-zookeeper:7.5.0 || { echo "❌ Ошибка загрузки cp-zookeeper"; exit 1; }
docker pull confluentinc/cp-kafka:7.5.0 || { echo "❌ Ошибка загрузки cp-kafka"; exit 1; }
docker pull redis:7-alpine || { echo "❌ Ошибка загрузки redis"; exit 1; }
docker pull clickhouse/clickhouse-server:23.8 || { echo "❌ Ошибка загрузки clickhouse-server"; exit 1; }
docker pull timberio/vector:0.47.0-debian || { echo "❌ Ошибка загрузки vector"; exit 1; }

echo "🔍 Проверка конфигурации Vector"
VECTOR_CONFIG="./docker/vector/vector.toml"
if [[ ! -f "$VECTOR_CONFIG" ]]; then
  echo "❌ Конфигурационный файл Vector не найден: $VECTOR_CONFIG"
  exit 1
fi

echo "🚀 Поднимаем стек docker-compose с тегом образов ${TAG}"
export TAG=${TAG}
DEPLOY_DIR="$(cd "$(dirname "$0")/../docker" && pwd)"
cd "$DEPLOY_DIR"

if ! command -v docker-compose &>/dev/null; then
  echo "❌ docker-compose не установлен. Установите его или используйте 'docker compose'."
  exit 1
fi

docker-compose down --remove-orphans
docker-compose up -d --no-build

echo "⏳ Ждем запуска ClickHouse..."
until docker exec clickhouse clickhouse-client -q "SELECT 1" > /dev/null 2>&1; do
  echo "⏳ Ожидание запуска ClickHouse..."
  sleep 2
done

echo "🗂 Выполняем SQL-скрипт создания БД и таблиц"
docker exec -i clickhouse clickhouse-client --multiquery < ./clickhouse-init/create_logs_table.sql

echo "✅ Все сервисы запущены!"
echo "🔗 Grafana: http://localhost:3000 (логин/пароль admin/changeme)"
