echo "{\"eng\":{\"S\":\"$2\"},\"kor\":{\"S\":\"$1\"}}" > w.json
echo aws dynamodb put-item --table-name hjaem_info_words --item file://w.json
aws dynamodb put-item --table-name hjaem_info_words --item file://w.json
rm w.json
