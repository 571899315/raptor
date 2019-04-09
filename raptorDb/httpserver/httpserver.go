package httpserver

import (
	"github.com/571899315/raptor/raptorDb/cache"
	"log"
	"net/http"
)

type HttpServer struct {
	cache cache.Cache
}

func (server *HttpServer) Listen() {

	http.Handle("/cache/", server.cacheHandler())
	http.Handle("/status/", server.statusHandler())
	http.ListenAndServe(":12345", nil)

	log.Print("server start successful")

}

func New(cache cache.Cache) *HttpServer {
	return &HttpServer{cache}
}
