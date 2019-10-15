cd `dirname $0`
JQ="jq-linux64"
if [ $(uname) == "Darwin" ]
then
  JQ="jq-osx-amd64"
fi

DST_SCRIPT=./dmp-import
rm -rf $DST_SCRIPT
cat source.sh $JQ  > $DST_SCRIPT
chmod +x $DST_SCRIPT