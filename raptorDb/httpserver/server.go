package httpserver

import (
	"github.com/571899315/raptor/raptorDb/cache"
	"log"
	"net/http"
)

type Server struct {
	cache cache.Cache
}

func (server *Server) Listen() {

	http.Handle("/cache/", server.cacheHandler())
	http.Handle("/status/", server.statusHandler())
	http.ListenAndServe(":12345", nil)

	log.Print("server start successful")

}

func New(cache cache.Cache) *Server {
	return &Server{cache}
}
