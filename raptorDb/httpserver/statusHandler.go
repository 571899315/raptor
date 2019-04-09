package httpserver

import (
	"encoding/json"
	"log"
	"net/http"
)

type statusHandler struct {
	server *HttpServer
}

func (handler *statusHandler) ServerHTTP(w http.ResponseWriter, r *http.Request) {

	m := r.Method

	if m != http.MethodGet {
		w.WriteHeader(http.StatusMethodNotAllowed)
		return

	}
	b, e := json.Marshal(handler.server.cache.GetStat())

	if e != nil {
		log.Print(e)
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	w.Write(b)

}

func (s *HttpServer) statusHandler() http.Handler {
	return &statusHandler{s}
}

func (handler *statusHandler) ServeHTTP(http.ResponseWriter, *http.Request) {
	panic("implement me")
}
