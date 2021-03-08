build(){
	rm -rf packages
	mkdir -p packages
	./gradlew clean pkg
	mv build/pkg/*.jar packages/kato.jar
}
build
