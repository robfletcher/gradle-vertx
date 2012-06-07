vertx.createHttpServer().requestHandler { req ->
	def file = req.uri == "/" ? "index.html" : req.uri
	req.response.sendFile "src/integration-test/resources/webapp/$file"
}.listen(8080)