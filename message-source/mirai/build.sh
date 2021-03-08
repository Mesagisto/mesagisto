build(){
	./gradlew clean buildPlugin
	rm -rf packages
	mkdir -p packages
	mv build/mirai/*.mirai2.jar packages/mirai-message-source.mirai2.jar
}
build

