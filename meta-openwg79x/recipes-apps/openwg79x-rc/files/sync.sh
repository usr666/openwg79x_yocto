#!/bin/sh
# Template: set the values below and chmod +x this file to start it at boot.

SERVER="user@host"
REMOTE_LOGS="/path/to/mower/logs/"
REMOTE_WORKORDERS="/path/to/workorder/"
SSH="ssh -i /data/home/openwg/.ssh/id_ed25519 -o UserKnownHostsFile=/data/home/openwg/.ssh/known_hosts -o BatchMode=yes -o HostKeyAlgorithms=+ssh-rsa -o PubkeyAcceptedAlgorithms=+ssh-rsa"

while true; do
	rsync -rlD --size-only --append-verify --exclude workorder -e "$SSH" /var/log/openwg79x_rc/ "$SERVER:$REMOTE_LOGS"
	rsync -rlD -e "$SSH" "$SERVER:$REMOTE_WORKORDERS" /data/openwg79x_rc/workorder/
	sleep 60
done
