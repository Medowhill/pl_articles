lang=`cat lang`
num=`cat num`
title=`cat title`
ver=$((`cat ver` - 1))

name=${lang}_${num}_${ver}

aws s3 cp $name s3://hjaem.info/articles/$name \
    --content-type text/html \
    --storage-class ONEZONE_IA \
    --acl public-read

aws dynamodb update-item \
    --table-name hjaem_info_articles \
    --key "{\"article_num\":{\"N\":\"${num}\"}}" \
    --expression-attribute-values "{\":latest\":{\"N\":\"${ver}\"},\":title\":{\"S\":\"${title}\"}}" \
    --update-expression "SET ${lang}_latest = :latest, ${lang}_title = :title"
