if [ "$#" -ne 2 ] ; then
  echo "Invalid arguments"
  exit 1
fi

cp -R $1/template $1/$2
echo $2 > $1/$2/index.num
