package com.test4x.ss

import com.jayway.jsonpath.JsonPath
import spark.Request
import spark.Spark

class Server {
    var port: Int = 8080
    var routes: List<Route> = emptyList()
}

fun Server.port(port: Int) {
    this.port = port
}

fun Server.path(path: String, block: Route.() -> Unit) {
    val route = Route()
    route.path = path
    route.block()
    this.routes = this.routes + route
}

class Route {
    var path: String = ""
    var method: String = ""
    var matchHandler: Req.() -> Any = { }
    var responseHandler: Req.() -> Any = {}
}

fun Route.method(method: String) {
    this.method = method
}

fun Route.template(method: String) {
    TODO()
}

class Req(val req: Request) {}

fun Req.queryParams(arg: String): List<String> {
    return req.queryParamsValues(arg).toList()
}

fun Req.pathParam(arg: String): String? {
    return req.params(arg)
}

fun Req.body(): ByteArray {
    TODO()
}

fun <T> Req.jsonPath(path: String): T {
    return JsonPath.read<T>(req.body(), path)
}

fun Route.match(block: Req.() -> Any) {
    this.matchHandler = block
}

fun Route.response(block: Req.() -> Any) {
    this.responseHandler = block
}

fun Request.match(route: Route): Boolean {
    if (route.method != this.requestMethod() || route.path != this.pathInfo()) {
        return false
    }
    try {
        val result = route.matchHandler.invoke(Req(this))
        if (result is Boolean && result == false) {
            return false
        }
    } catch (e: Exception) {
        return false
    }
    return true
}

fun server(block: Server.() -> Unit) {
    val server = Server()
    server.block()
    Spark.port(server.port)
    Spark.before("*") { request, response ->
        val route = server.routes.firstOrNull(request::match) ?: run {
            response.status(404)
            return@before
        }
        val resp = route.responseHandler.invoke(Req(request))
        response.header("content-type", "application/json")
        response.body(resp.toString())
    }
}