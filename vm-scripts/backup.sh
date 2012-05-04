#!/bin/sh

# wEnergy Backup Script
# ---------------------
# Instructions:
# 1. Install XtraBackup (http://www.percona.com/doc/percona-xtrabackup/index.html)
# 2. Install crontab (sudo crontab -e):
# @daily /home/cf/backup.sh > /home/cf/backup.log

BACKUP_ROOT="/home/cf/backup"
BACKUP_MYSQL="$BACKUP_ROOT/mysql"
BACKUP_VCAP="$BACKUP_ROOT/vcap"
BACKUP_DATE=`date +%Y%m%d%H%M%S`

# Backup mysql
innobackupex --defaults-file=/etc/mysql/my.cnf --user=root --password=mysql --use-memory=1G --stream=tar ./ | gzip -c -9 > $BACKUP_MYSQL/mysql.$BACKUP_DATE.tar.gz

# Backup vcap config
tar -czpf $BACKUP_VCAP/vcap.$BACKUP_DATE.tar.gz /home/cf/cloudfoundry/.deployments/devbox/config

# Remove backups older than 5 days
find $BACKUP_MYSQL -name mysql.\* -ctime +5 -exec rm {} \;
find $BACKUP_VCAP -name vcap.\* -ctime +5 -exec rm {} \;

# Manual commands
#sudo innobackupex --defaults-file=/etc/mysql/my.cnf --user=root --password=mysql --use-memory=1G $BACKUP_MYSQL
#sudo innobackupex --apply-log --use-memory=1G /home/cf/backup/mysql/<backup>