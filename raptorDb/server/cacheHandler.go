package server

import (
	"io/ioutil"
	"log"
	"net/http"
	"strings"
)

type cacheHandler struct {
	server *Server
}

func (handler *cacheHandler) ServerHTTP(w http.ResponseWriter, r *http.Request) {

	key := strings.Split(r.URL.EscapedPath(), "/")[2]

	if len(key) == 0 {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	m := r.Method

	if m == http.MethodPut {
		b, _ := ioutil.ReadAll(r.Body)

		if len(b) != 0 {
			e := handler.server.cache.Set(key, b)
			if e != nil {
				log.Print(e)
				w.WriteHeader(http.StatusInternalServerError)
			}

		}
		return
	}

	if m == http.MethodGet {
		b, e := handler.server.cache.Get(key)
		if e != nil {
			log.Print(e)
			w.WriteHeader(http.StatusInternalServerError)
			return
		}
		if len(b) == 0 {
			w.WriteHeader(http.StatusNotFound)
			return

		}
		w.Write(b)

	}

	if m == http.MethodDelete {
		e := handler.server.cache.Del(key)

		if e != nil {
			log.Print(e)
			w.WriteHeader(http.StatusInternalServerError)
			return
		}
	}
	w.WriteHeader(http.StatusMethodNotAllowed)

}

func (s *Server) cacheHandler() http.Handler {
	return &cacheHandler{s}
}

func (handler *cacheHandler) ServeHTTP(http.ResponseWriter, *http.Request) {
	panic("implement me")
}
