package main

import (
	"github.com/571899315/raptor/raptorDb/cache"
	"github.com/571899315/raptor/raptorDb/httpserver"
)

func main() {
	c := cache.New("inmemory")
	httpserver.New(c).Listen()
}
