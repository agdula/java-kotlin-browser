package com.github.agdula.localsrv

/**
 * @author: an001gd
 * @created: 05/04/2017 12:05
 * @version: 1.0
 */

import me.alexpanov.net.FreePortFinder
import mu.KotlinLogging

import spark.Spark.*

class Server  {

    private val log = KotlinLogging.logger {}

    val port = FreePortFinder.findFreeLocalPort()

    fun run(){
        port(port)
        get("/") { req, res ->
            log.debug { "${req.ip()}: handling request" }
            "<html><head><title>Hello</title></head><script> alert('kuku') </script><body>${System.currentTimeMillis()}</body></html>"
        }
    }



}

