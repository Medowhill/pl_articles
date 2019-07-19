html: index.md
	scala -classpath ../../format Format index -s
preview: index.md
	scala -classpath ../../format Format index -p -s
copy:
	cp *_* ../../htmls
deploy:
	../../deploy.sh
clean:
	rm *_*
