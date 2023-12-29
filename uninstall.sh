#! /bin/sh
scriptPath="$HOME/.local/bin"
installPath="/opt/ea-cli"

rm -f $scriptPath/ea
sudo rm -rf $installPath

echo "Succesfully uninstalled."
