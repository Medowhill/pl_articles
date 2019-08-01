html: index.md
	scala -classpath ../../format Format index
preview: index.md
	scala -classpath ../../format Format index -p
copy:
	cp *_* ../../htmls
deploy:
	../../deploy.sh
all: index.md
	make html && make copy && make deploy
clean:
	rm *_*
