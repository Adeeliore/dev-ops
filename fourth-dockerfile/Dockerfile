FROM ubuntu:20.04
MAINTAINER Adelya Khalyapova <adelyaflaffy@gmail.com>
ENV DEBIAN_FRONTEND=noninteractive
ENV PGDATA=/var/lib/postgresql/data
RUN apt-get update && apt-get install -y nginx postgresql postgresql-contrib && apt-get clean && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY nginx.conf /etc/nginx/nginx.conf
ADD website.tar.gz /var/www/html/
VOLUME /var/lib/postgresql/data
RUN useradd -ms /bin/bash pguser
USER pguser
EXPOSE 80 5432
CMD service postgresql start && nginx -g 'daemon off;'
