if [ "$#" -ne 3 ] ; then
  echo "Invalid arguments"
  exit 1
fi

mkdir $1/$2
cp template/Makefile $1/$2
cp template/$1.css $1/$2
echo 0 > $1/$2/ver
echo $1 > $1/$2/lang
echo $2 > $1/$2/num
echo $3 > $1/$2/title
touch $1/$2/index.md
