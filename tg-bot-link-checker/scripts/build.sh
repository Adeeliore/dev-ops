#!/usr/bin/env bash
set -e

usage() {
  echo "Использование: $0 -t <tag>"
  echo "  -t <tag>  — тег Docker-образов (например, 1.0.0 или latest)"
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
shift $((OPTIND -1))

if [[ -z "${TAG}" ]]; then
  echo "Ошибка: нужно указать тег через -t"
  usage
fi

echo "=== Сборка всех модулей через Maven ==="

mvn clean package -DskipTests

cd "$(dirname "$0")/../docker"

echo "=== Сборка Docker-образов Java-сервисов ==="
echo "Собираем образ bot:${TAG}"
docker build \
  --build-arg MAVEN_ARGS="-DskipTests" \
  -f Dockerfile-bot \
  -t tg-bot-link-checker-bot:${TAG} \
  ..

echo "Собираем образ scrapper:${TAG}"
docker build \
  --build-arg MAVEN_ARGS="-DskipTests" \
  -f Dockerfile-scrapper \
  -t tg-bot-link-checker-scrapper:${TAG} \
  ..

echo "=== Сборка Grafana ==="
docker build \
  -f grafana/Dockerfile-grafana \
  -t tg-bot-link-checker-grafana:${TAG} \
  grafana

echo "Успешно собраны образы с тегом ${TAG}"
