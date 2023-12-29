#! /bin/sh
scriptPath="$HOME/.local/bin"
installPath="$HOME/.local/share/ea-cli"

#preparing installdir
mkdir -p $installPath
cp -r * $installPath
javac ui/UI.java
rm -r $installPath/*/*.java

#preparing script
echo -e "#!/bin/sh\ncd $installPath\njava ui.UI" > $scriptPath/ea
chmod +x $scriptPath/ea

echo "Installation succeeded."
