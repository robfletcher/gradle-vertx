vertx.createHttpServer().requestHandler { req ->
    req.response.end '<!doctype html><html><head><title>Vert.x Test</title></head><body><h1>Vert.x Test</h1></body></html>'
}.listen(8080)