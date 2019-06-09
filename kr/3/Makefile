html: index.md
	scala -classpath ../../format Format index
preview: index.md
	scala -classpath ../../format Format index -p
copy:
	cp *_* ../../htmls
deploy:
	../../deploy.sh
clean:
	rm *_*
