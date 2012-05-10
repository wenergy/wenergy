#!/bin/sh

# wEnergy VCAP Update Script
# --------------------------

WENERGY_VCAP_HOME="/home/cf/cloudfoundry"
WENERGY_DOMAIN="wenergy-project.de"

# Stop vcap
$WENERGY_VCAP_HOME/vcap/dev_setup/bin/vcap_dev stop

# Get latest source
cd $WENERGY_VCAP/vap/
git pull origin master
git submodule foreach git pull origin master
git submodule update --recursive

# Run setup script again
$WENERGY_VCAP_HOME/vcap/dev_setup/bin/vcap_dev_setup -d $WENERGY_VCAP_HOME -D $WENERGY_DOMAIN

# Start vcap
$WENERGY_VCAP_HOME/vcap/dev_setup/bin/vcap_dev start