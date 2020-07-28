FROM ubuntu:latest

USER root

RUN chmod 777 -R /tmp && chmod o+t -R /tmp
RUN apt-get update && apt-get install -y gnupg2

RUN apt install gnupg ca-certificates
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 3FA7E0328081BFF6A14DA29AA6A19B38D3D831EF
RUN echo "deb https://download.mono-project.com/repo/ubuntu stable-bionic main" | tee /etc/apt/sources.list.d/mono-official-stable.list

RUN apt-get -y install software-properties-common nodejs npm && \
 update-alternatives --install /usr/bin/node node /usr/bin/nodejs 10

# Still required for chrome headless
RUN DEBIAN_FRONTEND=noninteractive apt-get -y -q install gconf-service libasound2 libatk1.0-0 libc6 libcairo2 libcups2 libdbus-1-3 \
  libexpat1 libfontconfig1 libgcc1 libgconf-2-4 libgdk-pixbuf2.0-0 libglib2.0-0 libgtk-3-0 libnspr4 \
  libpango-1.0-0 libpangocairo-1.0-0 libstdc++6 libx11-6 libx11-xcb1 libxcb1 libxcomposite1 \
  libxcursor1 libxdamage1 libxext6 libxfixes3 libxi6 libxrandr2 libxrender1 libxss1 libxtst6 \
  ca-certificates fonts-liberation libappindicator1 libnss3 lsb-release xdg-utils wget php7.2 php7.2-curl git

RUN git clone https://github.com/phacility/libphutil.git /opt/libphutil

# Needed for owasp dependency:check
RUN apt-get -y install mono-complete

RUN add-apt-repository ppa:webupd8team/java
RUN apt-get -y update
RUN apt-get -y install openjdk-8-jdk
#RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && apt-get -y install oracle-java8-installer && apt-get -y install oracle-java8-set-default

RUN mkdir /.npm && chmod 777 -R /.npm
RUN mkdir -p /home/jenkins/.m2 && \
  mkdir -p /home/jenkins/.npm && \
  mkdir -p /home/jenkins/.embedmongo/linux

# Jenkins user, map to actual user in ubuntu
RUN useradd -u 112 -g root jenkins && \
    groupadd -g 115 jenkins && \
	groupadd -g 116 docker
	
RUN chown -R jenkins:jenkins /home/jenkins

RUN apt-get install sshpass -y

USER jenkins