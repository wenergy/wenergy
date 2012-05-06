#!/bin/sh

# wEnergy Backup Script
# Install crontab (sudo crontab -e):
# "@daily /home/cf/backup.sh >/home/cf/backup.log 2>&1"
# Alternatively run using manually sudo

BACKUP_ROOT="/home/cf/backup"
BACKUP_TEMP="/home/cf/backup_temp"
BACKUP_MYSQL="$BACKUP_ROOT/mysql"
BACKUP_VCAP="$BACKUP_ROOT/vcap"
BACKUP_DATE=$(date +%Y-%m-%d_%H-%M-%S)
BACKUP_LOG_FILE="/tmp/innobackupex_stderr"

# Backup mysql
echo "$BACKUP_DATE:\tBacking up mysql"
innobackupex --defaults-file=/etc/mysql/my.cnf --user=root --password=mysql --use-memory=1G --stream=xbstream --compress $BACKUP_TEMP >$BACKUP_MYSQL/mysql.$BACKUP_DATE.xbstream 2>$BACKUP_LOG_FILE
cat $BACKUP_LOG_FILE
rm $BACKUP_LOG_FILE

# Backup vcap config
echo "$BACKUP_DATE:\tBacking up vcap config"
tar -czpf $BACKUP_VCAP/vcap.$BACKUP_DATE.tar.gz /home/cf/cloudfoundry/.deployments/devbox/config

# Remove backups older than 5 days
find $BACKUP_MYSQL -name mysql.\* -ctime +5 -exec rm {} \;
find $BACKUP_VCAP -name vcap.\* -ctime +5 -exec rm {} \;

# Manual commands
#sudo innobackupex --defaults-file=/etc/mysql/my.cnf --user=root --password=mysql --use-memory=1G $BACKUP_MYSQL
#sudo innobackupex --apply-log --use-memory=1G /home/cf/backup/mysql/<backup>