#!/bin/sh

# wEnergy Startup Script
# Install crontab (sudo crontab -e):
# "@reboot /home/cf/startup.sh >/home/cf/startup.log 2>&1"
# Alternatively run using manually sudo

SCRIPT="/home/cf/cloudfoundry/vcap/dev_setup/bin/vcap_dev start"

sudo -u cf $SCRIPT