#!/bin/sh
script="$HOME/.local/bin/ea"
installPath="$HOME/.local/share/ea-cli"
tmpdir=/tmp/ea-cli

#compiling to tmp dir
mkdir -p $tmpdir
cp -r * $tmpdir
cd $tmpdir
javac ui/UI.java
rm -r */*.java

#preparing installdir
mkdir -p $installPath/conf
cp conf/ea-cli_ascii.logo $installPath/conf/ 
touch $installPath/conf/credentials.json
jar cfm $installPath/ea-cli.jar Manifest.txt *
rm -r $tmpdir

#preparing script
echo -e "#!/bin/sh\ncd $installPath\njava -jar ea-cli.jar" > $script
chmod +x $script

echo "Installation done."
