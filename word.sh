echo "{\"eng\":{\"S\":\"$1\"},\"kor\":{\"S\":\"$2\"}}" > w.json
echo aws dynamodb put-item --table-name hjaem_info_words --item file://w.json
aws dynamodb put-item --table-name hjaem_info_words --item file://w.json
rm w.json
