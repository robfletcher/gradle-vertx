vertx.createHttpServer().requestHandler { req ->
    req.response.end 'O HAI!'
}.listen(8080)