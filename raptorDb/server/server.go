package server

import (
	"github.com/571899315/raptor/raptorDb/cache"
	"net/http"
)

type Server struct {
	cache cache.Cache
}

func (server *Server) Listen() {

	http.Handle("/cache/", server.cacheHandler())

}

func New(cache cache.Cache) *Server {
	return &Server{cache}
}
